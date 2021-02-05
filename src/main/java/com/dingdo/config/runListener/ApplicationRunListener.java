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
        System.out.println("=========== 容器初始化完成 ===========");
        SpringContextUtil.setApplicationContext(event.getApplicationContext());
    }
}
