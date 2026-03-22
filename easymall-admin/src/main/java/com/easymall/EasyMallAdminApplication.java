package com.easymall;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages =("com.easymall"))
public class EasyMallAdminApplication {
    public static void main(String[] args) {
        SpringApplication.run(EasyMallAdminApplication.class,args);
    }
}
