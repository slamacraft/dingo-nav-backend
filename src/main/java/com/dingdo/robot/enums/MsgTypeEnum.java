package com.dingdo.robot.enums;

/**
 * @author slamacraft
 * @version 1.0
 * @date 2020/12/22 13:53
 * @since JDK 1.8
 */
public enum MsgTypeEnum {
    PRIVATE("私聊请求"),
    GROUP("群聊请求");

    private String name;

    MsgTypeEnum(String name) {
        this.name = name;
    }
}
