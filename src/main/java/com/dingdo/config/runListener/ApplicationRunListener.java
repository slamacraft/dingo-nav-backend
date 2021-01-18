package com.dingdo.config.runListener;

import com.dingdo.util.SpringContextUtil;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;


/**
 * applicationContext启动监听器
 */
public class ApplicationRunListener implements ApplicationListener<ContextRefreshedEvent> {

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        System.out.println("===========！！！！！！！！！！==========初始化");
        SpringContextUtil.setApplicationContext(event.getApplicationContext());
//        InstructionMethodContext.setApplicationContext(event.getApplicationContext());
    }
}
