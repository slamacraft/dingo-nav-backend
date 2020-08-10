package com.dingdo.model.msgFromCQ;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class FriendList {

    private List<FriendListData> data;

    private Integer retcode;

    private String status;

    @Data
    public class FriendListData{
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
