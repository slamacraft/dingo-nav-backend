package com.dingdo.config;

import com.forte.qqrobot.BaseApplication;
import com.forte.qqrobot.SimpleRobotContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

/**
 * 一些声明信息
 *
 * @author slamacraft
 * @Description:
 * @date: 2020/8/9 18:39
 * @since JDK 1.8
 */
@Configuration
public class SimpleRobotConfig {

    private static SimpleRobotContext simpleRobotContext;

    public static void initSimpleRobotContext(Class clazz, String[] args){
        try {
            simpleRobotContext = BaseApplication.runAuto(clazz, args);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Bean
    public SimpleRobotContext simpleRobotContext () throws IOException {
        return simpleRobotContext;
    }

}