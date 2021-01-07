package com.dingdo.service.components;

import com.dingdo.service.MsgProcessor;
import com.dingdo.service.enums.StrategyEnum;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 具体功能服务的策略模式组件
 */
@Component
public class ServiceStrategist implements ApplicationContextAware {

    private final Map<StrategyEnum, MsgProcessor> msgMap = new HashMap<>();


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Map<String, MsgProcessor> beansOfType = applicationContext.getBeansOfType(MsgProcessor.class);

        for (MsgProcessor msgProcessor : beansOfType.values()) {
            StrategyEnum serviceType = msgProcessor.getType();
            if (serviceType == null) {
                throw new RuntimeException("服务的策略枚举不能为空");
            }
            MsgProcessor registeredService = msgMap.get(serviceType);
            if (registeredService != null) {
                throw new RuntimeException("服务策略冲突" + registeredService.getClass().getSimpleName() +
                        "<>" + msgProcessor.getClass().getSimpleName());
            }

            msgMap.put(msgProcessor.getType(), msgProcessor);
        }
    }
}
