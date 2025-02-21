package kr.hhplus.be.server.config;

import kr.hhplus.be.server.common.api.filter.LogFilter;
import kr.hhplus.be.server.common.api.interceptor.WaitingQueueInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final WaitingQueueInterceptor waitingQueueInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(waitingQueueInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/api/v1/waiting-queues/**")
                .excludePathPatterns("/api/v1/points/**");
    }

    @Bean
    public FilterRegistrationBean<LogFilter> logFilter() {
        FilterRegistrationBean<LogFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new LogFilter());
        registration.setOrder(1);
        registration.addUrlPatterns("/*");
        return registration;
    }

}
