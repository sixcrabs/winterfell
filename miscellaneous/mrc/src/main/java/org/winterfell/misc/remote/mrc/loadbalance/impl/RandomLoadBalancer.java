package org.winterfell.misc.remote.mrc.loadbalance.impl;

import org.winterfell.misc.remote.mrc.loadbalance.AbstractLoadBalancer;
import org.winterfell.misc.remote.mrc.loadbalance.LoadBalancer;
import com.google.auto.service.AutoService;

import java.util.Random;

/**
 * <p>
 * .随机算法随机选择一个服务器
 * </p>
 *
 * @author Alex
 * @version v1.0 2025/3/7
 */
@AutoService(LoadBalancer.class)
public class RandomLoadBalancer extends AbstractLoadBalancer {

    private static final Random RANDOM = new Random();

    public static final String NAME = "Random";

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
        int index = RANDOM.nextInt(resources.size());
        return resources.get(index).getUrl();
    }
}
