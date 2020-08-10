package com.example.demo;

import com.example.demo.Listener.ApplicationRunListener;
import com.example.demo.config.SimpleRobotConfig;
import com.example.demo.util.ServiceUtil;
import com.forte.qqrobot.SimpleRobotApplication;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.io.IOException;

@MapperScan(value = "com.example.demo.dao")
@EnableScheduling
@SpringBootApplication
@EnableWebMvc
@SimpleRobotApplication
public class DemoApplication {

	public static void main(String[] args) throws IOException {
		SimpleRobotConfig.initSimpleRobotContext(DemoApplication.class, args);
		ServiceUtil.startMysql();
		SpringApplication sa = new SpringApplication(DemoApplication.class);
		sa.addListeners(new ApplicationRunListener());
		sa.run(DemoApplication.class, args);
	}

}
