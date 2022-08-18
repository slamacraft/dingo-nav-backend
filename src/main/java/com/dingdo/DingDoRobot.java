package com.dingdo;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 一些声明信息
 *
 * @author slamacraft
 * @date: 2020/8/10 15:12
 * @since JDK 1.8
 */
@MapperScan(basePackages = {"com.dingdo.**.mapper"})
@EnableScheduling
@SpringBootApplication
public class DingDoRobot {

    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication(DingDoRobot.class);
        springApplication.run(args);
    }
}
