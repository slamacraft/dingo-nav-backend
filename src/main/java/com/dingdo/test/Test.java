package com.dingdo.test;

import com.dingdo.Component.classifier.NaiveBayesComponent;
import com.dingdo.util.SpringContextUtils;
import com.forte.qqrobot.BotRuntime;
import com.forte.qqrobot.anno.Listen;
import com.forte.qqrobot.anno.depend.Beans;
import com.forte.qqrobot.beans.messages.msgget.GroupMsg;
import com.forte.qqrobot.beans.messages.msgget.PrivateMsg;
import com.forte.qqrobot.beans.messages.types.MsgGetTypes;
import com.forte.qqrobot.sender.MsgSender;
import com.simplerobot.modules.utils.KQCodeUtils;

/**
 * 一些声明信息
 *
 * @author slamacraft
 * @Description:
 * @date: 2020/8/9 18:56
 * @since JDK 1.8
 */
@Beans

public class Test {

    /**
     * 监听私信消息并复读
     */
    @Listen(MsgGetTypes.privateMsg)
    public void hello(PrivateMsg msg, MsgSender sender) {
        System.out.println("收到消息：" + msg);
        // 复读, 以下方法均可
        NaiveBayesComponent naiveBayesComponent = (NaiveBayesComponent) SpringContextUtils.getBean("naiveBayesComponent");
        double predict = naiveBayesComponent.predict(msg.getMsg());
        sender.SENDER.sendPrivateMsg(msg, msg.getMsg() + "语义分析的结果:" + predict);
//        sender.SENDER.sendPrivateMsg(msg.getQQ(), msg.getMsg());
//        sender.SENDER.sendPrivateMsg(msg.getQQCode(), msg.getMsg());
//        sender.reply(msg, msg.getMsg(), false);
    }

    @Listen(MsgGetTypes.groupMsg)
    public void test2(GroupMsg msg, MsgSender sender) {
        BotRuntime.getRuntime().getBotManager().getBot("3087687530").getSender();
        sender.SENDER.sendGroupMsg(msg, atSenderOnBeginning(msg.getMsg(), 1114951452L));
        sender.SENDER.sendGroupMsg(msg, sendPicture("file=C:\\Users\\Administrator\\Desktop\\4.jpg"));
    }

    /**
     * 在句首at某人
     *
     * @param replyMsg
     * @param userId
     */
    private String atSenderOnBeginning(String replyMsg, Long userId) {
        return "[CQ:at,qq=" + userId + "]" + replyMsg;
    }

    private String sendPicture(String imgPath) {
        return KQCodeUtils.INSTANCE.toCq("image", imgPath);
    }
}
