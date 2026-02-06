package org.winterfell.misc.remote.mrc.loadbalance;

import java.io.Serializable;

/**
 * <p>
 * .
 * </p>
 *
 * @author Alex
 * @version v1.0 2025/3/7
 */
public class Resource implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * url
     */
    private String url;

    /**
     * 权重
     */
    private int weight;


    public String getUrl() {
        return url;
    }

    public Resource setUrl(String url) {
        this.url = url;
        return this;
    }

    public int getWeight() {
        return weight;
    }

    public Resource setWeight(int weight) {
        this.weight = weight;
        return this;
    }
}
