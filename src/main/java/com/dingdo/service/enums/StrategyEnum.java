package com.dingdo.service.enums;

/**
 * @author slamacraft
 * @version 1.0
 * @date 2021/1/5 9:27
 * @since JDK 1.8
 */
public enum StrategyEnum {

    CHAT("闲聊", 1.0)

    ;

    private String description;
    private double value;

    StrategyEnum(String description, double value) {
        this.description = description;
        this.value = value;
    }
}
