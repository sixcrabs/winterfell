package org.winterfell.samples.javalin.web;

import org.winterfell.samples.javalin.domain.Person;
import org.winterfell.samples.javalin.service.SampleService;
import org.winterfell.starter.javalin.annotation.RequestController;
import org.winterfell.starter.javalin.annotation.RequestMapping;
import org.winterfell.starter.javalin.support.Resp;
import com.google.inject.Inject;
import io.javalin.http.Context;
import io.javalin.http.HandlerType;
import io.javalin.plugin.openapi.annotations.*;

import java.util.List;

/**
 * <p>
 * .
 * </p>
 *
 * @author alex
 * @version v1.0 2022/9/27
 */
@RequestController("/api/v1")
public class SampleRestController {

    private final SampleService sampleService;

    @Inject
    public SampleRestController(SampleService sampleService) {
        this.sampleService = sampleService;
    }

    @OpenApi(
            tags = "测试接口",
            summary = "保存",
            path = "/api/v1/persons",
            requestBody = @OpenApiRequestBody(content = @OpenApiContent(from = Person.class, type = ContentType.JSON), required = true),
            method = HttpMethod.POST)
    @RequestMapping(value = "/persons", method = HandlerType.POST)
    public Person savePerson(Context ctx) {
        return sampleService.save(ctx.bodyAsClass(Person.class));
    }

    @OpenApi(
            tags = "测试接口",
            summary = "列表",
            path = "/api/v1/persons",
            responses = {@OpenApiResponse(status = "200", content = @OpenApiContent(from = Resp.class, type = ContentType.JSON))},
            method = HttpMethod.GET)
    @RequestMapping(value = "/persons", method = HandlerType.GET)
    public List<Person> find(Context context) {
        return sampleService.list();
    }

}
