package com.dingo.service.enums;


import java.util.Objects;

/**
 * 记录着每个服务对应的激活语料库文件地址
 */
public enum ClassicEnum {
    //========================================================================================================
    KNOWLEDGE_SRC("static/question/knowledgeQA/",
            null, 1, "知识问答语料库路径"),
    BAIDU_BAIKE("BaiduBaike.txt", null, 1.1, "百度百科搜索"),
    BAIDU_ZHIDAO("BaiduZhidao.txt", null, 1.2, "搜狗问问搜索"),
    SOUGOU_WENWEN("SougouWenWen.txt", null, 1.3, "搜狗问问搜索"),

    //========================================================================================================
    MUSIC_SRC("static/question/musicQA/",
            null, 2, "点歌服务语料路径"),
    //    MUSIC_FROM_QQ("MusicFromQQ.txt", "MusicServiceImpl", 2.1, "QQ音乐点歌"),
    RANDOM_MUSIC("RandomMusic.txt", "", 2.2, "网易云随机音乐"),

    //========================================================================================================
    WEATHER_SRC("static/question/weatherQA/",
            null, 3, ""),
    WEATHER_NOW("WeatherNow.txt", "WeatherServiceImpl", 3.1, "【询问地区的实时气温】"),
    WEATHER_DAILY_FORECAST("WeatherDailyForecast.txt", null, 3.2, "获取天气预测"),
    WEATHER_LIFESTYLE("WeatherLifestyle.txt", "WeatherLifestyleServiceImpl", 3.3, "【询问地区的生活指数】"),
    WEATHER_HOURLY("WeatherHourly.txt", null, 3.4, "获取气温每小时详情");

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

    /**
     * 通过文件名称获取枚举
     *
     * @param fileName
     * @return
     */
    public static ClassicEnum getEnumByFileName(String fileName) {
        ClassicEnum[] values = ClassicEnum.values();
        for (ClassicEnum item : values) {
            if (Objects.equals(item.getFileName(), fileName)) {
                return item;
            }
        }
        return null;
    }

    /**
     * 通过调用的服务名称获取枚举
     *
     * @param serviceName
     * @return
     */
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
    @Deprecated
//    public static Map<String, String> getAllFileSrc() {
//        ClassicEnum[] values = ClassicEnum.values();
//        Map<String, String> resultList = new HashMap<>();
//
//        for (int i = 0; i < values.length; i++) {
//            if (StringUtils.isBlank(values[i].getServiceName())
//                    && values[i].getValue() - (int) values[i].getValue() == 0) {
//                for (int j = i + 1; j < values.length; j++) {
//                    if (StringUtils.isNotBlank(values[j].getServiceName())) {
////                        double sub = values[j].getValue() - values[i].getValue();
//                        if ((int) values[j].getValue() == (int) values[i].getValue()) {
//                            resultList.put(values[i].getFileName() + values[j].getFileName(), values[j].getFileName());
//                        }
//                    }
//                }
//            }
//        }
//
//        return resultList;
//    }


    /**
     * 获取所有语料文件的分类值，以及其所对应的文件本地路径
     *
     * @return 返回一个装有分类值和文件路径的Map
     * key: layer
     * value: filePath
     */
//    public static Map<Double, String> getAllLayerFileMap() {
//        Stream<ClassicEnum> valuesStream = Arrays.stream(values());
//
//        // 获取过滤出ServiceName为null, 且其分类值value为整数的enum
//        // 并将其封装为一个Map
//        // key: value, value: FileName
//        Map<Double, String> layerSrcPathMap = valuesStream.filter(
//                item -> StringUtils.isBlank(item.getServiceName())
//                        && Math.ceil(item.getValue()) == Math.floor(item.getValue())
//        ).collect(Collectors.toMap(
//                item -> Math.rint(item.getValue()), ClassicEnum::getFileName)
//        );
//
//        // 获取ServiceName不为null的enum, 并通过其分类值向下取整，
//        // 从上面layerSrcPathMap中获取文件夹路径，并与自身文件名拼接
//        // 获得完整的文件路径，并封装为一个Map
//        // key: value, value: filePath
//        return valuesStream.filter(item -> StringUtils.isNotBlank(item.getServiceName()))
//                .collect(Collectors.toMap(
//                        ClassicEnum::getValue,
//                        item -> layerSrcPathMap.get(Math.ceil(item.getValue())) + item.getFileName()
//                ));
//    }

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