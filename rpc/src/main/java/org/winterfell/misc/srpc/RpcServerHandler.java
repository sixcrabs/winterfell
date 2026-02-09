package org.winterfell.misc.srpc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeromq.ZMQ;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * rpc 服务端控制器线程
 * <ul>
 *     <li>解析请求</li>
 *     <li>查找服务</li>
 *     <li>调用服务</li>
 *     <li>返回结果</li>
 * </ul>
 *
 * @author alex
 * @version v1.0 2019/12/19
 */
public class RpcServerHandler implements Runnable {

    private final ZMQ.Socket socket;

    private final RpcRequest request;

    private final RpcProviderFactory providerFactory;

    private final Logger logger = LoggerFactory.getLogger(RpcServerHandler.class);

    private RpcServerHandler(ZMQ.Socket socket, RpcRequest request, RpcProviderFactory providerFactory) {
        this.socket = socket;
        this.request = request;
        this.providerFactory = providerFactory;
    }

    public static Builder builder() {
        return new Builder();
    }

    /**
     * @see Thread#run()
     */
    @Override
    public void run() {

        AtomicBoolean valid = new AtomicBoolean(true);
        String accessToken = providerFactory.getAccessToken();
        if (accessToken != null && !accessToken.isEmpty()) {
            // 验证 token
            valid.getAndSet(accessToken.equals(request.getAccessToken()));
        }
        if (!valid.get()) {
            try {
                socket.send(providerFactory.getSerializer().serialize(RpcResponse.builder()
                        .requestId(request.getId())
                        .errorMsg("[rpc] error code : 403. Access Token not provide or invalid!")
                        .build()), 0);
            } catch (IOException e) {
                logger.error(e.getLocalizedMessage());
            }
        }
        RpcResponse response = providerFactory.invokeService(request);
        try {
            byte[] bytes = providerFactory.getSerializer().serialize(response);
            if (bytes.length > 0) {
                socket.send(bytes, 0);
            }
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage());
        }
    }

    public static class Builder {

        private ZMQ.Socket socket;

        private RpcRequest request;

        private RpcProviderFactory providerFactory;

        private Builder() {}

        public Builder socket(ZMQ.Socket socket) {
            this.socket = socket;
            return this;
        }

        public Builder request(RpcRequest request) {
            this.request = request;
            return this;
        }

        public Builder providerFactory(RpcProviderFactory providerFactory) {
            this.providerFactory = providerFactory;
            return this;
        }

        public RpcServerHandler build() {
            return new RpcServerHandler(this.socket, this.request, this.providerFactory);
        }
    }
}
