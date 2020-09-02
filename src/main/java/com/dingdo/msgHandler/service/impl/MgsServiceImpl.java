package com.dingdo.msgHandler.service.impl;

import com.dingdo.Component.InstructionMethodContext;
import com.dingdo.Component.SaveMsgComponent;
import com.dingdo.Component.Tess4jComponent;
import com.dingdo.msgHandler.factory.CQCodeFactory;
import com.dingdo.msgHandler.model.CQCode;
import com.dingdo.msgHandler.model.ReqMsg;
import com.dingdo.msgHandler.service.MsgHandleService;
import com.dingdo.msgHandler.service.MsgService;
import com.dingdo.util.CQCodeUtil;
import com.dingdo.util.InstructionUtils;
import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * 消息转发接口的实例
 */
@Service
public class MgsServiceImpl implements MsgService, ApplicationContextAware {

    // 使用log4j打印日志
    private static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(MgsServiceImpl.class);

    private Map<String, MsgHandleService> msgMap = new HashMap<>();

    @Autowired
    private Tess4jComponent tess4jComponent;
    @Autowired
    private InstructionMethodContext instructionMethodContext;

    @Override
    public String receive(HttpServletRequest httpServletRequest) {
        return null;
    }


    @Override
    public String handleMsg(ReqMsg reqMsg) {
        this.extractCQCode(reqMsg);
        this.msgOCR(reqMsg);    // 识别图中文字
        if (InstructionUtils.DFA(reqMsg.getRawMessage())) {    // 使用DFA确定是否属于指令格式
            return this.instructionHandle(reqMsg);
        }

        // 根据请求的类型不同跳转☞不同的service实例的handleMsg方法处理
        return msgMap.get(reqMsg.getMessageType()).handleMsg(reqMsg);
    }


    /**
     * 提取cq码
     *
     * @param reqMsg
     */
    public void extractCQCode(ReqMsg reqMsg) {
        String msg = reqMsg.getMessage();
        List<CQCode> cqCodeList = CQCodeFactory.getCQCodeList(msg);
        reqMsg.setCqCodeList(cqCodeList);
        reqMsg.setRawMessage(CQCodeUtil.removeAllCQCode(msg));
    }


    /**
     * 提取图中文字，保留中文
     *
     * @param reqMsg
     * @return
     */
    public void msgOCR(ReqMsg reqMsg) {
        String imgChiInfo = tess4jComponent.tessOCR(reqMsg);
        reqMsg.setRawMessage(reqMsg.getRawMessage() + imgChiInfo);
    }


    /**
     * 指令处理方法
     *
     * @param reqMsg
     * @return
     */
    public String instructionHandle(ReqMsg reqMsg) {
        String result = (String) instructionMethodContext.invokeMethodByMsg(reqMsg);
        if (StringUtils.isNotBlank(result)) {
            return result;
        }
        return "未知异常";
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
