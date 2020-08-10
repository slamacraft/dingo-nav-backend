package com.example.demo.enums;

import com.example.demo.model.msgFromCQ.ReceiveMsg;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

public enum RobotAppidEnum {

    DEFAULT(null, -1, "cebaf94c551f180d5c6847cf1ccaa1fa"),
    CHU_CHU("private", 906563518, "c37ebe447783e1ac85ef11c4c217a572");

    private String type;    // null代表所有类型
    private long id;        // -1代表所有人
    private String appid;

    private static Map<Long, String> privateAppidMap = new HashMap<>();
    private static Map<Long, String> groupAppidMap = new HashMap<>();

    RobotAppidEnum(String type, long id, String appid) {
        this.type = type;
        this.id = id;
        this.appid = appid;
    }

    public static String getAppidByMsg(ReceiveMsg receiveMsg){
        String message_type = receiveMsg.getMessage_type();
        if("private".equals(message_type)){     // 通过私聊发送
            return getAppid(message_type, receiveMsg.getUser_id());
        }
        if("group".equals(message_type)){       // 通过群聊发送
            return getAppid(message_type, receiveMsg.getGroup_id());
        }
        // 从任意渠道发送，但是是私人定制的机器人
        String appid = getAppid("private", receiveMsg.getUser_id());
        if(StringUtils.isNotBlank(appid)){
            return appid;
        }
        return RobotAppidEnum.DEFAULT.appid;
    }

    public static String getAppid(String type, long id){
        Map<Long, String> appidMap = getAppidMap(type);
        String appid = appidMap.get(id);
        return appid == null ? RobotAppidEnum.DEFAULT.appid : appid;
    }

    public static Map<Long, String> getAppidMap(String type){
        if("private".equals(type)){
            if(privateAppidMap.size()==0){
                initPrivateAppidMap();
            }
            return privateAppidMap;
        }
        if("group".equals(type)){
            if(privateAppidMap.size()==0){
                initGroupAppidMap();
            }
            return groupAppidMap;
        }
        return null;
    }

    private static void initPrivateAppidMap(){
        RobotAppidEnum[] values = RobotAppidEnum.values();
        for(RobotAppidEnum item : values){
            if("private".equals(item.type)){
                privateAppidMap.put(item.id, item.appid);
            }
        }
    }

    private static void initGroupAppidMap(){
        RobotAppidEnum[] values = RobotAppidEnum.values();
        for(RobotAppidEnum item : values){
            if("group".equals(item.type)){
                groupAppidMap.put(item.id, item.appid);
            }
        }
    }
}
