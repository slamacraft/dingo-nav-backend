package com.dingdo;

import com.dingdo.config.configuration.SimpleRobotConfig;
import com.dingdo.config.runListener.ApplicationRunListener;
import com.forte.qqrobot.SimpleRobotApplication;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

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
@SimpleRobotApplication
@EnableSwagger2
public class DingDoRobot {

    public static void main(String[] args) {
        SimpleRobotConfig.initSimpleRobotContext(DingDoRobot.class, args);
        SpringApplication sa = new SpringApplication(DingDoRobot.class);
        sa.addListeners(new ApplicationRunListener());
        sa.run(DingDoRobot.class, args);
    }
}
