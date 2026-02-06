package org.winterfell.misc.remote.mrc.loadbalance.impl;

import org.winterfell.misc.remote.mrc.loadbalance.AbstractLoadBalancer;
import org.winterfell.misc.remote.mrc.loadbalance.LoadBalancer;
import com.google.auto.service.AutoService;


/**
 * <p>
 * .轮询算法按顺序将请求分发到服务器列表中的每个服务器
 * </p>
 *
 * @author Alex
 * @version v1.0 2025/3/7
 */
@AutoService(LoadBalancer.class)
public class RoundRobinLoadBalancer extends AbstractLoadBalancer {

    public static final String NAME = "RoundRobin";


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
        int index = currentIndex.getAndIncrement() % resources.size();
        return resources.get(index).getUrl();
    }
}
