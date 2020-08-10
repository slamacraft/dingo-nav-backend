package com.example.demo.enums;

/**
 * 存放在redis中的枚举
 */
public enum RedisEnum {
    SYSINFO("sys_info", "系统的运行信息");

    private String key;
    private String description;

    RedisEnum(String key, String description) {
        this.key = key;
        this.description = description;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return this.key;
    }
}
