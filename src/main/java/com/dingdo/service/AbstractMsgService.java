package com.dingdo.service;

import com.dingdo.Component.MsgTypeComponent;
import com.dingdo.enums.ClassicEnum;
import com.dingdo.extendService.MsgExtendService;

import com.dingdo.model.msgFromMirai.ReqMsg;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

public abstract class AbstractMsgService implements ApplicationContextAware {

    protected static final Map<Double, MsgExtendService> extendServiceMap = new HashedMap();

    @Autowired
    private MsgTypeComponent msgTypeComponent;

    /**
     * 确定用户状态
     * @param reqMsg
     * @return
     */
    public String determineUserStatus(ReqMsg reqMsg){
        String msg = reqMsg.getRawMessage();
        // 功能模式切换
        switch (msgTypeComponent.msgTriger(reqMsg.getUserId(), msg)) {
            case 1: {
                return "请问有什么需要的吗？";
            }
            case -1: {
                return "还有什么问题下次记得问我哦";
            }
        }
        return null;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Map<String, MsgExtendService> beansOfType = applicationContext.getBeansOfType(MsgExtendService.class);
        Collection<MsgExtendService> values = beansOfType.values();
        Iterator<MsgExtendService> iterator = values.iterator();
        for (int i = 0; i < values.size(); i++) {
            MsgExtendService item = iterator.next();
            String simpleName = item.getClass().getSimpleName();
            ClassicEnum enumByServiceName = ClassicEnum.getEnumByServiceName(simpleName);
            if(enumByServiceName != null){
                extendServiceMap.put(enumByServiceName.getValue(), item);
            }
        }
    }

}
