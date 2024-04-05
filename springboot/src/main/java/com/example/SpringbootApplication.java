package com.example;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 *SpringbootApplication启动基类
 * @SpringBootApplication 表示该类为springboot程序的入口程序
 * @MapperScan("com.example.mapper")： 扫描com.example.mapper包下的所有mapper接口注入到spring容器内
 **/
@SpringBootApplication
@MapperScan("com.example.mapper")
public class SpringbootApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringbootApplication.class, args);
    }

}
