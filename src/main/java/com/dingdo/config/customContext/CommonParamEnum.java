package com.dingdo.config.customContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 指令所使用的公共后缀枚举
 *
 * @author slamacraft
 * @date: 2020/9/2 16:20
 * @since JDK 1.8
 */
public enum CommonParamEnum {

    HELP(new String[]{"帮助", "h", "help"}, "帮助");

    private String[] paramName;
    private String description;

    private static Map<String, CommonParamEnum> commonParamEnumMap = new HashMap<>();

    CommonParamEnum(String[] paramName, String description) {
        this.paramName = paramName;
        this.description = description;
    }

    /**
     * 通过指令参数集合判断是否是公共后缀
     * @param paramMap  指令参数集合
     * @return  公共后缀的枚举
     */
    public static CommonParamEnum getParamEnum(Map<String, String> paramMap){
        if(commonParamEnumMap.size() == 0){
            initCommonParamsMap();
        }
        List<String> paramsList = new ArrayList<>(paramMap.keySet());

        for(String param: paramsList){
            CommonParamEnum commonParamEnum = commonParamEnumMap.get(param);
            if(commonParamEnum!=null){
                return commonParamEnum;
            }
        }
        return null;
    }


    public static void initCommonParamsMap(){
        CommonParamEnum[] values = CommonParamEnum.values();

        for(CommonParamEnum commonParamEnum : values){
            for(String param: commonParamEnum.paramName){
                commonParamEnumMap.put(param, commonParamEnum);
            }
        }
    }

    public String[] getParamName() {
        return paramName;
    }

    public String getDescription() {
        return description;
    }
}
