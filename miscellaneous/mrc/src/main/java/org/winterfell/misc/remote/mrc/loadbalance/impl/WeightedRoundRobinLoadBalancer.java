package org.winterfell.misc.remote.mrc.loadbalance.impl;

import org.winterfell.misc.remote.mrc.loadbalance.AbstractLoadBalancer;
import org.winterfell.misc.remote.mrc.loadbalance.LoadBalancer;
import org.winterfell.misc.remote.mrc.loadbalance.Resource;
import com.google.auto.service.AutoService;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * .加权轮询算法根据服务器的权重将请求分发到服务器。权重较高的服务器接收的请求更多
 * </p>
 *
 * @author Alex
 * @version v1.0 2025/3/7
 */
@AutoService(LoadBalancer.class)
public class WeightedRoundRobinLoadBalancer extends AbstractLoadBalancer {

    private List<String> resourceUrls;

    public static final String NAME = "WeightedRoundRobin";


    @Override
    public void setResources(Resource... resources) {
        super.setResources(resources);
        resourceUrls = buildUrlList();
    }

    /**
     * @return
     */
    @Override
    public String name() {
        return NAME;
    }

    /**
     * 获取下一个资源的url
     *
     * @return
     */
    @Override
    public String getNextResource() {
        int index = currentIndex.getAndIncrement() % resourceUrls.size();
        return resourceUrls.get(index);
    }

    private List<String> buildUrlList() {
        List<String> urls = new ArrayList<>();
        for (Resource resource : resources) {
            for (int i = 0; i < resource.getWeight(); i++) {
                urls.add(resource.getUrl());
            }
        }
        return urls;
    }
}
