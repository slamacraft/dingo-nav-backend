package com.dingdo.service.impl;

import com.dingdo.Component.InstructionMethodContext;
import com.dingdo.Component.SaveMsgComponent;
import com.dingdo.Component.Tess4jComponent;
import com.dingdo.extendService.musicService.impl.MusicServiceImpl;
import com.dingdo.model.msgFromCQ.ReceiveMsg;
import com.dingdo.model.msgFromCQ.ReplyMsg;
import com.dingdo.service.MsgHandleService;
import com.dingdo.service.MsgService;
import com.dingdo.util.InstructionUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 消息转发接口的实例
 */
@Service
public class MgsServiceImpl implements MsgService, ApplicationContextAware {

    // 使用log4j打印日志
    private static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(MusicServiceImpl.class);

    private Map<String, MsgHandleService> msgMap = new HashMap<>();

    @Autowired
    private Tess4jComponent tess4jComponent;

    @Autowired
    private SaveMsgComponent saveMsgComponent;

    @Autowired
    private InstructionMethodContext instructionMethodContext;

    @Override
    public ReplyMsg receive(HttpServletRequest httpServletRequest) {
        //打印请求信息
        ReceiveMsg receiveMsg = null;
        try {
            String msg = httpServletRequest.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
            if (StringUtils.isBlank(msg)) {
                return null; //请求信息为空，快速失败
            }
            System.out.println(msg);
            receiveMsg = new ObjectMapper().readValue(msg, ReceiveMsg.class);
        } catch (Exception e) {
            logger.error(e);
        }
        return handleReceiveMsg(receiveMsg);
    }

    /**
     * 处理请求消息
     * @param receiveMsg
     * @return
     */
    private ReplyMsg handleReceiveMsg(ReceiveMsg receiveMsg) {
        // 消息预处理
        this.msgOCR(receiveMsg);    // 识别图中文字
        this.saveMsg(receiveMsg);   // 存储群消息
        if (InstructionUtils.DFA(receiveMsg.getRaw_message())) {    // 有穷自动机确定是否属于指令格式
            return this.instructionHandle(receiveMsg);
        }

        // 根据请求的类型不同跳转☞不同的service实例的handleMsg方法处理
        return msgMap.get(receiveMsg.getMessage_type()).handleMsg(receiveMsg);
    }

    /**
     * 提取图中文字，保留中文
     *
     * @param receiveMsg
     * @return
     */
    public void msgOCR(ReceiveMsg receiveMsg) {
        String message = receiveMsg.getRaw_message();
        if (StringUtils.isNotBlank(message) && message.contains("[CQ:image,")) {
            String imgChiInfo = tess4jComponent.tessOCR(message);
            System.out.println("识别图中的文字为:" + imgChiInfo);
            receiveMsg.setRaw_message(message.replaceAll("\\[CQ:image,file=.*?\\]", imgChiInfo));
        }
    }

    /**
     * 通过组件存储消息
     *
     * @param receiveMsg
     */
    public void saveMsg(ReceiveMsg receiveMsg) {
        // 存储群消息
        if (receiveMsg.getMessage_type().equals("group")) {
            Long groupId = receiveMsg.getGroup_id();
            String msg = receiveMsg.getRaw_message();
            saveMsgComponent.saveGroupMsg(msg, groupId);
        }
    }

    /**
     * 指令处理方法
     *
     * @param receiveMsg
     * @return
     */
    public ReplyMsg instructionHandle(ReceiveMsg receiveMsg) {
        ReplyMsg replyMsg = new ReplyMsg();
        String result = (String) instructionMethodContext.invokeMethodByMsg(receiveMsg);
        if (StringUtils.isNotBlank(result)) {
            replyMsg.setReply(result);
            return replyMsg;
        }
        replyMsg.setReply("未知异常");
        return replyMsg;
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
