package org.winterfell.samples.cloud.samples.vertx.config;

import org.winterfell.vertx.boot.annotation.VertxConfigurationProperties;

/**
 * <p>
 * .
 * </p>
 *
 * @author alex
 * @version v1.0
 */
@VertxConfigurationProperties(prefix = "zoo")
public class ZooConfig {

    private String animal = "Monkey";

    /**
     * 启动的实例数
     */
    private int instances = 2;

    public int getInstances() {
        return instances;
    }

    public ZooConfig setInstances(int instances) {
        this.instances = instances;
        return this;
    }

    public String getAnimal() {
        return animal;
    }

    public ZooConfig setAnimal(String animal) {
        this.animal = animal;
        return this;
    }
}
