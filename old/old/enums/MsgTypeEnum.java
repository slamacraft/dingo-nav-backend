package com.example.old.enums;

import com.dingdo.msgHandler.model.ReqMsg;
import com.forte.qqrobot.beans.messages.NicknameAble;
import com.forte.qqrobot.beans.messages.RemarkAble;
import com.forte.qqrobot.beans.messages.msgget.GroupMsg;
import com.forte.qqrobot.beans.messages.msgget.MsgGet;
import com.forte.qqrobot.beans.messages.msgget.PrivateMsg;

/**
 * 一些声明信息
 *
 * @author slamacraft
 * @Description:
 * @date: 2020/8/10 16:21
 * @since JDK 1.8
 */
public enum MsgTypeEnum {

    /*私聊类型*/
    PRIVATE_MSG(PrivateMsg.class),
    /*群聊类型*/
    GROUP_MSG(GroupMsg.class);

    private Class<?> clazz;

    MsgTypeEnum(Class clazz) {
        this.clazz = clazz;
    }

    /**
     * 通过传入的对象构造初始化一个ReqMsg
     * @param msg
     * @param reqMsg
     */
    public static void createReqMsg(Object msg, ReqMsg reqMsg){
        reqMsg.setMessage(((MsgGet)msg).getMsg());
        reqMsg.setFont(((MsgGet)msg).getFont());
        reqMsg.setTime(((MsgGet)msg).getTime());
        reqMsg.setMessageId(((MsgGet)msg).getId());
        reqMsg.setNickname(((NicknameAble)msg).getNickname());
        reqMsg.setCard(((RemarkAble)msg).getRemark());
        reqMsg.setSelfId(((MsgGet)msg).getThisCode());

        if(MsgTypeEnum.PRIVATE_MSG.clazz.isInstance(msg)){
            reqMsg.setMessageType("private");
            privateReqMsg((PrivateMsg)msg, reqMsg);
        }else if(MsgTypeEnum.GROUP_MSG.clazz.isInstance(msg)){
            reqMsg.setMessageType("group");
            groupReqMsg((GroupMsg)msg, reqMsg);
        }
    }

    private static void privateReqMsg(PrivateMsg msg, ReqMsg reqMsg){
        reqMsg.setUserId(msg.getQQ());
    }
    private static void groupReqMsg(GroupMsg msg, ReqMsg reqMsg){
        reqMsg.setUserId(msg.getQQ());
        reqMsg.setGroupId(msg.getGroup());
    }

}
