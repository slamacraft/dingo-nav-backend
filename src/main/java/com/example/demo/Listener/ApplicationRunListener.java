package com.example.demo.Listener;

import com.example.demo.Component.InstructionMethodContext;
import com.example.demo.util.SpringContextUtils;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

public class ApplicationRunListener implements ApplicationListener<ContextRefreshedEvent> {

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        System.out.println("===========！！！！！！！！！！==========初始化");
        SpringContextUtils.setApplicationContext(event.getApplicationContext());
        InstructionMethodContext.setApplicationContext(event.getApplicationContext());
    }
}
