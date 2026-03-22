package com.easymall;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages =("com.easymall"))
public class EasyMallMcpApplication {
    public static void main(String[] args) {
        SpringApplication.run(EasyMallMcpApplication.class,args);
    }
}
