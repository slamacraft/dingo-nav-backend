package com.example.demo.service;

import com.example.demo.Component.MsgTypeComponent;
import com.example.demo.enums.ClassicEnum;
import com.example.demo.extendService.MsgExtendService;
import com.example.demo.model.msgFromCQ.ReceiveMsg;
import com.example.demo.model.msgFromCQ.ReplyMsg;
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
     * @param receiveMsg
     * @return
     */
    public ReplyMsg determineUserStatus(ReceiveMsg receiveMsg){
        String msg = receiveMsg.getRaw_message();
        // 功能模式切换
        switch (msgTypeComponent.msgTriger(receiveMsg.getUser_id(), msg)) {
            case 1: {
                ReplyMsg replyMsg = new ReplyMsg();
                replyMsg.setReply("请问有什么需要的吗？");
                return replyMsg;
            }
            case -1: {
                ReplyMsg replyMsg = new ReplyMsg();
                replyMsg.setReply("还有什么问题下次记得问我哦");
                return replyMsg;
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
            extendServiceMap.put(enumByServiceName.getValue(), item);
        }
    }

}
