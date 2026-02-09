package org.winterfell.misc.remote.mrc.loadbalance;

import com.google.common.collect.Lists;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * <p>
 * .
 * </p>
 *
 * @author Alex
 * @version v1.0 2025/3/7
 */
public abstract class AbstractLoadBalancer implements LoadBalancer {

    protected List<Resource> resources;

    protected AtomicInteger currentIndex = new AtomicInteger(0);

    @Override
    public void setResources(Resource... resources) {
        this.resources = Lists.newArrayList(resources);
    }
}
