package com.dingdo.util.nlp;

/**
 * 一些声明信息
 *
 * @author slamacraft
 * @Description:
 * @date: 2020/8/29 15:42
 * @since JDK 1.8
 */
public enum TimeEnum {
    SEC_PERIOD(new String[]{"秒钟", "秒"}),
    SEC_POINT(new String[]{"秒"}),
    MIN_PERIOD(new String[]{"分钟", "分"}),
    MIN_POINT(new String[]{"分"}),
    HOUR_PERIOD(new String[]{"小时", "钟头"}),
    HOUR_POINT(new String[]{"时", "点钟", "点"}),
    DAY_PERIOD(new String[]{"天"}),
    DAY_POINT(new String[]{"号", "日"}),
    MONTH(new String[]{"月"}),
    WEEK(new String[]{"星期", "周"});

    private final String[] keywords;

    TimeEnum(String[] keywords) {
        this.keywords = keywords;
    }

    public String[] getKeywords() {
        return keywords;
    }
}
