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
    private static final String[] DAY = new String[]{"天", "号", "日"};
    private static final String[] MONTH = new String[]{"月"};
    private static final String[] WEEK = new String[]{"星期", "周"};
    private static final String[] WEEK_ENUM = new String[]{"周一", "周二", "周三", "周四", "周五", "周六", "周日"};

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
     * @param text  文本内容
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
        int day = getIndexBatch(wordList, DAY);
        int mon = getIndexBatch(wordList, MONTH);
        int week = getIndexBatch(wordList, WEEK);
        int weekEnum = getIndexBatch(wordList, WEEK_ENUM);

        if (secPoint >= 1 && (secPoint -2 >=0 && !"每".equals(list.get(secPoint - 2).word))) {
            int number = getNumber(list.get(secPoint - 1).word);
            if (number >= 0) {
                cron.append(number + " ");
            } else {
                cron.append("* ");
            }
        } else if(secPeriod >= 1){
            int number = getNumber(list.get(secPeriod - 1).word);
            if (number >= 0) {
                cron.append("0/" + number + " ");
            } else {
                cron.append("* ");
            }
        } else{
            cron.append("0 ");
        }

        if (minPoint >= 1 && (minPoint -2 >=0 && !"每".equals(list.get(minPoint - 2).word))) {
            int number = getNumber(list.get(minPoint - 1).word);
            if (number >= 0) {
                cron.append(number + " ");
            } else {
                cron.append("0 ");
            }
        }else if(hourPoint >= 1 && "半".equals(list.get(hourPoint + 1).word)){
            cron.append("30 ");
        }else if (minPeriod >= 1) {
            int number = getNumber(list.get(minPeriod - 1).word);
            if (number >= 0) {
                cron.append("0/" + number + " ");
            } else {
                cron.append("* ");
            }
        }else {
            cron.append("0 ");
        }

        if (hourPoint >= 1) {
            int number = getNumber(list.get(hourPoint - 1).word);
            if (hourPoint - 2 > 0 && "晚上".equals(list.get(hourPoint - 2).word)) {
                number += 12;
            }
            if (number >= 0) {
                cron.append(number + " ");
            }else {
                cron.append("* ");
            }
        }else if (hourPeriod >= 1) {
            int number = getNumber(list.get(hourPeriod - 1).word);
            if (hourPeriod - 2 >= 0 && "晚上".equals(list.get(hourPeriod - 2).word)) {
                number += 12;
            }
            if (number >= 0) {
                cron.append("0/" + number + " ");
            } else {
                cron.append("* ");
            }
        } else {
            cron.append("* ");
        }

        if (day >= 1) {
            int number = getNumber(list.get(day - 1).word);
            if (number >= 0) {
                cron.append("0/" + number + " ");
            } else {
                cron.append("* ");
            }
        } else {
            cron.append("* ");
        }

        if (mon >= 1) {
            int number = getNumber(list.get(mon - 1).word);
            if (number >= 0) {
                cron.append("0/" + number + " ");
            } else {
                cron.append("* ");
            }
        } else {
            cron.append("* ");
        }

         if (weekEnum >= 0) {
            int number = getNumber(list.get(weekEnum).word.substring(1));
            if (number >= 0) {
                cron.append(number);
            } else {
                cron.append("?");
            }
        }else if (week >= 0 && week < list.size() - 2) {
             int number = getNumber(list.get(week + 1).word);
             if (number >= 0) {
                 cron.append(number);
             } else {
                 cron.append("1");
             }
         }else {
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
        if("每".equals(text)){
            return -1;
        }
        if("半".equals(text)){
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
