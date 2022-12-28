package com.dingo.enums;

/**
 * 一些声明信息
 *
 * @author slamacraft
 * @Description:
 * @date: 2020/8/31 14:03
 * @since JDK 1.8
 */
public enum CQCodeEnum {

    AT("at", new String[]{"id", "qq"}),
    IMAGE("image", new String[]{"url"});

    private String name;    // CQ码的名称
    private String[] params;    // CQ码的变量名称

    CQCodeEnum(String name, String[] params) {
        this.name = name;
        this.params = params;
    }

    public static CQCodeEnum getCQCode(String code){
        CQCodeEnum[] values = CQCodeEnum.values();
        for(CQCodeEnum item : values){
            if(item.getName().equals(code)){
                return item;
            }
        }
        return null;
    }

    public String getName() {
        return name;
    }

    public String[] getParams() {
        return params;
    }
}
