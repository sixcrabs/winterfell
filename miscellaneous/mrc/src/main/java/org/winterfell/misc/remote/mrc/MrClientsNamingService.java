package org.winterfell.misc.remote.mrc;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

/**
 * <p>
 * naming service for `MrClient`
 * - 获取nacos服务地址
 * </p>
 *
 * @author alex
 * @version v1.0, 2020/5/22
 */
public class MrClientsNamingService implements EnvironmentAware {

    private Environment environment;

    private NamingService namingService = null;

    private static final String NACOS_SERVER_ADDR = "nacos.discovery.server-addr";

    private static final String SPRING_CLOUD_NACOS_SERVER_ADDR = "spring.cloud.nacos.server-addr";

    private static final Logger log = LoggerFactory.getLogger(MrClientsNamingService.class);

    /**
     * Set the {@code Environment} that this component runs in.
     *
     * @param environment
     */
    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    /**
     * 初始化nacos server等
     */
    private void init() {
        String nacosServerAddr = environment.getProperty(NACOS_SERVER_ADDR);
        if (StringUtils.isEmpty(nacosServerAddr)) {
            nacosServerAddr = environment.getProperty(SPRING_CLOUD_NACOS_SERVER_ADDR);
        }
        if (StringUtils.isEmpty(nacosServerAddr)) {
            nacosServerAddr = environment.getProperty("mrc.nacos-server-addr");
        }
        if (StringUtils.hasText(nacosServerAddr)) {
            try {
                namingService = NamingFactory.createNamingService(nacosServerAddr);
            } catch (NacosException e) {
                log.error("[mr-client] create naming service on [{}] error: {} ", nacosServerAddr, e.getLocalizedMessage());
            }
        } else {
            log.warn("[mr-client] `{}` or `{}`  or `mrc.nacos-server-addr` not found.", NACOS_SERVER_ADDR, SPRING_CLOUD_NACOS_SERVER_ADDR);
        }
    }

    /**
     * 随机获取到一个健康的服务地址
     *
     * @param serviceName
     * @return
     * @throws NacosException
     */
    public String getHealthyUrl(String serviceName) {
        Instance instance = null;
        try {
            instance = namingService.selectOneHealthyInstance(serviceName);
        } catch (NacosException | IllegalStateException | NullPointerException e) {
            log.error(e.getLocalizedMessage());
        }
        if (instance != null) {
            return instance.getIp() + ":" + instance.getPort();
        }
        return "";
    }
}
