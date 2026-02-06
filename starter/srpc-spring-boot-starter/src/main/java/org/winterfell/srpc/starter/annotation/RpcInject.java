package org.winterfell.srpc.starter.annotation;


import org.winterfell.srpc.starter.config.SrpcProperties;

import java.lang.annotation.*;

/**
 * <p>
 * 自动注入 rpc 的接口类并在spring容器内注册
 * - 使用此接口 无需使用 @AutoWried
 * <pre>
 *     @RpcInject("demo-service")
 *     private DemoService demoService;
 * </pre>
 * </p>
 *
 * @author alex
 * @version v1.0, 2019/12/19
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RpcInject {


    /**
     * 明确 provider name，当多个 provider 的场景，默认空表示对应配置中第一个 provider
     * {@link SrpcProperties.RpcProvider}
     * or
     * 使用自动发现模式时，此参数不能为空(对应的注册中心里的服务名称)
     *
     * @return
     */
    String value() default "";

}
