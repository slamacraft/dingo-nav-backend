package com.dingdo.service.model.specialReply;

import com.dingdo.robot.botDto.ReqMsg;
import lombok.Data;

import java.util.LinkedList;
import java.util.List;

/**
 * 一些声明信息
 *
 * @author slamacraft
 * @Description:
 * @date: 2020/9/2 14:48
 * @since JDK 1.8
 */
@Data
public class RereadMsgQueue {

    volatile private List<RereadMsgInfo> msgInfoList = new LinkedList<>();

    public RereadMsgQueue(ReqMsg reqMsg) {
        msgInfoList.add(new RereadMsgInfo(reqMsg));
    }

}
