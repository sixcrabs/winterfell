package org.winterfell.shared.as.openapi.knife4j;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import com.github.xiaoymin.knife4j.core.conf.ExtensionsConstants;
import com.github.xiaoymin.knife4j.core.util.StrUtil;
import com.github.xiaoymin.knife4j.extend.util.ExtensionUtils;
import com.google.common.collect.Lists;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import org.springdoc.core.customizers.GlobalOperationCustomizer;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.HandlerMethod;
import org.winterfell.shared.as.openapi.OpenApiProperties;

import java.util.Optional;
import java.util.regex.Pattern;

import static org.winterfell.shared.as.openapi.knife4j.Knife4jOpenApiCustomizer.SECURITY_SCHEME_NAME;

/**
 * <p>
 * operation customizer for `knife4j`
 * </p>
 *
 * @author Alex
 * @since 2025/9/25
 */
public class Knife4jOperationCustomizer implements GlobalOperationCustomizer {

    private final OpenApiProperties properties;

    public Knife4jOperationCustomizer(OpenApiProperties properties) {
        this.properties = properties;
    }

    @Override
    public Operation customize(Operation operation, HandlerMethod handlerMethod) {
        // 1. 拿到当前接口的完整路径 匹配正则，追加安全要求
        String[] prefixPaths = handlerMethod.getBeanType().getAnnotation(RequestMapping.class).value();
        String[] paths = Optional
                .ofNullable(handlerMethod.getMethodAnnotation(RequestMapping.class))
                .map(RequestMapping::path)
                .orElseGet(() -> {
                    GetMapping gm = handlerMethod.getMethodAnnotation(GetMapping.class);
                    if (gm != null) {
                        return gm.path();
                    }
                    PostMapping pm = handlerMethod.getMethodAnnotation(PostMapping.class);
                    if (pm != null) {
                        return pm.path();
                    }
                    PutMapping put = handlerMethod.getMethodAnnotation(PutMapping.class);
                    if (put != null) {
                        return put.path();
                    }
                    DeleteMapping dm = handlerMethod.getMethodAnnotation(DeleteMapping.class);
                    return dm == null ? new String[0] : dm.path();
                });
        String prefix = prefixPaths.length > 0 ? prefixPaths[0] : "";
        for (String path : paths) {
            boolean needAuth = Lists.newArrayList(properties.getAuthPaths()).stream()
                    .anyMatch(ant -> Pattern.compile(ant).matcher(prefix.concat(path)).matches());
            if (needAuth) {
                operation.addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME));
                break;
            }
        }
        // 解析支持作者、接口排序
        // https://gitee.com/xiaoym/knife4j/issues/I6FB9I
        ApiOperationSupport operationSupport = AnnotationUtils.findAnnotation(handlerMethod.getMethod(), ApiOperationSupport.class);
        if (operationSupport != null) {
            String author = ExtensionUtils.getAuthors(operationSupport);
            if (StrUtil.isNotBlank(author)) {
                operation.addExtension(ExtensionsConstants.EXTENSION_AUTHOR, author);
            }
            if (operationSupport.order() != 0) {
                operation.addExtension(ExtensionsConstants.EXTENSION_ORDER, operationSupport.order());
            }
        } else {
            // 如果方法级别不存在，再找一次class级别的
            ApiSupport apiSupport = AnnotationUtils.findAnnotation(handlerMethod.getBeanType(), ApiSupport.class);
            if (apiSupport != null) {
                String author = ExtensionUtils.getAuthor(apiSupport);
                if (StrUtil.isNotBlank(author)) {
                    operation.addExtension(ExtensionsConstants.EXTENSION_AUTHOR, author);
                }
                if (apiSupport.order() != 0) {
                    operation.addExtension(ExtensionsConstants.EXTENSION_ORDER, apiSupport.order());
                }
            }
        }
        return operation;
    }
}