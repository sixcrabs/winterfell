package org.winterfell.shared.as.openapi.knife4j.filter;

import com.github.xiaoymin.knife4j.extend.filter.BasicFilter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;

/**
 * <p>
 * .
 * </p>
 *
 * @author Alex
 * @since 2025/9/25
 */
public class ProductionSecurityFilter extends BasicFilter implements Filter {

    /***
     * 是否生产环境,如果是生成环境,过滤Swagger的相关资源请求
     */
    private boolean production = false;

    /**
     * 生产环境屏蔽后自定义响应HTTP状态码
     */
    private Integer customCode = 200;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // 判断filterConfig
        Enumeration<String> enumeration = filterConfig.getInitParameterNames();
        // SpringMVC环境中,由此init方法初始化此Filter,SpringBoot环境中则不同
        if (enumeration.hasMoreElements()) {
            setProduction(Boolean.parseBoolean(filterConfig.getInitParameter("production")));
        }
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        if (production) {
            String uri = httpServletRequest.getRequestURI();
            if (!match(uri)) {
                chain.doFilter(request, response);
            } else {
                HttpServletResponse resp = (HttpServletResponse) response;
                resp.setStatus(customCode);
                resp.sendError(customCode, "You do not have permission to access this page");
                // response.setContentType("text/palin;charset=UTF-8");
                // PrintWriter pw=response.getWriter();
                // pw.write("You do not have permission to access this page");
                // pw.flush();
            }
        } else {
            chain.doFilter(request, response);
        }
    }

    public ProductionSecurityFilter(boolean production) {
        this.production = production;
    }

    public ProductionSecurityFilter(boolean production, Integer customCode) {
        this.production = production;
        this.customCode = customCode;
    }

    public ProductionSecurityFilter() {
    }

    public boolean isProduction() {
        return production;
    }

    public void setProduction(boolean production) {
        this.production = production;
    }
}
