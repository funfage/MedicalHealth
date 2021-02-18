package com.zrf;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author 张润发
 * @date 2021/2/17
 */
@SpringBootApplication
@MapperScan(basePackages = {"com.zrf.mapper"})
@EnableDubbo
public class ErpApplication {
    public static void main(String[] args) {
        SpringApplication.run(ErpApplication.class,args);
        System.out.println("ERP子系统启动成功");
    }
}
