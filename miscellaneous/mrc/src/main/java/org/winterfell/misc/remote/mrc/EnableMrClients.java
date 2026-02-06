package org.winterfell.misc.remote.mrc;

import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * <p>
 * 开启 {@link MrClient}
 * </p>
 *
 * @author alex
 * @version v1.0, 2020/4/9
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import(MrClientsRegistrar.class)
public @interface EnableMrClients {

    /**
     * alias for {@link #basePackages()}
     * @return
     */
    @AliasFor("basePackages")
    String[] value() default {};

    /**
     * client 所在的包路径
     * @return
     */
    @AliasFor("value")
    String[] basePackages() default {};

    /**
     * if not empty, disables package scanning
     * 指定具体的 client 类
     * @return
     */
    Class<?>[] clients() default {};

}
