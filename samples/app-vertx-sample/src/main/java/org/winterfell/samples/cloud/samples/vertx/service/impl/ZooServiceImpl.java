package org.winterfell.samples.cloud.samples.vertx.service.impl;


import org.winterfell.samples.cloud.samples.vertx.config.ZooConfig;
import org.winterfell.samples.cloud.samples.vertx.service.ZooService;
import org.winterfell.vertx.boot.annotation.VertxComponent;
import com.google.inject.Inject;

import java.io.Serializable;

/**
 * <p>
 * .
 * </p>
 *
 * @author alex
 * @version v1.0
 */
@VertxComponent
public class ZooServiceImpl implements Serializable,  ZooService {

    private final ZooConfig config;

    @Inject
    public ZooServiceImpl(ZooConfig config) {
        this.config = config;
    }

    /**
     * .
     *
     * @param name
     * @return
     */
    @Override
    public String sayHi(String name) {
        return String.format("Hello %s, Welcome to Vertx Zoo, Today is %s's day", name, config.getAnimal());
    }
}
