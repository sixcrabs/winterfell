package org.winterfell.srpc.starter.support;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.listener.Event;
import com.alibaba.nacos.api.naming.listener.EventListener;
import com.alibaba.nacos.api.naming.pojo.Instance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>
 * .
 * </p>
 *
 * @author alex
 * @version v1.0, 2020/5/25
 */
public class NacosNamingService implements EnvironmentAware {

    private Environment environment;

    private NamingService namingService = null;

    private boolean available = false;

    private static final String NACOS_SERVER_ADDR = "nacos.discovery.server-addr";

    public static final Logger logger = LoggerFactory.getLogger(NacosNamingService.class);

    /**
     * 记录监听的service
     */
    private Map<String, EventListener> serviceListeners = new ConcurrentHashMap<>(2);

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    /**
     * 初始化nacos server等
     */
    private void init() {
        String nacosServerAddr = resolveNacosServerAddr();
        if (StringUtils.hasText(nacosServerAddr)) {
            try {
                namingService = NamingFactory.createNamingService(nacosServerAddr);
                this.available = true;
            } catch (NacosException e) {
                logger.error("[rpc] create naming service on [{}] error: {} ", nacosServerAddr, e.getLocalizedMessage());
            }
        } else {
            logger.warn("[rpc] `nacos.discovery.server-addr` or `srpc.nacos-server-addr` not found.");
        }
    }

    /**
     * 是否可用
     *
     * @return
     */
    public boolean available() {
        return this.available;
    }

    /**
     * 解析 nacos 服务地址
     *
     * @return
     */
    private String resolveNacosServerAddr() {
        String nacosServerAddr = environment.getProperty(NACOS_SERVER_ADDR);
        if (StringUtils.isEmpty(nacosServerAddr)) {
            nacosServerAddr = environment.getProperty("srpc.nacos-server-addr");
        }
        return nacosServerAddr;
    }

    /**
     * 随机获取到一个健康的服务地址
     *
     * @param serviceName
     * @return
     * @throws NacosException
     */
    public Instance getHealthyOne(String serviceName) {
        Instance instance = null;
        try {
            instance = namingService.selectOneHealthyInstance(serviceName);
        } catch (NacosException | IllegalStateException e) {
            logger.error(e.getLocalizedMessage());
        }
        return instance;
    }

    public NamingService getNamingService() {
        return namingService;
    }

    static class ServiceEventListener implements EventListener {

        /**
         * callback event
         *
         * @param event
         */
        @Override
        public void onEvent(Event event) {
            // TODO


        }
    }
}
