package com.dingdo.msgHandler.service.impl;

import com.dingdo.config.customContext.InstructionMethodContext;
import com.dingdo.msgHandler.factory.RobotMsgFactory;
import com.dingdo.msgHandler.model.CQCode;
import com.dingdo.msgHandler.model.ReqMsg;
import com.dingdo.msgHandler.service.MsgHandleService;
import com.dingdo.msgHandler.service.MsgService;
import com.dingdo.util.CQCodeUtil;
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
    private static final Logger logger = Logger.getLogger(MgsServiceImpl.class);

    private final Map<String, MsgHandleService> msgMap = new HashMap<>();

//    private final Tess4jComponent tess4jComponent;
    private final InstructionMethodContext instructionMethodContext;

    @Autowired
    public MgsServiceImpl(InstructionMethodContext instructionMethodContext) {
        this.instructionMethodContext = instructionMethodContext;
    }

    @Override
    public String receive(ReqMsg request) {
        return null;
    }


    @Override
    public String handleMsg(ReqMsg reqMsg) {
        logger.info("收到消息:" + reqMsg.getMessage());
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
     * <p>
     * 从{@code reqMsg.message}中将cq码提取出来，并保存到
     * {@code reqMsg.cqCodeList}中，提取完cq码后的消息保存至
     * {@code reqMsg.rawMessage}中。
     * </p>
     *
     * @param reqMsg 请求消息
     */
    private void extractCQCode(ReqMsg reqMsg) {
        String msg = reqMsg.getMessage();
        List<CQCode> cqCodeList = RobotMsgFactory.getCQCodeList(msg);
        reqMsg.setCqCodeList(cqCodeList);
        reqMsg.setRawMessage(CQCodeUtil.removeAllCQCode(msg));
    }


    /**
     * 提取图中文字，保留中文
     *
     * @param reqMsg 请求消息
     */
    private void msgOCR(ReqMsg reqMsg) {
//        String imgChiInfo = tess4jComponent.tessOCR(reqMsg);
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
     * @param simpleName service的实例简单名称
     * @return 消息的类型
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
