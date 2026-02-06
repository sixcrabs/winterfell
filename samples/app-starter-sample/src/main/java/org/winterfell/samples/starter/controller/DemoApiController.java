package org.winterfell.samples.starter.controller;

import org.apache.commons.lang3.RandomUtils;
import org.winterfell.misc.hutool.mini.RandomUtil;
import org.winterfell.samples.starter.domain.Person;
import org.winterfell.samples.starter.ex.MyException;
import org.winterfell.samples.starter.service.DemoService;
import org.winterfell.shared.as.advice.RespAdviceExclude;
import org.winterfell.shared.as.advice.EnableRespAdvice;
import org.winterfell.shared.as.advice.response.Response;
import org.winterfell.shared.as.advice.ex.WebApiException;
import org.winterfell.shared.as.security.aop.MethodLogger;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.winterfell.shared.as.service.ServiceBiFunction;
import org.winterfell.shared.as.service.ServiceManager;

import java.util.Random;


/**
 * <p>
 * .
 * </p>
 *
 * @author Alex
 * @version v1.0 2024/11/18
 */
@EnableRespAdvice
@RestController
@RequestMapping("/demo/api/v1")
@Tag(name = "测试api")
public class DemoApiController {


    @Operation(summary = "演示get请求 返回 字符串 包装")
    @GetMapping("/greeting/{name}")
    @MethodLogger(module = "MY")
    public Response<String> getName(@PathVariable String name) {
        return ServiceManager.call((ServiceBiFunction<DemoService, String, String>) DemoService::getGreeting, name, true);
    }

    @Operation(summary = "演示请求限流， 返回 字符串 (原样)")
    @GetMapping(value = "/buy/ticket", produces = MediaType.APPLICATION_JSON_VALUE)
    public String ticket() {
        return ServiceManager.callBare(DemoService::getTicket, true);
    }

    @Operation(summary = "演示get请求 返回 POJO")
    @GetMapping("/persons/{id}")
    public Response<Person> getPerson(@PathVariable String id) {
        try {
            return ServiceManager.call(DemoService::getPerson, id);
        } catch (Throwable e) {
            throw new WebApiException(e);
        }
    }


    @Operation(summary = "演示不被默认包装返回")
    @GetMapping("/persons/exclude/{myId}")
    @RespAdviceExclude
    public Person getPerson2(@PathVariable String myId) {
        try {
            String name = RandomUtil.randomString(4);
            return ServiceManager.call((DemoService service, String id)-> service.getPerson2(id, name), myId).getData();
        } catch (Throwable e) {
            throw new WebApiException(e);
        }
    }

    @Operation(summary = "演示自定义异常返回")
    @GetMapping("/ex")
    public void ex() {
        throw new MyException("系统异常...");
    }

    @Operation(summary = "演示command返回void")
    @GetMapping("/commit")
    public void commit() {
        System.out.println("commit....");
    }

    @Operation(summary = "演示返回单一值")
    @GetMapping("/detect")
    public boolean detect() {
        return new Random().nextBoolean();
    }


}