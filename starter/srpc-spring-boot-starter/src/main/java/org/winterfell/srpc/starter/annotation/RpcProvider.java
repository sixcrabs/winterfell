package org.winterfell.srpc.starter.annotation;

import org.springframework.stereotype.Service;

import java.lang.annotation.*;

/**
 * <p>
 * 注解某个 rpc 接口的实现类,以便在 spring 启动时，provideFactory 可以扫描并注册该类
 * </p>
 *
 * @author alex
 * @version v1.0, 2019/12/19
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Service
public @interface RpcProvider {
}
