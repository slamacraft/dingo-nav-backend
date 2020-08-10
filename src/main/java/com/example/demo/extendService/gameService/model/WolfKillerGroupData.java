package com.example.demo.extendService.gameService.model;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * 狼人杀群组信息
 */
@Data
public class WolfKillerGroupData {

    private Long groupId;
    /**
     * 游戏状态；
     * 0=未开始
     * 1=已经开始
     */
    private int status = 0;

    /**
     * 第几天
     */
    private int day = 0;

    /**
     * 是否是晚上
     */
    private boolean isNight = true;

    private Map<Long, WolfKillerGamerData> gamerDataMap = new HashMap<>();

    /**
     * 本局游戏职业列表
     */
    private Map<String, Integer> occupationMap = new HashMap<>();
}
