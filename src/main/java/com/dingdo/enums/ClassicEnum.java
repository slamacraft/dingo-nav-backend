package com.dingdo.enums;

import java.util.*;

/**
 * 记录着每个服务对应的激活语料库文件地址
 */
public enum ClassicEnum {
    //========================================================================================================
    KNOWLEDGE_SRC("python/CQPython/static/question/knowledgeQA/",
            null, 1, "知识问答语料库路径"),
    BAIDU_BAIKE("BaiduBaike.txt", "SearchServiceImpl", 1.1, "百度百科搜索"),
    BAIDU_ZHIDAO("BaiduZhidao.txt", "ZhidaoServiceImpl", 1.2, "搜狗问问搜索"),
    SOUGOU_WENWEN("SougouWenWen.txt", "SougoServiceImpl", 1.3, "搜狗问问搜索"),

    //========================================================================================================
    MUSIC_SRC("python/CQPython/static/question/musicQA/",
            null, 2, "点歌服务语料路径"),
    MUSIC_FROM_QQ("MusicFromQQ.txt", "MusicServiceImpl", 2.1, "QQ音乐点歌"),
    RANDOM_MUSIC("RandomMusic.txt", "", 2.2, "网易云随机音乐"),

    //========================================================================================================
    WEATHER_SRC("python/CQPython/static/question/weatherQA/",
            null, 3, ""),
    WEATHER_NOW("WeatherNow.txt", "WeatherServiceImpl", 3.1, "获取现在天气详情"),
    WEATHER_DAILY_FORECAST("WeatherDailyForecast.txt", "", 3.2, "获取天气预测"),
    WEATHER_LIFESTYLE("WeatherLifestyle.txt", "WeatherLifestyleServiceImpl", 3.3, "获取生活指数"),
    WEATHER_HOURLY("WeatherHourly.txt", "", 3.4, "获取气温每小时详情");

    private String fileName;
    private String serviceName;
    private double value;
    private String describe;

//    private static Map<String, ClassicEnum> enumMap;

    ClassicEnum(String fileName, String serviceName, double value, String describe) {
        this.fileName = fileName;
        this.serviceName = serviceName;
        this.value = value;
        this.describe = describe;
    }

    public static ClassicEnum getEnumByFileName(String fileName) {
        ClassicEnum[] values = ClassicEnum.values();
        for (ClassicEnum item : values) {
            if (Objects.equals(item.getFileName(), fileName)) {
                return item;
            }
        }
        return null;
    }

    public static ClassicEnum getEnumByServiceName(String serviceName) {
        ClassicEnum[] values = ClassicEnum.values();
        for (ClassicEnum item : values) {
            if (Objects.equals(item.getServiceName(), serviceName.split("\\$")[0])) {
                return item;
            }
        }
        return null;
    }

    /**
     * 获取所有的语料文件名与文件地址地址
     * Map: filePath -> fileName
     *
     * @return
     */
    public static Map<String, String> getAllFileSrc() {
        ClassicEnum[] values = ClassicEnum.values();
        Map<String, String> resultList = new HashMap<>();

        for (int i = 0; i < values.length; i++) {
            if (values[i].getServiceName() == null) {
                for (int j = i + 1; j < values.length; j++) {
                    double sub = values[j].getValue() - values[i].getValue();
                    if (sub > 0 && sub < 1) {
                        resultList.put(values[i].getFileName() + values[j].getFileName(), values[j].getFileName());
                    }
                }
            }
        }

        return resultList;
    }

    // ==========================================get&set============================================

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    // ============================================Override===========================================

    @Override
    public String toString() {
        return "ClassicEnum{" +
                "describe='" + describe + '\'' +
                '}';
    }
}