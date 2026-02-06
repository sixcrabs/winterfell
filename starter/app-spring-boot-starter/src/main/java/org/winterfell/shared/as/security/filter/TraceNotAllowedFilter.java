package org.winterfell.shared.as.security.filter;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * <p>
 * .默认禁用 TRACE 请求
 * </p>
 *
 * @author Alex
 * @version v1.0 2024/11/18
 */
@Component
public class TraceNotAllowedFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        if ("TRACE".equals(httpRequest.getMethod())) {
            HttpServletResponse httpResponse = (HttpServletResponse) response;
            httpResponse.sendError(HttpStatus.METHOD_NOT_ALLOWED.value());
            return;
        }
        chain.doFilter(request, response);
    }
}
