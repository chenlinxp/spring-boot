package com.bootdo;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;
@EnableTransactionManagement
@ServletComponentScan
@MapperScan({"com.bootdo.*.*.dao" , "com.bootdo.*.dao"})
@SpringBootApplication
public class BootdoApplication {
    public static void main(String[] args) {
        SpringApplication.run(BootdoApplication.class, args);
        System.out.println("ヾ(◍°∇°◍)ﾉﾞ   justdo启动成功      ヾ(◍°∇°◍)ﾉﾞ");
    }
}