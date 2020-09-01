package com.dingdo.simpleRobot;

import com.dingdo.msgHandler.model.ReqMsg;
import com.dingdo.msgHandler.service.MsgService;
import com.dingdo.util.SpringContextUtils;

/**
 * 一些声明信息
 *
 * @author slamacraft
 * @Description:
 * @date: 2020/8/11 10:06
 * @since JDK 1.8
 */
public class MsgListener {

    private static class MsgServiceInitalizer {
        private static MsgService msgService = SpringContextUtils.getBean(MsgService.class);
    }

    public String getReplyFromRobot(ReqMsg reqMsg) {
        if(MsgServiceInitalizer.msgService == null){
            MsgServiceInitalizer.msgService = SpringContextUtils.getBean(MsgService.class);
        }
        return MsgServiceInitalizer.msgService.handleMsg(reqMsg);
    }

}
