package org.winterfell.srpc.starter.processor;

import org.winterfell.misc.srpc.AsyncResultHandler;
import org.winterfell.misc.srpc.RpcException;
import org.winterfell.srpc.starter.config.SrpcProperties;
import org.winterfell.misc.srpc.RpcProviderFactory;
import org.winterfell.srpc.starter.annotation.RpcProvider;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.Map;

/**
 * <p>
 * rpc service bean processor
 * {@link RpcProvider}
 * </p>
 *
 * @author alex
 * @version v1.0, 2019/12/23
 */
public class RpcProviderBeanProcessor implements ApplicationContextAware, InitializingBean, DisposableBean {

    private final SrpcProperties.RpcServer rpcServer;

    private RpcProviderFactory providerFactory;

    private ApplicationContext context;

    public RpcProviderBeanProcessor(SrpcProperties.RpcServer rpcServer) {
        this.rpcServer = rpcServer;
    }


    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        this.context = context;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (rpcServer != null) {
            providerFactory = new RpcProviderFactory()
                    .setCorePoolSize(rpcServer.getCorePoolSize())
                    .setMaxPoolSize(rpcServer.getMaxPoolSize())
                    .setPort(rpcServer.getPort())
                    .setAccessToken(rpcServer.getAccessToken());
            providerFactory.start(new AsyncResultHandler() {
                @Override
                public void complete(Object result) {
                    Map<String, Object> serviceBeanMap = context.getBeansWithAnnotation(RpcProvider.class);
                    if (!serviceBeanMap.isEmpty()) {
                        for (Object serviceBean : serviceBeanMap.values()) {
                            if (serviceBean.getClass().getInterfaces().length == 0) {
                                throw new RpcException("[rpc] @RpcProvider must inherit some interface.");
                            }
                            providerFactory.addService(serviceBean.getClass().getInterfaces()[0].getName(), serviceBean);
                        }
                    }
                }

                @Override
                public void failed(Throwable error) {

                }
            });
        }
    }

    @Override
    public void destroy() throws Exception {
        if (providerFactory != null) {
            providerFactory.stop();
        }
    }


}
