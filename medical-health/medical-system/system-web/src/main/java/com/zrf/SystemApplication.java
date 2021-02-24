package com.zrf;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;

/**
 * @author 张润发
 * @date 2021/2/17
 */
@SpringBootApplication
@MapperScan(basePackages = {"com.zrf.mapper"})
@EnableDubbo
@EnableHystrix //启用hystrix
@EnableCircuitBreaker //启用hystrix的断路保存
public class SystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(SystemApplication.class,args);
        System.out.println("主系统启动成功");
    }
}