package com.dingdo.model.msgFromCQ;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@Deprecated
public class LoginUser {

    private LoginerUserData data;

    private Integer retcode;

    private String status;

    @Data
    public class LoginerUserData{
        /**
         * 登录机器人的QQ号
         */
        private long user_id;

        /**
         * 登录机器人的QQ名称
         */
        private String nickname;
    }
}
