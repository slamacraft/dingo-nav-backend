package com.dingdo.util;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.seg.Segment;
import com.hankcs.hanlp.seg.common.Term;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 自然语言处理工具类
 */
public class NLPUtils {

    // 简易分词器
    private static Segment nativeSegment = HanLP.newSegment();

    // 启用地名识别的分词器
    private static Segment placeSegment = HanLP.newSegment();

    // 启用命名实体识别的分词器
    private static Segment NERSegment = HanLP.newSegment();

    private static final String[] SEC_PERIOD = new String[]{"秒钟", "秒"};
    private static final String[] SEC_POINT = new String[]{"秒"};
    private static final String[] MIN_PERIOD = new String[]{"分钟", "分"};
    private static final String[] MIN_POINT = new String[]{"分"};
    private static final String[] HOUR_PERIOD = new String[]{"小时", "钟头"};
    private static final String[] HOUR_POINT = new String[]{"时", "点钟", "点"};
    private static final String[] DAY_PERIOD = new String[]{"天"};
    private static final String[] DAY_POINT = new String[]{"号", "日"};
    private static final String[] MONTH = new String[]{"月"};
    private static final String[] MONTH_ENUM = new String[]{"一月", "二月", "三月", "四月", "五月", "六月", "七月", "八月", "久月", "十月", "十一月", "十二月"};
    private static final String[] WEEK = new String[]{"星期", "周"};
    private static final String[] WEEK_ENUM = new String[]{"周一", "周二", "周三", "周四", "周五", "周六", "周日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六", "星期天", "星期日"};

    static {
        // 启用地名识别
        placeSegment.enablePlaceRecognize(true);
        NERSegment.enableAllNamedEntityRecognize(true);
    }

    /**
     * 获取一个全命名实体识别的分词器
     *
     * @return
     */
    public static Segment getNERSegment() {
        return NERSegment;
    }

    /**
     * 获取一个简易分词器
     *
     * @return
     */
    public static Segment getNativeSegment() {
        return nativeSegment;
    }

    /**
     * 获取识别地名的分词器
     *
     * @return
     */
    public static Segment getPlaceSegment() {
        return placeSegment;
    }

    /**
     * 语句抽象化，将指定词性的词替换成词性的简写
     *
     * @param querySentence
     * @return
     */
    public static List<Term> queryAbstract(String querySentence, String... natures) {
        // 句子抽象化
        Segment segment = NLPUtils.getPlaceSegment();
        List<Term> terms = segment.seg(querySentence);
        for (Term term : terms) {
            for (String nature : natures) {
                if (term.nature.toString().equals(nature)) {
                    term.word = term.nature.toString();
                }
            }
        }
        return terms;
    }

    /**
     * 命名实体识别，提取文本中所有的实体
     *
     * @return
     */
    public static List<Term> getNER(String msg) {
        List<Term> terms = NERSegment.seg(msg);

        List<Term> result = terms.stream().filter(item -> {
            if (item.nature.toString().matches("^[n|g].*")) {
                return true;
            }
            return false;
        }).collect(Collectors.toList());
        return result;
    }

    public static void main(String args[]) {
        System.out.println(getCronFromString("每3小时一次"));
    }


    /**
     * 从文本中提取cron表达式
     *
     * @param text 文本内容
     * @return cron表达式
     */
    public static String getCronFromString(String text) {
        List<Term> list = nativeSegment.seg(text);
        System.out.println(list);

        List<String> wordList = list.stream().map(item -> item.word).collect(Collectors.toList());

        StringBuilder cron = new StringBuilder();

        int secPeriod = getIndexBatch(wordList, SEC_PERIOD);
        int secPoint = getIndexBatch(wordList, SEC_POINT);
        int minPeriod = getIndexBatch(wordList, MIN_PERIOD);
        int minPoint = getIndexBatch(wordList, MIN_POINT);
        int hourPeriod = getIndexBatch(wordList, HOUR_PERIOD);
        int hourPoint = getIndexBatch(wordList, HOUR_POINT);
        int dayPeriod = getIndexBatch(wordList, DAY_PERIOD);
        int dayPoint = getIndexBatch(wordList, DAY_POINT);
        int mon = getIndexBatch(wordList, MONTH);
        int monEnum = getIndexBatch(wordList, MONTH_ENUM);
        int week = getIndexBatch(wordList, WEEK);
        int weekEnum = getIndexBatch(wordList, WEEK_ENUM);

        // 获取秒钟
        if (secPoint >= 1 && (secPoint - 2 >= 0 && !"每".equals(list.get(secPoint - 2).word))) {
            int number = getNumber(list.get(secPoint - 1).word);
            if (number >= 0) {
                cron.append(number + " ");
            } else {
                cron.append("* ");
            }
        } else if (secPeriod >= 1) {
            int number = getNumber(list.get(secPeriod - 1).word);
            if (number >= 0) {
                cron.append("0/" + number + " ");
            } else {
                cron.append("* ");
            }
        } else {
            cron.append("0 ");
        }


        // 获取分钟
        if (minPoint >= 1 && (minPoint - 2 >= 0 && !"每".equals(list.get(minPoint - 2).word))) {
            int number = getNumber(list.get(minPoint - 1).word);
            if (number >= 0) {
                cron.append(number + " ");
            } else {
                cron.append("0 ");
            }
        } else if (hourPoint >= 1 && hourPoint + 1 < list.size() && "半".equals(list.get(hourPoint + 1).word)) {
            cron.append("30 ");
        } else if (minPeriod >= 1) {
            int number = getNumber(list.get(minPeriod - 1).word);
            if (number >= 0) {
                cron.append("0/" + number + " ");
            } else {
                cron.append("* ");
            }
        } else if (hourPeriod >= 1 || hourPoint >= 1) {
            int hourIndex = 1;
            if (hourPeriod >= 0) {
                hourIndex += hourPeriod;
            } else {
                hourIndex += hourPoint;
            }
            if (hourIndex < list.size()) {
                int number = getNumber(list.get(hourIndex).word);
                if (number >= 0) {
                    cron.append(number + " ");
                }
            } else {
                cron.append("0 ");
            }
        } else {
            cron.append("0 ");
        }

        if (hourPoint >= 1) {
            int number = getNumber(list.get(hourPoint - 1).word);
            if (hourPoint - 2 > 0 && "晚上".equals(list.get(hourPoint - 2).word)) {
                number += 12;
            }
            if (number >= 0) {
                cron.append(number + " ");
            } else {
                cron.append("* ");
            }
        } else if (hourPeriod >= 1) {
            int number = getNumber(list.get(hourPeriod - 1).word);
            String timeWord = list.get(hourPeriod - 2).word;
            if (hourPeriod - 2 >= 0 && ("晚上".equals(timeWord) || "下午".equals(timeWord))) {
                if (number + 12 <= 24) {
                    number += 12;
                }
            }
            if (number >= 0) {
                cron.append("0/" + number + " ");
            } else {
                cron.append("* ");
            }
        } else {
            cron.append("* ");
        }

        if (dayPoint >= 1 && (dayPoint - 2 >= 0 && !"每".equals(list.get(dayPoint - 2).word))) {
            int number = getNumber(list.get(dayPoint - 1).word);
            if (number >= 0) {
                cron.append(number + " ");
            } else {
                cron.append("* ");
            }
        } else if (dayPeriod >= 1) {
            int number = getNumber(list.get(dayPeriod - 1).word);
            if (number >= 0) {
                cron.append("1/" + number + " ");
            } else {
                cron.append("* ");
            }
        } else if (monEnum >= 0 || mon >= 1) {
            int monIndex = 1;
            if (monEnum >= 0) {
                monIndex += monEnum;
            } else {
                monIndex += mon;
            }
            if (monIndex < list.size()) {
                int number = getNumber(list.get(monIndex).word);
                if (number >= 0) {
                    cron.append(number + " ");
                }
            } else {
                cron.append("* ");
            }
        } else {
            cron.append("* ");
        }

        if (monEnum >= 0) {
            String word = list.get(monEnum).word;
            int number = getNumber(word.substring(0, word.length() - 1));
            if (number >= 0) {
                cron.append(number);
            } else {
                cron.append("?");
            }
        } else if (mon >= 1) {
            int number = getNumber(list.get(mon - 1).word);
            if (number >= 0) {
                cron.append(number + " ");
            } else {
                cron.append("* ");
            }
        } else {
            cron.append("* ");
        }

        if (weekEnum >= 0) {
            String word = list.get(weekEnum).word;
            int number = getNumber(word.substring(word.length() - 1));
            if (number >= 0) {
                cron.append(number);
            } else {
                cron.append("?");
            }
        } else if (week >= 0 && week < list.size() - 2) {
            int number = getNumber(list.get(week + 1).word);
            if (number >= 0) {
                cron.append(number);
            } else {
                cron.append("1");
            }
        } else {
            cron.append("?");
        }

        return cron.toString();
    }


    public static int getIndexBatch(List<String> list, String[] strList) {
        List<Integer> result = new ArrayList<>();

        for (String str : strList) {
            int i = list.indexOf(str);
            if (i >= 0) {
                result.add(i);
            }
        }
        if (CollectionUtils.isEmpty(result)) {
            return -1;
        }
        return result.stream().min(Integer::compareTo).get();
    }

    public static int getNumber(String text) {
        if ("每".equals(text)) {
            return -1;
        }
        if ("半".equals(text)) {
            return 30;
        }
        if ("日".equals(text)) {
            return 7;
        }

        text = text.replaceAll("[个]", "");
        List<String> chiNimList = new ArrayList<>();
        chiNimList.add("零");
        chiNimList.add("一");
        chiNimList.add("二");
        chiNimList.add("三");
        chiNimList.add("四");
        chiNimList.add("五");
        chiNimList.add("六");
        chiNimList.add("七");
        chiNimList.add("八");
        chiNimList.add("九");

        char[] chars = text.toCharArray();

        StringBuilder result = new StringBuilder();

        for (char c : chars) {
            int i = chiNimList.indexOf(c + "");
            if (i > -1) {
                result.append(i);
                continue;
            }
            result.append(c);
        }
        return Integer.valueOf(result.toString());
    }
}
