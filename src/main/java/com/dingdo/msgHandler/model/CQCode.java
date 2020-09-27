package com.dingdo.msgHandler.model;

import com.dingdo.enums.CQCodeEnum;

import java.util.Map;

/**
 * 一些声明信息
 *
 * @author slamacraft
 * @date: 2020/8/31 14:00
 * @since JDK 1.8
 */
public class CQCode {

    // CQ码类型
    private CQCodeEnum code;

    // CQ码的变量值
    private Map<String, String> values;


    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("[CQ:").append(code.getName());

        for (Map.Entry<String, String> item : values.entrySet()) {
            result.append(",").append(item.getKey()).append("=").append(item.getValue());
        }

        return result.toString();
    }

    @Override
    public boolean equals(Object obj){
        if(obj == null){
            return false;
        }
        if(this == obj){
            return true;
        }
        if(obj instanceof String){
            return obj.equals(this.code.getName());
        } else if(obj instanceof CQCode){
            return ((CQCode) obj).getCode().equals(this.code);
        } else if(obj instanceof CQCodeEnum){
            return obj.equals(this.code);
        }
        return false;
    }

    public CQCodeEnum getCode() {
        return code;
    }

    public void setCode(CQCodeEnum code) {
        this.code = code;
    }

    public Map<String, String> getValues() {
        return values;
    }

    public void setValues(Map<String, String> values) {
        this.values = values;
    }
}
