package org.winterfell.misc.remote.mrc.loadbalance;

import org.winterfell.misc.remote.mrc.loadbalance.impl.WeightedRoundRobinLoadBalancer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;

/**
 * <p>
 * 根据用户配置选择合适的负载均衡实现
 * </p>
 *
 * @author Alex
 * @version v1.0 2025/3/7
 */
public class LoadBalancerFactory {

    public static final Map<String, LoadBalancer> REPO = new HashMap<String, LoadBalancer>();

    public static final Logger LOG = LoggerFactory.getLogger(LoadBalancerFactory.class);

    public LoadBalancerFactory() {
        // 加载所有引擎实现
        ServiceLoader<LoadBalancer> loadBalancers = ServiceLoader.load(LoadBalancer.class);
        for (LoadBalancer loadBalancer : loadBalancers) {
            REPO.put(loadBalancer.name(), loadBalancer);
        }
        LOG.info("[mrc] loaded [{}] load balancers", REPO.size());
    }

    public LoadBalancer getLoadBalancer(String name, Resource... resources) {
        LoadBalancer loadBalancer = REPO.get(name);
        loadBalancer.setResources(resources);
        return loadBalancer;
    }

    public LoadBalancer getLoadBalancer(String name, List<Resource> resources) {
        LoadBalancer loadBalancer = REPO.get(name);
        loadBalancer.setResources(resources.toArray(new Resource[]{}));
        return loadBalancer;
    }

    public static void main(String[] args) {
        LoadBalancer loadBalancer = new LoadBalancerFactory().getLoadBalancer(WeightedRoundRobinLoadBalancer.NAME, new Resource().setUrl("server1").setWeight(5), new Resource().setUrl("server2").setWeight(2), new Resource().setUrl("server3").setWeight(1));
        for (int i = 0; i < 10; i++) {
            System.out.println(loadBalancer.getNextResource());
        }
    }


}
