package com.dingdo.simpleRobot;

import com.dingdo.Component.classifier.NaiveBayesComponent;
import com.dingdo.model.msgFromMirai.ReqMsg;
import com.dingdo.service.MsgService;
import com.dingdo.util.SpringContextUtils;
import org.apache.hadoop.yarn.webapp.hamlet.Hamlet;
import org.springframework.beans.factory.annotation.Autowired;

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
        private static MsgService msgService = (MsgService) SpringContextUtils.getBean(MsgService.class);
    }

    public String getReplyFromRobot(ReqMsg reqMsg) {
        return MsgServiceInitalizer.msgService.handleMsg(reqMsg);
    }

}
