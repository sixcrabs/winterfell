package org.winterfell.shared.as.security.ratelimit;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Optional;

/**
 * <p>
 * .
 * </p>
 *
 * @author Alex
 * @version v1.0 2023/8/2
 */
public final class RateLimitUtil {

    private RateLimitUtil() {
    }

    public static Method getMethod(Class<?> targetClass, String methodName, Object[] arguments) {
        Method[] declaredMethods = targetClass.getDeclaredMethods();
        Optional<Method> methodOpt = Arrays.stream(declaredMethods)
                .filter(method -> method.getParameters().length == arguments.length && method.getName().equals(methodName))
                .findFirst();
        return methodOpt.orElse(null);
    }


}
