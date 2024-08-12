package com.tinqinacademy.authentication.rest.configurations;

import com.tinqinacademy.authentication.api.operations.base.AuthenticationMappings;
import com.tinqinacademy.authentication.rest.interceptor.UserSecurityInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class SpringMvcConfig implements WebMvcConfigurer {
    private final UserSecurityInterceptor userSecurityInterceptor;

    @Override
    public void addInterceptors(final InterceptorRegistry registry) {
        registry.addInterceptor(userSecurityInterceptor)
                .addPathPatterns(
                        AuthenticationMappings.CHANGE_PASSWORD,
                        AuthenticationMappings.DEMOTE_USER,
                        AuthenticationMappings.PROMOTE_USER);
    }
}
