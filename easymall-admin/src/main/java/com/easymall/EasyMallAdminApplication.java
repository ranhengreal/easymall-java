package com.easymall;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.easymall"})
@MapperScan(basePackages = {"com.easymall.mapper"})
public class EasyMallAdminApplication {
    public static void main(String[] args) {
        SpringApplication.run(EasyMallAdminApplication.class, args);
        System.out.println("========================================");
        System.out.println("   Easymall 管理端启动成功！");
        System.out.println("========================================");
    }
}