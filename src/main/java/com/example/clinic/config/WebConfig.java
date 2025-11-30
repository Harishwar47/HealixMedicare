package com.example.clinic.config;

import java.time.Duration;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Cache static resources (css/js/images) for 1 year
        registry.addResourceHandler("/static/**", "/css/**", "/js/**", "/images/**")
                .addResourceLocations("classpath:/static/", "classpath:/static/css/", "classpath:/static/js/", "classpath:/static/images/")
                .setCacheControl(CacheControl.maxAge(Duration.ofDays(365)).cachePublic());
    }
}
