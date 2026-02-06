package org.winterfell.misc.remote.mrc.loadbalance;

/**
 * <p>
 * .
 * </p>
 *
 * @author Alex
 * @version v1.0 2025/3/7
 */
public interface LoadBalancer {

    /**
     *
     * @return
     */
    String name();

    /**
     * 获取下一个资源的url
     *
     * @return
     */
    String getNextResource();

    /**
     * set resources
     * @param resources
     */
    void setResources(Resource... resources);
}
