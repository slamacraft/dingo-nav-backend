package com.example.demo.extendService.gameService.gameEnum;

import java.util.ArrayList;
import java.util.List;

public enum  WolfKillerIdentityEnum {
    CIVILIAN("civilian", "平民"),
    PROPHET("prophet", "预言家"),
    WITCH("witch", "女巫"),
    HUNTER("hunter", "猎人"),
    GUARD("guard", "守卫"),
    WEREWOLVES("werewolves", "狼人");

    private String dutyEN;
    private String dutyCN;

    private static List<WolfKillerIdentityEnum> identityEnumList = new ArrayList<>();

    WolfKillerIdentityEnum(String dutyEN, String dutyCN) {
        this.dutyEN = dutyEN;
        this.dutyCN = dutyCN;
    }

    public static List<WolfKillerIdentityEnum> getidentityEnumList(){
        if(WolfKillerIdentityEnum.identityEnumList.size() > 0){
            return WolfKillerIdentityEnum.identityEnumList;
        }

        WolfKillerIdentityEnum[] values = WolfKillerIdentityEnum.values();
        for(WolfKillerIdentityEnum item : values){
            identityEnumList.add(item);
        }
        return WolfKillerIdentityEnum.identityEnumList;
    }

    public String getDutyCN() {
        return dutyCN;
    }

    public String getDutyEN() {
        return dutyEN;
    }
}
