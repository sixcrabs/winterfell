package org.winterfell.shared.as.advice;

import org.winterfell.shared.as.advice.ex.ErrorAdvice;
import org.winterfell.shared.as.advice.response.ResponseFactory;
import org.winterfell.shared.as.config.AppCustomProperties;
import org.winterfell.shared.as.config.ResponseProperties;
import org.winterfell.shared.as.advice.ex.WebApiException;
import org.winterfell.shared.as.advice.response.ErrorResponse;
import org.winterfell.shared.as.advice.response.Response;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.core.MethodParameter;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.util.StringUtils;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 全局的响应advice
 * </p>
 *
 * @author alex
 * @version v1.0 2022/4/13
 */
@RestControllerAdvice(annotations = {EnableRespAdvice.class})
@Order(Ordered.HIGHEST_PRECEDENCE)
public class DefinedResponseAdvice implements ResponseBodyAdvice<Object> {


    @Resource
    private AppCustomProperties appCustomProperties;

    @Resource
    private ResponseFactory responseFactory;

    /**
     * Whether this component supports the given controller method return type
     * and the selected {@code HttpMessageConverter} type.
     * 使用{@linkplain  RespAdviceExclude} 注解标注的方法不包装返回对象
     *
     * @param returnType    the return type
     * @param converterType the selected converter type
     * @return {@code true} if {@link #beforeBodyWrite} should be invoked;
     * {@code false} otherwise
     */
    @Override
    public boolean supports(MethodParameter returnType,
                            Class<? extends HttpMessageConverter<?>> converterType) {
        return !returnType.hasMethodAnnotation(RespAdviceExclude.class);
    }

    /**
     * Invoked after an {@code HttpMessageConverter} is selected and just before
     * its write method is invoked.
     *
     * @param body                  the body to be written
     * @param methodParameter       the return type of the controller method
     * @param selectedContentType   the content type selected through content negotiation
     * @param selectedConverterType the converter type selected to write to the response
     * @param request               the current request
     * @param response              the current response
     * @return the body that was passed in or a modified (possibly new) instance
     */
    @Override
    public Object beforeBodyWrite(Object body,
                                  MethodParameter methodParameter,
                                  MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  ServerHttpRequest request,
                                  ServerHttpResponse response) {
        // 以下情形会原样返回：  response entity / 非 jackson2converter(包含返回类型是string情形) / 特定请求头 / response 类型
        if (body == null) {
            return responseFactory.createSuccess(null);
        } else if (!MappingJackson2HttpMessageConverter.class.isAssignableFrom(selectedConverterType)) {
            return body;
        } else if (methodParameter.getParameterType().equals(ResponseEntity.class)) {
            return body;
        } else if (body instanceof Response) {
            return body;
        } else {
            ResponseProperties responseProperties = appCustomProperties.getResp();
            if (responseProperties.getRequestHeadersExclude().stream().anyMatch(header -> request.getHeaders().containsKey(header))) {
                return body;
            }
        }
        return responseFactory.createSuccess(body);
    }

    /**
     * 参数校验异常
     *
     * @param e
     * @return
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Response<Object> methodArgumentNotValidExceptionHandler(MethodArgumentNotValidException e) {
        List<ObjectError> allErrors = e.getBindingResult().getAllErrors();
        return responseFactory.createFail(ErrorResponse.PARAMETERS_INVALID.getCode(),
                allErrors.stream().map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.joining(",")));
    }

    /**
     * 接口层异常
     *
     * @param e
     * @return
     */
    @ExceptionHandler(WebApiException.class)
    public Response<Object> webApiExceptionHandler(WebApiException e) {
        return responseFactory.createFail(e.getCode(), e.getMsg());
    }

    /**
     * 通用的运行时异常
     *
     * @param e
     * @return
     */
    @ExceptionHandler(Throwable.class)
    public Response<Object> rtExceptionHandler(Throwable e) {
        Class<? extends Throwable> clazz = e.getClass();
        ErrorAdvice errorAdvice = clazz.getAnnotation(ErrorAdvice.class);
        if (errorAdvice != null) {
            String throwableMsg = e.getMessage();
            return responseFactory.createFail(errorAdvice.code(), StringUtils.hasLength(throwableMsg) ? throwableMsg : errorAdvice.message());
        }
        return responseFactory.createFail(e.getLocalizedMessage());
    }
}