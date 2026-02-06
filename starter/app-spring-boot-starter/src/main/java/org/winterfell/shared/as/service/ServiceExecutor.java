package org.winterfell.shared.as.service;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

/**
 * <p>
 * service executor
 * </p>
 *
 * @author Alex
 * @since 2025/12/26
 */
@Slf4j
public class ServiceExecutor<T, U, R> {

    private ServiceBiFunction<T, U, R> serviceBiFn;
    private ServiceFunction<T, R> serviceFn;
    private U param;
    private Meta<T> meta;
    // 是否打印日志
    private boolean verbose;


    public ServiceExecutor<T, U, R> setServiceBiFn(ServiceBiFunction<T, U, R> serviceBiFunction) {
        this.serviceBiFn = serviceBiFunction;
        return this;
    }

    public ServiceExecutor<T, U, R> setServiceFn(ServiceFunction<T, R> serviceFunction) {
        this.serviceFn = serviceFunction;
        return this;
    }

    public ServiceExecutor<T, U, R> setParam(U param) {
        this.param = param;
        return this;
    }

    public ServiceExecutor<T, U, R> setMeta(Meta<T> meta) {
        this.meta = meta;
        return this;
    }

    public ServiceExecutor<T, U, R> setVerbose(boolean verbose) {
        this.verbose = verbose;
        return this;
    }

    /**
     * 执行方法
     *
     * @return
     * @throws Exception
     */
    public R execute() throws RuntimeException {
        if (meta == null) {
            throw new RuntimeException("ServiceExecutor.meta is null");
        }
        // Check both functional interfaces
        if (serviceBiFn == null && serviceFn == null) {
            throw new RuntimeException("ServiceExecutor.serviceBiFunction or serviceFunction is null");
        }
        String serviceName = meta.getClazz().getSimpleName();
        String methodName = meta.getMethodName();
        long startTime = System.currentTimeMillis();
        if (verbose) {
            log.info("开始调用：{}的{}方法，参数：{}", serviceName, methodName, param);
        }
        try {
            R result;
            if (Objects.nonNull(serviceBiFn)) {
                result = serviceBiFn.apply(meta.getInstance(), param);
            } else { // serviceFn must be non-null here
                result = serviceFn.apply(meta.getInstance());
            }
            if (verbose) {
                long costTime = System.currentTimeMillis() - startTime;
                log.info("调用成功：{}的{}方法，耗时{}ms，结果：{}", serviceName, methodName, costTime, result);
            }
            return result;
        } catch (Exception e) {
            if (verbose) {
                long costTime = System.currentTimeMillis() - startTime;
                log.error("调用失败：{}的{}方法，耗时{}ms", serviceName, methodName, costTime, e);
            }
            throw new RuntimeException("调用" + serviceName + "的" + methodName + "方法失败：" + e.getMessage());
        }
    }


    @Getter
    public static class Meta<T> {
        // Service的Class
        private Class<T> clazz;
        // Service的实例
        private T instance;
        // Service的Method
        private String methodName;

        public Meta<T> setClazz(Class<T> clazz) {
            this.clazz = clazz;
            return this;
        }

        public Meta<T> setInstance(T instance) {
            this.instance = instance;
            return this;
        }

        public Meta<T> setMethodName(String methodName) {
            this.methodName = methodName;
            return this;
        }
    }

}