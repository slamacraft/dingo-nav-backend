package com.dingdo.extendService.gameService.model;

import lombok.Data;

/**
 * 狼人杀玩家信息
 */
@Data
public class WolfKillerGamerData {

    /**
     * 用户id
     */
    private long userId;

    /**
     * 用户名称
     */
    private String nickName;

    /**
     * 用户状态：
     * 游戏开始前
     * 0=未准备
     * 1=准备
     *
     * 游戏开始后：
     * 0=死亡
     * 1=存活
     */
    private int status = 0;

    /**
     * 用户职业
     */
    private String occupation;

    /**
     * 用户的职责
     * 0=平民
     * 1=狼人
     * 2=神职
     */
    private int duty;

    /**
     * 游戏序号
     */
    private int number;
}
