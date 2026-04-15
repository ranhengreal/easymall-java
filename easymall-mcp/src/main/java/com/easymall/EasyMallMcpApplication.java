package com.easymall;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;

@SpringBootApplication(scanBasePackages = {"com.easymall"},
        exclude = {
                WebMvcAutoConfiguration.class,
                SecurityAutoConfiguration.class,
                DataSourceAutoConfiguration.class
        })
public class EasyMallMcpApplication {
    public static void main(String[] args) {
        SpringApplication.run(EasyMallMcpApplication.class, args);
        System.out.println("========================================");
        System.out.println("   Easymall MCP 启动成功！");
        System.out.println("========================================");
    }
}