package org.winterfell.sample.ddd.interfaces.rest;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.winterfell.sample.ddd.application.service.UserBizService;
import org.winterfell.shared.as.advice.EnableRespAdvice;

/**
 * <p>
 * .
 * </p>
 *
 * @author Alex
 * @since 2025/10/23
 */
@EnableRespAdvice
@RestController
@RequestMapping("/demo/api/v1")
@Tag(name = "DDD Api")
@RequiredArgsConstructor
public class ApiController {

    private final UserBizService userBizService;
}
