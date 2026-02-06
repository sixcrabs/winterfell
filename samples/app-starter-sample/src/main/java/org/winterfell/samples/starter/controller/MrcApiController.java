package org.winterfell.samples.starter.controller;

import org.winterfell.samples.starter.client.TodosClient;
import org.winterfell.samples.starter.client.UsersClient;
import org.winterfell.shared.as.advice.EnableRespAdvice;
import org.winterfell.shared.as.advice.ex.WebApiException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Map;

/**
 * <p>
 * .
 * </p>
 *
 * @author Alex
 * @since 2025/9/25
 */
@EnableRespAdvice
@RestController
@RequestMapping("/mrc/api/v1")
public class MrcApiController {

    @Resource
    UsersClient usersClient;

    @Resource
    TodosClient todosClient;


    @Operation(summary = "演示Mrc 返回 User结构体(来自web)", tags = "Mrc Api")
    @GetMapping("/users/{id}")
    public Map getSomeOne(@PathVariable String id) {
        try {
            return usersClient.getUser(id);
        } catch (Throwable e) {
            throw new WebApiException(e);
        }
    }

    @Operation(summary = "演示Mrc 返回 Todo结构体(来自web)", tags = "Mrc Api")
    @Parameter(name = "id", description = "todo id")
    @GetMapping("/todos/{id}")
    @SecurityRequirements
    public Map getTodo(@PathVariable String id) {
        try {
            return todosClient.todos(id);
        } catch (Throwable e) {
            throw new WebApiException(e);
        }
    }
}
