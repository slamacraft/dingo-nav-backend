package com.dingdo;

import com.dingdo.Listener.ApplicationRunListener;
import com.dingdo.config.SimpleRobotConfig;
import com.forte.qqrobot.SimpleRobotApplication;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

/**
 * 一些声明信息
 *
 * @author slamacraft
 * @Description:
 * @date: 2020/8/10 15:12
 * @since JDK 1.8
 */
@MapperScan(value = "com.dingdo.dao")
@EnableScheduling
@SpringBootApplication
@EnableWebMvc
@SimpleRobotApplication
public class DingDoRobot {

    public static void main(String[] args) {
        SimpleRobotConfig.initSimpleRobotContext(DingDoRobot.class, args);
        SpringApplication sa = new SpringApplication(DingDoRobot.class);
        sa.addListeners(new ApplicationRunListener());
        sa.run(DingDoRobot.class, args);
    }
}
