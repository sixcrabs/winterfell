package org.winterfell.misc.srpc;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;


/**
 * <p>
 * rpc server
 * </p>
 *
 * @author alex
 * @version v1.0, 2019/12/19
 */
public class RpcServer {


    private final RpcProviderFactory providerFactory;

    public RpcServer(RpcProviderFactory providerFactory) {
        this.providerFactory = providerFactory;
    }


    /**
     * 启动 rpc server
     *
     * @param asyncResultHandler
     */
    public void start(AsyncResultHandler asyncResultHandler) {
        new Thread(new ServerThread(providerFactory, asyncResultHandler)).start();
    }

    static class ServerThread implements Runnable {

        private RpcProviderFactory providerFactory;

        private AsyncResultHandler asyncResultHandler;

        private volatile boolean toStop = false;

        public ServerThread(RpcProviderFactory providerFactory, AsyncResultHandler asyncResultHandler) {
            this.providerFactory = providerFactory;
            this.asyncResultHandler = asyncResultHandler;
        }

        /**
         * run.
         *
         * @see Thread#run()
         */
        @Override
        public void run() {
            try (ZContext context = new ZContext()) {
                final ZMQ.Socket socket = context.createSocket(SocketType.REP);
                boolean succeed = socket.bind("tcp://*:".concat(String.valueOf(providerFactory.getPort())));
                if (succeed) {
                    asyncResultHandler.complete(String.format("server is started, listen on [%d]", providerFactory.getPort()));
                } else {
                    asyncResultHandler.failed(new RpcException("rpc server error"));
                    return;
                }
                while (!toStop) {
                    if (Thread.currentThread().isInterrupted()) {
                        break;
                    }
                    try {
                        byte[] content = socket.recv(ZMQ.DONTWAIT);
                        if (content != null) {
                            RpcRequest request = (RpcRequest) providerFactory.getSerializer().deserialize(content);
                            providerFactory.getThreadPoolExecutor()
                                    .submit(RpcServerHandler.builder()
                                            .socket(socket)
                                            .request(request)
                                            .providerFactory(providerFactory)
                                            .build());
                        }
                        // fix: set cpu free
                        Thread.sleep(10);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }


        }
    }
}
