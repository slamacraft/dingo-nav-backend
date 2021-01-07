package com.dingdo.enums;

/**
 * 一些声明信息
 *
 * @author slamacraft
 * @Description:
 * @date: 2020/8/26 13:54
 * @since JDK 1.8
 */
public enum VerificationEnum {
    ROOT(0, "开发者权限","该指令仅为超级管理员开放"),
    DEVELOPER(1, "开发者权限", "该指令仅为开发人员开放"),
    MANAGER(2, "群内管理员权限", "很抱歉，你还不是本群的管理员，并不能使用该服务"),
    FRIEND(3, "好友", "很抱歉，你还不是我的好友，并不能使用该服务");

    private int level;
    private String description;
    private String errorMsg;

    VerificationEnum(int level, String description, String errorMsg) {
        this.level = level;
        this.description = description;
        this.errorMsg = errorMsg;
    }

    public int getLevel() {
        return level;
    }

    public String getDescription() {
        return description;
    }

    public String getErrorMsg() {
        return errorMsg;
    }
}
