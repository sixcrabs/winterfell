package org.winterfell.srpc.starter.processor;

import org.winterfell.misc.hutool.mini.MapUtil;
import org.winterfell.misc.hutool.mini.StringUtil;
import org.winterfell.misc.srpc.RpcException;
import org.winterfell.srpc.starter.config.SrpcProperties;
import org.winterfell.srpc.starter.support.NacosNamingService;
import org.winterfell.srpc.starter.support.RpcInstanceMetadataKeys;
import com.alibaba.nacos.api.naming.pojo.Instance;
import org.winterfell.misc.srpc.RpcConsumerFactory;
import org.winterfell.srpc.starter.annotation.RpcInject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessorAdapter;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * <p>
 * rpc inject bean processor
 * {@link RpcInject}
 * </p>
 *
 * @author alex
 * @version v1.0, 2019/12/23
 */
public class RpcInjectBeanProcessor extends InstantiationAwareBeanPostProcessorAdapter implements InitializingBean, DisposableBean, BeanFactoryAware {

    public static final Logger logger = LoggerFactory.getLogger(RpcInjectBeanProcessor.class);

    private final List<SrpcProperties.RpcProvider> providers;

    private final SrpcProperties srpcProperties;

    private final NacosNamingService namingService;

    private Map<String, RpcConsumerFactory> rpcConsumerFactoryRepository = new HashMap<>(2);


    public RpcInjectBeanProcessor(SrpcProperties srpcProperties, NacosNamingService namingService) {
        this.srpcProperties = srpcProperties;
        this.namingService = namingService;
        this.providers = srpcProperties.getProviders();
    }

    @Override
    public Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName) throws BeansException {
        return super.postProcessBeforeInstantiation(beanClass, beanName);
    }

    @SuppressWarnings({"unchecked","rawtypes"})
    @Override
    public boolean postProcessAfterInstantiation(Object bean, String beanName) throws BeansException {

        ReflectionUtils.doWithFields(bean.getClass(), field -> {
            if (field.isAnnotationPresent(RpcInject.class)) {
                Class type = field.getType();
                if (!type.isInterface()) {
                    throw new RpcException("[rpc] the field with annotation `@RpcInject` must be interface.");
                }
                RpcInject annotation = field.getAnnotation(RpcInject.class);
                String providerServiceId = annotation.value();

                if (namingService.available() && !StringUtils.hasText(providerServiceId)) {
                    // 在nacos 可用时，需要明确指定服务名称 (用于寻址)
                    logger.error("[rpc] when retrieve rpc service from nacos server, the service id of [{}] cannot be empty",
                            field.getClass().getSimpleName());
                } else {
                    RpcConsumerFactory consumerFactory = null;

                    if (StringUtil.isBlank(providerServiceId) && !rpcConsumerFactoryRepository.isEmpty()) {
                        // 兼容旧的配置，返回第一个provider
                        consumerFactory = rpcConsumerFactoryRepository.values().stream().findFirst().get();
                    } else if (rpcConsumerFactoryRepository.containsKey(providerServiceId)) {
                        // 如果repository中存在 直接返回
                        consumerFactory = rpcConsumerFactoryRepository.get(providerServiceId);
                    } else {
                        try {
                            // 从注册中心实时获取服务
                            Instance instance = namingService.getHealthyOne(providerServiceId);
                            if (instance != null) {
                                consumerFactory = rpcConsumerFactoryRepository.put(providerServiceId, fromInstance(instance));
                            }
                        } catch (Exception e) {
                            logger.error("[sprc] naming service error: {}", e.getLocalizedMessage());
                        }
                    }

                    if (consumerFactory != null) {
                        Object serviceProxy;
                        try {
                            serviceProxy = consumerFactory.getObject(type);
                        } catch (Exception e) {
                            throw new RpcException(e);
                        }
                        field.setAccessible(true);
                        field.set(bean, serviceProxy);
                        logger.info("[rpc] consumer factory init @RpcInject bean succeed.  bean.field = {}.{}", beanName, field.getName());
                    }
                }
            }
        });

        return super.postProcessAfterInstantiation(bean, beanName);
    }

    @Override
    public void destroy() throws Exception {
        rpcConsumerFactoryRepository.values().forEach(RpcConsumerFactory::destroy);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        // 当注册中心可用时，默认使用注册中心去发现服务，providers被忽略
        if (namingService.available()) {
            logger.info("[rpc] nacos server is available, will retrieve rpc server from that");
        } else {
            if (providers != null) {
                providers.forEach(rpcProvider -> rpcConsumerFactoryRepository.put(rpcProvider.getName(),
                        RpcConsumerFactory.builder()
                                .accessToken(rpcProvider.getAccessToken())
                                .address(rpcProvider.getAddress())
                                .recvTimeout(rpcProvider.getRecvTimeout())
                                .requestTimeout(rpcProvider.getRequestTimeout())
                                .build()));
            }
            if (rpcConsumerFactoryRepository.isEmpty()) {
                logger.warn("[rpc] consumer factory map is empty. should check application settings");
            }
        }
    }


    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
    }

    /**
     * build one consumer factory from instance
     *
     * @param instance
     * @return
     */
    private RpcConsumerFactory fromInstance(Instance instance) {
        Map<String, String> metadata = instance.getMetadata();
        RpcConsumerFactory.Builder builder = RpcConsumerFactory.builder();
        if (metadata.containsKey(RpcInstanceMetadataKeys.ACCESS_TOKEN)) {
            builder.accessToken(metadata.get(RpcInstanceMetadataKeys.ACCESS_TOKEN));
        }
        builder.address(instance.getIp() + ":" + instance.getPort())
                .recvTimeout(metadata.containsKey(RpcInstanceMetadataKeys.RECV_TIME_OUT) ? MapUtil.getInt(metadata, RpcInstanceMetadataKeys.RECV_TIME_OUT) : 30000)
                .requestTimeout(metadata.containsKey(RpcInstanceMetadataKeys.REQ_TIME_OUT) ? MapUtil.getInt(metadata, RpcInstanceMetadataKeys.REQ_TIME_OUT) : 30000);
        return builder.build();

    }

    /**
     * find factory by provider name
     *
     * @param name
     * @return
     */
    @Deprecated
    private RpcConsumerFactory findConsumerFactoryByName(String name) {
        // 找出address
        if (Objects.isNull(providers)) {
            return null;
        }
        SrpcProperties.RpcProvider targetProvider = null;
        for (SrpcProperties.RpcProvider provider : providers) {
            if (name.equalsIgnoreCase(provider.getName())) {
                targetProvider = provider;
            }
        }
        if (targetProvider != null) {
            return rpcConsumerFactoryRepository.get(targetProvider.getName());
        }
        return null;
    }
}
