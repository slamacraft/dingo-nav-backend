package com.dingdo.config.configuration;

import com.forte.qqrobot.BaseApplication;
import com.forte.qqrobot.BotRuntime;
import com.forte.qqrobot.SimpleRobotContext;
import com.forte.qqrobot.anno.depend.Beans;
import com.forte.qqrobot.beans.function.PathAssembler;
import com.forte.qqrobot.beans.function.VerifyFunction;
import com.forte.qqrobot.bot.BotInfo;
import com.forte.qqrobot.bot.BotManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

/**
 * 一些声明信息
 *
 * @author slamacraft
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

    @Bean
    public BotManager botManager(){
        return BotRuntime.getRuntime().getBotManager();
    }

}
