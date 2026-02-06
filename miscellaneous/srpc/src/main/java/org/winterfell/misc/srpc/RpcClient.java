package org.winterfell.misc.srpc;

import org.winterfell.misc.srpc.serializer.Serializer;
import org.winterfell.misc.srpc.serializer.SerializerFactory;
import org.winterfell.misc.srpc.serializer.impl.FurySerializer;
import net.jcip.annotations.NotThreadSafe;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import java.util.Objects;

/**
 * rpc client
 * <ul>
 *     <li> connect server</li>
 *     <li>发送请求</li>
 *     <li>接收结果</li>
 * </ul>
 *
 * @author alex
 * @version v1.0 2019/12/19
 */
@NotThreadSafe
public class RpcClient {

    public static final Logger logger = LoggerFactory.getLogger(RpcClient.class);

    private final String address;

    private final int recvTimeout;

    private final int reqTimeout;

    private final Serializer serializer;

    private ZContext context;

    /**
     * pool for socket
     */
    private GenericObjectPool<ZMQ.Socket> socketPool;

    /**
     * pool for poller
     */
    private GenericObjectPool<ZMQ.Poller> pollerPoll;

    private static final int POOL_MAX_SIZE = 16;

    /**
     * msecs, (> 1000!)
     */
    private final static int REQUEST_TIMEOUT = 500000;

    /**
     * 重试次数 Before we abandon
     */
    private final static int REQUEST_RETRIES = 3;


    private RpcClient(String address, int recvTimeout, int reqTimeout, Serializer serializer) {
        this.address = address;
        this.recvTimeout = recvTimeout;
        this.reqTimeout = reqTimeout;
        this.serializer = serializer;
    }

    public static Builder builder(String address) {
        return new Builder(address);
    }

    public static class Builder {
        private Builder(String address) {
            this.address = address;
        }

        private final String address;

        private Integer recvTimeout;

        private Integer reqTimeout;

        private String serializerName = FurySerializer.NAME;

        /**
         * 设置采用序列化实现的名称
         *
         * @param serializerName
         * @return
         */
        public Builder serializerName(String serializerName) {
            this.serializerName = serializerName;
            return this;
        }

        public Builder recvTimeout(int recvTimeout) {
            this.recvTimeout = recvTimeout;
            return this;
        }

        public Builder reqTimeout(int reqTimeout) {
            this.reqTimeout = reqTimeout;
            return this;
        }

        public RpcClient build() {
            return new RpcClient(address, recvTimeout == null ? 3000 : recvTimeout, reqTimeout,
                    SerializerFactory.getInstance().getSerializer(serializerName));
        }

    }

    /**
     * client start
     * - 创建socket 资源池 并初始化
     * - 测试地址连通性
     *
     * @param asyncResultHandler result handler sync
     */
    public void start(AsyncResultHandler<String> asyncResultHandler) {
        try {
            context = new ZContext();

            // 创建 socket 资源池
            GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();
            poolConfig.setMaxTotal(POOL_MAX_SIZE * 2);
            poolConfig.setMaxIdle(POOL_MAX_SIZE);

            socketPool = new GenericObjectPool<>(new BasePooledObjectFactory<ZMQ.Socket>() {
                @Override
                public ZMQ.Socket create() throws Exception {
                    ZMQ.Socket socket = context.createSocket(SocketType.REQ);
                    socket.setReceiveTimeOut(recvTimeout);
                    return socket;
                }

                @Override
                public PooledObject<ZMQ.Socket> wrap(ZMQ.Socket obj) {
                    return new DefaultPooledObject<>(obj);
                }
            }, poolConfig);

            pollerPoll = new GenericObjectPool<>(new BasePooledObjectFactory<ZMQ.Poller>() {
                @Override
                public ZMQ.Poller create() throws Exception {
                    return context.createPoller(1);
                }

                @Override
                public PooledObject<ZMQ.Poller> wrap(ZMQ.Poller obj) {
                    return new DefaultPooledObject<>(obj);
                }
            }, poolConfig);


            for (int i = 0; i < POOL_MAX_SIZE; i++) {
                socketPool.addObject();
                pollerPoll.addObject();
            }

            asyncResultHandler.complete("[rpc-client] client initialized");

        } catch (Exception ex) {
            asyncResultHandler.failed(ex);
        }
    }

    /**
     * call sync
     * - 从资源池中获取到 socket connect,
     * - 发送消息 接收消息（加锁）等
     * - 完成后 disconnect 并且放回到池中
     *
     * @param request
     */
    public synchronized RpcResponse call(RpcRequest request) {
        ZMQ.Socket socket = null;
        ZMQ.Poller poller = null;
        try {
            socket = socketPool.borrowObject();
            socket.setReceiveTimeOut(recvTimeout);
            boolean succeed = socket.connect(address);
            if (succeed) {
                poller = pollerPoll.borrowObject();
                poller.register(socket, ZMQ.Poller.POLLIN);

                int retriesLeft = REQUEST_RETRIES;
                while (retriesLeft > 0 && !Thread.currentThread().isInterrupted()) {
                    socket.send(serializer.serialize(request), ZMQ.DONTWAIT);
                    int expectReply = 1;
                    while (expectReply > 0) {
                        //  Poll socket for a reply, with timeout
                        int rc = poller.poll(reqTimeout);
                        if (rc == -1) {
                            //  Interrupted
                            break;
                        }
                        //  Here we process a server reply and exit our loop if the
                        //  reply is valid. If we didn't a reply we close the client
                        //  socket and resend the request. We try a number of times
                        //  before finally abandoning:
                        if (poller.pollin(0)) {
                            //  We got a reply from the server, must match
                            //  getSequence
                            byte[] reply = socket.recv(0);
                            if (reply != null && reply.length > 0) {
                                retriesLeft = REQUEST_RETRIES;
                                expectReply = 0;
                                Object obj = serializer.deserialize(reply);
                                if (obj instanceof RpcResponse) {
                                    return (RpcResponse) obj;
                                } else {
                                    return noReply(null);
                                }
                            } else {
                                break;
                            }

                        } else if (--retriesLeft == 0) {
                            logger.error("[rpc] server seems to be offline, abandoning");
                            break;
                        } else {
                            // 重试
                            logger.info("[rpc] no response from server, retrying");
                            poller.unregister(socket);
                            context.destroySocket(socket);
                            logger.info("[rpc] reconnecting to server");
                            socket = socketPool.borrowObject();
                            socket.connect(address);
                            poller.register(socket, ZMQ.Poller.POLLIN);
                            socket.send(serializer.serialize(request), ZMQ.DONTWAIT);
                        }
                    }
                }
            } else {
                return noReply("connect to [" + address + "] failed.");
            }

        } catch (Exception e) {
            throw new RpcException(e);
        } finally {
            if (socket != null) {
                if (poller != null) {
                    poller.unregister(socket);
                    pollerPoll.returnObject(poller);
                }
                socket.disconnect(address);
                socketPool.returnObject(socket);
///                context.destroySocket(socket);
            }
        }
        return noReply(null);
    }

    /**
     * no reply
     *
     * @param msg
     * @return
     */
    private RpcResponse noReply(String msg) {
        return RpcResponse.builder()
                .errorMsg("[rpc] " + (msg != null ? msg : "no reply..."))
                .build();
    }

    /**
     * 关闭 context
     */
    public void destroy() {
        if (!Objects.isNull(context)) {
            context.close();
            context.destroy();
        }
    }
}
