package com.dingdo.util.nlp;

import cn.hutool.core.collection.CollectionUtil;
import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.seg.Segment;
import com.hankcs.hanlp.seg.common.Term;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.dingdo.util.nlp.TimeEnum.*;

/**
 * 自然语言处理工具类
 */
public final class NLPUtils {

    private NLPUtils() {
    }

    // 简易分词器
    private static final Segment nativeSegment = HanLP.newSegment();

    // 启用地名识别的分词器
    private static final Segment placeSegment = HanLP.newSegment();

    // 启用命名实体识别的分词器
    private static final Segment NERSegment = HanLP.newSegment();

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
     * @param querySentence 句子
     * @return 返回抽象并分词后的句子词组
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
     * @return 分词后的句子词组
     */
    public static List<Term> getNER(String msg) {
        List<Term> terms = NERSegment.seg(msg);

        return terms.stream()
                .filter(item -> item.nature.toString().matches("^[n|g].*"))
                .collect(Collectors.toList());
    }


    /**
     * 从自然语言中提取cron表达式
     *
     * @param text 句子
     * @return 一个cron表达式，例如"星期三下午三点三十三", 将会返回"0 33 15 * * 3"
     */
    public static String getCronFromString(String text) {
        List<Term> list = nativeSegment.seg(chiNumTransformation(text));
        List<String> wordList = list.stream().map(item -> item.word).collect(Collectors.toList());
        StringBuilder cron = new StringBuilder();
        cron.append(getSec(wordList))
                .append(getMin(wordList))
                .append(getHour(wordList))
                .append(getDay(wordList))
                .append(getMonth(wordList))
                .append(getWeek(wordList));

        return cron.toString();
    }


    private static String getSec(List<String> wordList) {
        int secPeriodIndex = getIndexBatch(wordList, SEC_PERIOD.getKeywords());
        int secPointIndex = getIndexBatch(wordList, SEC_POINT.getKeywords());

        // 获取秒钟
        // 每*秒属于时间段
        if (secPointIndex >= 1 && (secPointIndex - 2 >= 0 && !"每".equals(wordList.get(secPointIndex - 2)))) {
            int number = Integer.parseInt(wordList.get(secPointIndex - 1));
            return number >= 0
                    ? number + " "
                    : "* ";
        } else if (secPeriodIndex >= 1) {
            int number = Integer.parseInt(wordList.get(secPeriodIndex - 1));
            return number >= 0
                    ? "0/" + number + " "
                    : "* ";
        } else {
            return "0 ";
        }
    }


    private static String getMin(List<String> wordList) {
        int minPeriodIndex = getIndexBatch(wordList, MIN_PERIOD.getKeywords());
        int minPointIndex = getIndexBatch(wordList, MIN_POINT.getKeywords());
        int hourPeriodIndex = getIndexBatch(wordList, HOUR_PERIOD.getKeywords());
        int hourPointIndex = getIndexBatch(wordList, HOUR_POINT.getKeywords());

        // 获取分钟
        // 时间点
        if (minPointIndex >= 1 && (minPointIndex - 2 >= 0 && !"每".equals(wordList.get(minPointIndex - 2)))) {
            int number = Integer.parseInt(wordList.get(minPointIndex - 1));
            return number >= 0
                    ? number + " "
                    : "0 ";
        } else if (minPeriodIndex >= 1) { // 时间断
            int number = Integer.parseInt(wordList.get(minPeriodIndex - 1));
            return number >= 0
                    ? "0/" + number + " "
                    : "* ";
        } else if (hourPeriodIndex >= 1 || hourPointIndex >= 1) { // 例如 3点20，没有分、分钟，但是语义确实表示了分钟
            int hourIndex = hourPeriodIndex >= 0
                    ? hourPeriodIndex + 1
                    : hourPointIndex + 1;
            if (hourIndex < wordList.size()) {
                int number = Integer.parseInt(wordList.get(hourIndex));
                return number >= 0
                        ? number + " "
                        : "0 ";
            } else {
                return "0 ";
            }
        } else {
            return "0 ";
        }
    }


    private static String getHour(List<String> wordList) {
        int hourPeriodIndex = getIndexBatch(wordList, HOUR_PERIOD.getKeywords());
        int hourPointIndex = getIndexBatch(wordList, HOUR_POINT.getKeywords());

        // 获取分钟
        // 时间点
        if (hourPointIndex >= 1) {
            int number = Integer.parseInt(wordList.get(hourPointIndex - 1));
            String timeWord =wordList.get(hourPointIndex - 2);
            if (("晚上".equals(timeWord) || "下午".equals(timeWord)) && number + 12 <= 24) {
                number += 12;
            }
            return number >= 0
                    ? number + " "
                    : "* ";
        } else if (hourPeriodIndex >= 1) {
            int number = Integer.parseInt(wordList.get(hourPeriodIndex - 1));
            String timeWord =wordList.get(hourPeriodIndex - 2);
            if (("晚上".equals(timeWord) || "下午".equals(timeWord)) && number + 12 <= 24) {
                number += 12;
            }
            return number >= 0
                    ? "0/" + number + " "
                    : "* ";
        } else {
            return "* ";
        }
    }


    private static String getDay(List<String> wordList) {
        int dayPeriodIndex = getIndexBatch(wordList, DAY_PERIOD.getKeywords());
        int dayPointIndex = getIndexBatch(wordList, DAY_POINT.getKeywords());
        int monIndex = getIndexBatch(wordList, MONTH.getKeywords());

        if (dayPointIndex >= 1 && (dayPointIndex - 2 >= 0 && !"每".equals(wordList.get(dayPointIndex - 2)))) {
            int number = Integer.parseInt(wordList.get(dayPointIndex - 1));
            return number >= 0
                    ? number + " "
                    : "* ";
        } else if (dayPeriodIndex >= 1) {
            int number = Integer.parseInt(wordList.get(dayPeriodIndex - 1));
            return number >= 0
                    ? "1/" + number + " "
                    : "* ";
        } else if (monIndex >= 1) {
            int monthIndex = monIndex + 1;
            if (monthIndex < wordList.size()) {
                int number = Integer.parseInt(wordList.get(monthIndex));
                return number >= 0
                        ? number + " "
                        : "0 ";
            } else {
                return "* ";
            }
        } else {
            return "* ";
        }
    }


    private static String getMonth(List<String> wordList) {
        int monIndex = getIndexBatch(wordList, MONTH.getKeywords());

        if (monIndex >= 1) {
            int number = Integer.parseInt(wordList.get(monIndex - 1));
            return number >= 0
                    ? number + " "
                    : "* ";
        } else {
            return "* ";
        }
    }


    private static String getWeek(List<String> wordList) {
        int weekIndex = getIndexBatch(wordList, WEEK.getKeywords());
        // 星期
        if (weekIndex >= 0 && weekIndex < wordList.size() - 2) {
            int number = Integer.parseInt(wordList.get(weekIndex + 1));
            return number >= 0
                    ? String.valueOf(number)
                    : "1";
        } else {
            return "?";
        }
    }


    /**
     * 获取strList在list中的位置
     * 如果存在多个，取最前端的位置
     *
     * @param list
     * @param strList
     * @return
     */
    public static int getIndexBatch(List<String> list, String[] strList) {
        List<Integer> result = new ArrayList<>();

        for (String str : strList) {
            int i = list.indexOf(str);
            if (i >= 0) {
                result.add(i);
            }
        }
        if (CollectionUtil.isEmpty(result)) {
            return -1;
        }
        return result.stream().min(Integer::compareTo).orElse(0);
    }


    /**
     * 将句子中所有中文数字转换为阿拉伯数字
     *
     * @param text
     * @return
     */
    public static String chiNumTransformation(String text) {
        char[] chars = text.toCharArray();

        StringBuilder result = new StringBuilder();

        for (int i = 0; i < chars.length; i++) {
            int num = 0;
            boolean unit = false;

            while (i < chars.length && ChiNumEnum.isChiNum(chars[i])) {
                // 直到下一个非数字为止，不断迭代列表的数字字符
                int numByChi = ChiNumEnum.getNumByChi(chars[i]);
                if (numByChi < 10) {
                    // 如果字面量小于10，那么就不是单位量
                    if (!unit) {
                        num *= 10;  // 如果上一个中文数字不是单位，那么进行进位
                    } else if (numByChi == 0) { // 如果是单位，而目前数字为0，直接跳过
                        continue;
                    }
                    num += numByChi; // 进位的数值加上遍历的数字
                    unit = false;
                } else {
                    // 如果汉字的字面量大于等于10，那么代表它是单位量
                    if (num == 0) {
                        // 如果值为0，那么直接加上单位量，比如 百二十 = 100 + 20
                        num += numByChi;
                    } else {
                        // 否则把之前的值乘以单位量，比如三千 = 3 * 1000
                        num *= numByChi;
                        unit = true;
                    }
                }
                // 向后移位
                i++;
            }
            if (num > 0) {
                result.append(num);
            }
            if (i < chars.length) {
                result.append(chars[i]);
            }
        }
        return result.toString();
    }

    public static void main(String args[]) {
        String msg = "星期三下午三点三十三";
        System.out.println(getCronFromString(msg));
    }

}
