package com.dingdo.config.configuration;

import org.apache.log4j.DailyRollingFileAppender;
import org.apache.log4j.Priority;
import org.springframework.context.annotation.Configuration;

/**
 * log4j配置类
 */
@Configuration
public class Log4JConfig extends DailyRollingFileAppender {

    @Override
    public boolean isAsSevereAsThreshold(Priority priority) {
        //只判断是否相等，而不判断优先级
        return this.getThreshold().equals(priority);
    }
}
