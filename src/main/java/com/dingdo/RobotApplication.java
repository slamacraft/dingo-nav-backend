package com.dingdo;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 一些声明信息
 *
 * @author slamacraft
 * @date: 2020/8/10 15:12
 * @since JDK 1.8
 */
@MapperScan(basePackages = {"com.dingdo.**.mapper"})
@SpringBootApplication
public class RobotApplication {

    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication(RobotApplication.class);
        springApplication.run(args);
    }
}
