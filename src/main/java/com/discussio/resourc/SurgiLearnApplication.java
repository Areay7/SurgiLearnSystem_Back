package com.discussio.resourc;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 外科护理主管护师培训学习系统V1.0
 * 主启动类
 */
@SpringBootApplication
@MapperScan("com.discussio.resourc.mapper")
@EnableScheduling
public class SurgiLearnApplication {
    public static void main(String[] args) {
        SpringApplication.run(SurgiLearnApplication.class, args);
    }
}
