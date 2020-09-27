package com.dingdo.config.runListener;

import com.dingdo.config.customContext.InstructionMethodContext;
import com.dingdo.util.SpringContextUtils;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;


/**
 * applicationContext启动监听器
 */
public class ApplicationRunListener implements ApplicationListener<ContextRefreshedEvent> {

    private static final Logger logger = Logger.getLogger(ApplicationRunListener.class);

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        logger.info("容器初始化完毕，开始执行自定义初始化");
        SpringContextUtils.setApplicationContext(event.getApplicationContext());
        InstructionMethodContext.setApplicationContext(event.getApplicationContext());
    }
}
