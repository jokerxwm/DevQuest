package com.devquest.question;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableDiscoveryClient
@MapperScan("com.devquest.question.mapper")
@ComponentScan(basePackages = {"com.devquest.question", "com.devquest.common"})
public class QuestionServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(QuestionServiceApplication.class, args);
    }
}