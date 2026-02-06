package org.winterfell.samples.cloud.samples.vertx.web;

import org.winterfell.misc.hutool.mini.MapUtil;
import org.winterfell.samples.cloud.samples.vertx.service.ZooService;
import org.winterfell.vertx.boot.web.AbstractVertxRoute;
import org.winterfell.vertx.boot.web.RequestMethod;
import org.winterfell.vertx.boot.annotation.VertxHttpRequest;
import org.winterfell.vertx.boot.annotation.VertxRoute;
import com.google.inject.Inject;
import io.vertx.ext.web.RoutingContext;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * .
 * </p>
 *
 * @author Alex
 * @version v1.0 2024/4/26
 */
@VertxRoute
public class SimpleController extends AbstractVertxRoute {

    private final ZooService zooService;

    private int a = 1;

    @Inject
    public SimpleController(ZooService zooService) {
        this.zooService = zooService;
    }

    @VertxHttpRequest("/my")
    public String my() {
        return "alex".concat(String.valueOf(Math.random()));
    }

    @VertxHttpRequest(method = RequestMethod.GET, value = "/visit/:name")
    public String index(RoutingContext context) {
        return zooService.sayHi(param(context.request(), "name"));
    }

    @VertxHttpRequest("/json")
    public Map json() {
        HashMap<String, Object> map = MapUtil.of("foo", a);
        map.put("now", LocalDateTime.now());
        return map;
    }

    @VertxHttpRequest("/add")
    public int add() {
        a += 1;
        return a;
    }
}
