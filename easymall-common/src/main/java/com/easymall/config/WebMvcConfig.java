package com.easymall.config;

import com.easymall.interceptor.AuthInterceptor;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Resource
    private AuthInterceptor authInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/error", "/uploads/**");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 使用正确的完整绝对路径（注意三个斜杠）
        String uploadPath = "file:D:/Java实践项目/workspace-teach/easymall-java/easymall-uploads/";

        System.out.println("========== 静态资源映射配置 ==========");
        System.out.println("资源映射: " + uploadPath);
        System.out.println("=====================================");

        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(uploadPath);
    }
}