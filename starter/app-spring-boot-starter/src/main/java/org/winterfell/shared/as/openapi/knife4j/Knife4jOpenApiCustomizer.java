package org.winterfell.shared.as.openapi.knife4j;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.winterfell.shared.as.openapi.OpenApiProperties;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import com.github.xiaoymin.knife4j.core.conf.ExtensionsConstants;
import com.github.xiaoymin.knife4j.core.conf.GlobalConstants;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.springdoc.core.SpringDocConfigProperties;
import org.springdoc.core.customizers.GlobalOpenApiCustomizer;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RestController;

import java.lang.annotation.Annotation;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * openapi customizer for `knife4j`
 * </p>
 *
 * @author Alex
 * @since 2025/9/25
 */
@Slf4j
@AllArgsConstructor
public class Knife4jOpenApiCustomizer implements GlobalOpenApiCustomizer {

    final OpenApiProperties knife4jProperties;
    final SpringDocConfigProperties properties;
    public static final String SECURITY_SCHEME_NAME = "Authorization";

    @Override
    public void customise(OpenAPI openApi) {
        log.debug("Knife4j OpenApiCustomizer");
        if (properties.getApiDocs().isEnabled()) {
            OpenApiProperties.DocUiSetting setting = knife4jProperties.getSetting();
            OpenApiProperties.Info info = knife4jProperties.getInfo();
            if (info != null) {
                openApi.info(new Info()
                        .title(info.getTitle())
                        .description(info.getDescription())
                        .version(info.getVersion())
                        .summary(info.getSummary())
                        .contact(info.getContact())
                        .license(info.getLicense())
                        .termsOfService(info.getTermsOfService()));
            }
            // 检查并获取现有的 components，避免覆盖已有配置
            Components components = openApi.getComponents();
            if (components == null) {
                components = new Components();
                openApi.components(components);
            }
            components.addSecuritySchemes(SECURITY_SCHEME_NAME,
                    new SecurityScheme()
                            .type(SecurityScheme.Type.HTTP)
                            .scheme("bearer")
                            .bearerFormat("JWT"));
            OpenApiExtensionResolver openApiExtensionResolver =
                    new OpenApiExtensionResolver(setting, knife4jProperties.getDocuments());
            // 解析初始化
            openApiExtensionResolver.start();
            Map<String, Object> objectMap = new HashMap<>();
            objectMap.put(GlobalConstants.EXTENSION_OPEN_SETTING_NAME, setting);
            objectMap.put(
                    GlobalConstants.EXTENSION_OPEN_MARKDOWN_NAME,
                    openApiExtensionResolver.getMarkdownFiles());
            openApi.addExtension(GlobalConstants.EXTENSION_OPEN_API_NAME, objectMap);
            addOrderExtension(openApi);
        } else {
            log.warn("springdoc.api-docs.enabled is false");
        }
    }

    /**
     * 往OpenAPI内tags字段添加x-order属性
     *
     * @param openApi openApi
     */
    private void addOrderExtension(OpenAPI openApi) {
        if (CollectionUtils.isEmpty(properties.getGroupConfigs())) {
            return;
        }
        // 获取包扫描路径
        Set<String> packagesToScan =
                properties.getGroupConfigs().stream()
                        .map(SpringDocConfigProperties.GroupConfig::getPackagesToScan)
                        .filter(toScan -> !CollectionUtils.isEmpty(toScan))
                        .flatMap(List::stream)
                        .collect(Collectors.toSet());
        if (CollectionUtils.isEmpty(packagesToScan)) {
            return;
        }
        // 扫描包下被ApiSupport注解的RestController Class
        Set<Class<?>> classes =
                packagesToScan.stream()
                        .map(packageToScan -> scanPackageByAnnotation(packageToScan, RestController.class))
                        .flatMap(Set::stream)
                        .filter(clazz -> clazz.isAnnotationPresent(ApiSupport.class))
                        .collect(Collectors.toSet());
        if (!CollectionUtils.isEmpty(classes)) {
            // ApiSupport oder值存入tagSortMap<Tag.name,ApiSupport.order>
            Map<String, Integer> tagOrderMap = new HashMap<>();
            classes.forEach(
                    clazz -> {
                        Tag tag = getTag(clazz);
                        if (Objects.nonNull(tag)) {
                            ApiSupport apiSupport = clazz.getAnnotation(ApiSupport.class);
                            tagOrderMap.putIfAbsent(tag.name(), apiSupport.order());
                        }
                    });
            // 往openApi tags字段添加x-order增强属性
            if (openApi.getTags() != null) {
                openApi
                        .getTags()
                        .forEach(
                                tag -> {
                                    if (tagOrderMap.containsKey(tag.getName())) {
                                        tag.addExtension(
                                                ExtensionsConstants.EXTENSION_ORDER, tagOrderMap.get(tag.getName()));
                                    }
                                });
            }
        }
    }

    private Tag getTag(Class<?> clazz) {
        // 从类上获取
        Tag tag = clazz.getAnnotation(Tag.class);
        if (Objects.isNull(tag)) {
            // 从接口上获取
            Class<?>[] interfaces = clazz.getInterfaces();
            if (ArrayUtils.isNotEmpty(interfaces)) {
                for (Class<?> interfaceClazz : interfaces) {
                    Tag anno = interfaceClazz.getAnnotation(Tag.class);
                    if (Objects.nonNull(anno)) {
                        tag = anno;
                        break;
                    }
                }
            }
        }
        return tag;
    }

    private Set<Class<?>> scanPackageByAnnotation(
            String packageName, final Class<? extends Annotation> annotationClass) {
        ClassPathScanningCandidateComponentProvider scanner =
                new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AnnotationTypeFilter(annotationClass));
        Set<Class<?>> classes = new HashSet<>();
        for (BeanDefinition beanDefinition : scanner.findCandidateComponents(packageName)) {
            try {
                Class<?> clazz = Class.forName(beanDefinition.getBeanClassName());
                classes.add(clazz);
            } catch (ClassNotFoundException ignore) {

            }
        }
        return classes;
    }
}