package org.jboss.arquillian.extension.rest.app;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.HttpMethod;
import java.io.IOException;

@WebFilter(urlPatterns = "/rest/customer/*")
public class SecurityFilter implements Filter {

    @Override
    public void destroy() {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse servletResponse, FilterChain filterChain)
        throws IOException, ServletException {
        final HttpServletRequest httpRequest = (HttpServletRequest) request;
        if (HttpMethod.POST.equals(httpRequest.getMethod()) && httpRequest.getPathInfo().matches("/customer/\\d+")) {
            final String authorization = httpRequest.getHeader("Authorization");
            if (null == authorization || !"abc".equals(authorization)) {
                ((HttpServletResponse) servletResponse).sendError(401);
                return;
            }
        }
        filterChain.doFilter(request, servletResponse);
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }
}
