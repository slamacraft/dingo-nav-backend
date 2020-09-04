package com.dingdo.extendService.model.specialReply;

import com.dingdo.msgHandler.model.ReqMsg;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 一些声明信息
 *
 * @author slamacraft
 * @Description:
 * @date: 2020/9/2 14:47
 * @since JDK 1.8
 */
@Data
public class RereadMsgInfo {

    private String userId;
    private List<String> message = new ArrayList<>();
    private int status = 0;
    private boolean flag = false; // 是否参与复读

    public RereadMsgInfo(ReqMsg reqMsg) {
        this.userId = reqMsg.getUserId();
        this.message.add(reqMsg.getMessage());
    }

}
