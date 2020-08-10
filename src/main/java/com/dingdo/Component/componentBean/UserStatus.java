package com.dingdo.Component.componentBean;

import lombok.NoArgsConstructor;

import java.util.Date;

@NoArgsConstructor
public class UserStatus {
    /**
     * 用户的功能状态
     */
    private boolean status = false;
    /**
     * 用户的最后发言时间（用户初始化长时间未操作的用户状态）
     */
    private Date lastSendMsgTime;

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public Date getLastSendMsgTime() {
        return lastSendMsgTime;
    }

    public void setLastSendMsgTime(Date lastSendMsgTime) {
        this.lastSendMsgTime = lastSendMsgTime;
    }
}
