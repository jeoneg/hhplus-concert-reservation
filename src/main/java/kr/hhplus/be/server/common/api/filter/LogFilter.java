package kr.hhplus.be.server.common.api.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.util.Arrays;

@Slf4j
public class LogFilter implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;
        HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;
        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(httpRequest);
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(httpResponse);

        String requestURI = requestWrapper.getRequestURI();
        String requestBody = Arrays.toString(requestWrapper.getContentAsByteArray());
        log.info("REQUEST [{}][{}]", requestURI, requestBody);

        filterChain.doFilter(servletRequest, servletResponse);

        String responseBody = Arrays.toString(responseWrapper.getContentAsByteArray());
        log.info("RESPONSE [{}][{}]", requestURI, responseBody);
    }

}
