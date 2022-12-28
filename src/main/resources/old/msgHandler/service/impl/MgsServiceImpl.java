package com.dingo.msgHandler.service.impl;

import com.dingo.component.otherComponent.Tess4jComponent;
import com.dingo.config.customContext.InstructionMethodContext;
import com.dingo.msgHandler.factory.CQCodeFactory;
import com.dingo.msgHandler.model.CQCode;
import com.dingo.msgHandler.model.ReqMsg;
import com.dingo.msgHandler.service.MsgHandleService;
import com.dingo.msgHandler.service.MsgService;
import com.dingo.util.CQCodeUtil;
import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 消息转发接口的实例
 */
@Service
public class MgsServiceImpl implements MsgService, ApplicationContextAware {

    // 使用log4j打印日志
    private static Logger logger = Logger.getLogger(MgsServiceImpl.class);

    private Map<String, MsgHandleService> msgMap = new HashMap<>();

    private final Tess4jComponent tess4jComponent;
    private final InstructionMethodContext instructionMethodContext;

    @Autowired
    public MgsServiceImpl(InstructionMethodContext instructionMethodContext,
                          Tess4jComponent tess4jComponent) {
        this.instructionMethodContext = instructionMethodContext;
        this.tess4jComponent = tess4jComponent;
    }

    @Override
    public String receive(ReqMsg request) {
        return null;
    }


    @Override
    public String handleMsg(ReqMsg reqMsg) {
        this.extractCQCode(reqMsg); // 提取cq码
        this.msgOCR(reqMsg);    // 识别图中文字
        String instructResult = instructionMethodContext.instructionHandle(reqMsg);
        if (StringUtils.isNotBlank(instructResult)) {    // 如果指令调用服务有返回结果
            return instructResult;
        }

        // 根据请求的类型不同跳转☞不同的service实例的handleMsg方法处理
        return msgMap.get(reqMsg.getMessageType()).handleMsg(reqMsg);
    }


    /**
     * 提取cq码
     *
     * @param reqMsg
     */
    private void extractCQCode(ReqMsg reqMsg) {
        String msg = reqMsg.getMessage();
        List<CQCode> cqCodeList = CQCodeFactory.getCQCodeList(msg);
        reqMsg.setCqCodeList(cqCodeList);
        reqMsg.setRawMessage(CQCodeUtil.removeAllCQCode(msg));
    }


    /**
     * 提取图中文字，保留中文
     *
     * @param reqMsg
     */
    private void msgOCR(ReqMsg reqMsg) {
        String imgChiInfo = tess4jComponent.tessOCR(reqMsg);
    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Map<String, MsgHandleService> beansOfType = applicationContext.getBeansOfType(MsgHandleService.class);
        Collection<MsgHandleService> values = beansOfType.values();
        Iterator<MsgHandleService> iterator = values.iterator();

        for (int i = 0; i < values.size(); i++) {
            MsgHandleService item = iterator.next();
            String msgType = this.getMsgType(item.getClass().getSimpleName());
            msgMap.put(msgType, item);
        }
    }


    /**
     * 由service实例的名称获取service类型的名称
     * 例如：postService -> post
     *
     * @param simpleName
     * @return
     */
    private String getMsgType(String simpleName) {
        // 通过大小写获取字符串第一个单词
        char[] chars = simpleName.toCharArray();
        int resultIndex = 0;
        chars[0] += 32;
        for (int i = 1; i < chars.length; i++) {
            if (CharUtils.isAsciiAlphaUpper(chars[i])) {
                resultIndex = i;
                break;
            }
        }

        char[] result = Arrays.copyOf(chars, resultIndex);
        return new String(result);
    }
}
