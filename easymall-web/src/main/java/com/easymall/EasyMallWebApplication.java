package com.easymall;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

@SpringBootApplication
@ComponentScan(
        basePackages = {"com.easymall"},
        excludeFilters = {
                // 排除 admin 模块的 AccountController（管理员登录）
                @ComponentScan.Filter(
                        type = FilterType.REGEX,
                        pattern = "com\\.easymall\\.controller\\.AccountController"
                ),
                // 排除 admin 模块的 AppConfig（需要管理员配置）
                @ComponentScan.Filter(
                        type = FilterType.REGEX,
                        pattern = "com\\.easymall\\.entity\\.config\\.AppConfig"
                )
        }
)
@MapperScan(basePackages = {"com.easymall.mapper"})
public class EasyMallWebApplication {
    public static void main(String[] args) {
        SpringApplication.run(EasyMallWebApplication.class, args);
        System.out.println("========================================");
        System.out.println("   Easymall 用户端启动成功！");
        System.out.println("   访问端口: http://localhost:6050");
        System.out.println("   API前缀: /api");
        System.out.println("========================================");
    }
}