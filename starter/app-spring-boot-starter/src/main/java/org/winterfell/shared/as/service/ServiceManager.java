package org.winterfell.shared.as.service;

import lombok.extern.slf4j.Slf4j;
import org.winterfell.shared.as.advice.response.Response;
import org.winterfell.shared.as.advice.response.ResponseFactory;

import java.lang.invoke.SerializedLambda;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>
 * service manager
 * </p>
 *
 * @author Alex
 * @since 2025/12/26
 */
@Slf4j
@SuppressWarnings("unchecked")
public class ServiceManager {

    private static final Map<Object, ServiceExecutor.Meta<?>> META_CACHE;

    static {
        META_CACHE = new ConcurrentHashMap<>(128);
    }

    /**
     * 调用Service, 不进行调用日志输出
     *
     * @param serviceBiFunction
     * @param param
     * @param <T>
     * @param <U>
     * @param <R>
     * @return
     */
    public static <T, U, R> Response<R> call(ServiceBiFunction<T, U, R> serviceBiFunction, U param) {
        return call(serviceBiFunction, param, false);
    }

    public static <T, U, R> R callBare(ServiceBiFunction<T, U, R> serviceBiFunction, U param, boolean verbose) throws RuntimeException {
        if (Objects.isNull(serviceBiFunction)) {
            throw new RuntimeException("serviceBiFunction is null");
        }
        ServiceExecutor.Meta<T> meta = (ServiceExecutor.Meta<T>) META_CACHE.computeIfAbsent(serviceBiFunction, ServiceManager::parseMeta);
        ServiceExecutor<T, U, R> executor = new ServiceExecutor<T, U, R>()
                .setServiceBiFn(serviceBiFunction)
                .setParam(param)
                .setVerbose(verbose)
                .setMeta(meta);
        try {
            return executor.execute();
        } catch (Exception e) {
            log.error("service call error：{}", e.getLocalizedMessage());
            throw new RuntimeException("service call error：" + e.getLocalizedMessage());
        }
    }

    /**
     * 调用Service
     *
     * @param serviceBiFunction
     * @param param
     * @param <T>
     * @param <U>
     * @param <R>
     * @return
     */
    public static <T, U, R> Response<R> call(ServiceBiFunction<T, U, R> serviceBiFunction, U param, boolean verbose) {
        ResponseFactory responseFactory = SpringAppContext.getBean(ResponseFactory.class);
        if (Objects.isNull(serviceBiFunction)) {
            return responseFactory.createFail("serviceBiFunction is null");
        }
        ServiceExecutor.Meta<T> meta = (ServiceExecutor.Meta<T>) META_CACHE.computeIfAbsent(serviceBiFunction, ServiceManager::parseMeta);
        ServiceExecutor<T, U, R> executor = new ServiceExecutor<T, U, R>()
                .setServiceBiFn(serviceBiFunction)
                .setParam(param)
                .setVerbose(verbose)
                .setMeta(meta);
        try {
            R result = executor.execute();
            return responseFactory.createSuccess(result);
        } catch (Exception e) {
            log.error("service call error：{}", e.getLocalizedMessage());
            META_CACHE.remove(serviceBiFunction);
            return responseFactory.createFail("service call error：" + e.getLocalizedMessage());
        }
    }

    public static <T, R> Response<R> call(ServiceFunction<T, R> serviceFunction) {
        return call(serviceFunction, false);
    }

    /**
     * 调用Service (无参数)
     *
     * @param serviceFunction
     * @param verbose
     * @param <T>
     * @param <R>
     * @return
     */
    public static <T, R> R callBare(ServiceFunction<T, R> serviceFunction, boolean verbose) throws RuntimeException {
        if (Objects.isNull(serviceFunction)) {
            throw new RuntimeException("serviceFunction is null");
        }
        ServiceExecutor.Meta<T> meta = (ServiceExecutor.Meta<T>) META_CACHE.computeIfAbsent(serviceFunction, ServiceManager::parseMeta);
        ServiceExecutor<T, Object, R> executor = new ServiceExecutor<T, Object, R>()
                .setServiceFn(serviceFunction)
                .setParam(null)
                .setVerbose(verbose)
                .setMeta(meta);
        try {
            return executor.execute();
        } catch (Exception e) {
            log.error("service call error：{}", e.getLocalizedMessage());
            META_CACHE.remove(serviceFunction);
            throw new RuntimeException("service call error：" + e.getLocalizedMessage());
        }
    }

    /**
     * 调用Service (无参数)
     *
     * @param serviceFunction
     * @param verbose
     * @param <T>
     * @param <R>
     * @return
     */
    public static <T, R> Response<R> call(ServiceFunction<T, R> serviceFunction, boolean verbose) {
        ResponseFactory responseFactory = SpringAppContext.getBean(ResponseFactory.class);
        if (Objects.isNull(serviceFunction)) {
            return responseFactory.createFail("serviceFunction is null");
        }
        ServiceExecutor.Meta<T> meta = (ServiceExecutor.Meta<T>) META_CACHE.computeIfAbsent(serviceFunction, ServiceManager::parseMeta);
        ServiceExecutor<T, Object, R> executor = new ServiceExecutor<T, Object, R>()
                .setServiceFn(serviceFunction)
                .setParam(null)
                .setVerbose(verbose)
                .setMeta(meta);
        try {
            R result = executor.execute();
            return responseFactory.createSuccess(result);
        } catch (Exception e) {
            log.error("service call error：{}", e.getLocalizedMessage());
            META_CACHE.remove(serviceFunction);
            return responseFactory.createFail("service call error：" + e.getLocalizedMessage());
        }
    }

    /**
     * 解析Service元数据
     *
     * @param serviceBiFn
     * @param <T>
     * @return
     */
    private static <T> ServiceExecutor.Meta<T> parseMeta(Object serviceBiFn) {
        // 用LambdaUtil拿到Lambda的元数据
        SerializedLambda lambda = LambdaUtil.of(serviceBiFn);
        ServiceExecutor.Meta<T> lambdaMeta = new ServiceExecutor.Meta<>();
        // 1. 解析Service类名：Lambda里的类名是“com/example/UserService”，要改成“com.example.UserService”
        String tClassName = lambda.getImplClass().replaceAll("/", ".");
        try {
            // 2. 拿到Service的Class对象（比如UserService.class）
            Class<T> aClass = (Class<T>) Class.forName(tClassName);
            // 3. 从Spring里拿Service实例（不用@Autowired就是靠这行）
            T inst = SpringAppContext.getBean(aClass);
            // 4. 把信息存到lambdaMeta里
            lambdaMeta.setClazz(aClass)
                    .setInstance(inst)
                    .setMethodName(lambda.getImplMethodName());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("service class not found：" + tClassName, e);
        }
        return lambdaMeta;
    }
}