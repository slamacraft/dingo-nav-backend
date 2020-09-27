package com.dingdo.util.nlp;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.seg.Segment;
import com.hankcs.hanlp.seg.common.Term;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.dingdo.util.nlp.TimeEnum.*;

/**
 * 自然语言处理工具类
 */
public final class NLPUtils {

    // 简易分词器
    private static Segment nativeSegment = HanLP.newSegment();

    // 启用地名识别的分词器
    private static Segment placeSegment = HanLP.newSegment();

    // 启用命名实体识别的分词器
    private static Segment NERSegment = HanLP.newSegment();

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


    /**
     * 从文本中提取cron表达式
     *
     * @param text 文本内容
     * @return cron表达式
     */
    public static String getCronFromString(String text) {
        List<Term> list = nativeSegment.seg(chiNumTranformation(text));
        System.out.println(list);

        List<String> wordList = list.stream().map(item -> item.word).collect(Collectors.toList());

        StringBuilder cron = new StringBuilder();

        int secPeriod = getIndexBatch(wordList, SEC_PERIOD.getKeywords());
        int secPoint = getIndexBatch(wordList, SEC_POINT.getKeywords());
        int minPeriod = getIndexBatch(wordList, MIN_PERIOD.getKeywords());
        int minPoint = getIndexBatch(wordList, MIN_POINT.getKeywords());
        int hourPeriod = getIndexBatch(wordList, HOUR_PERIOD.getKeywords());
        int hourPoint = getIndexBatch(wordList, HOUR_POINT.getKeywords());
        int dayPeriod = getIndexBatch(wordList, DAY_PERIOD.getKeywords());
        int dayPoint = getIndexBatch(wordList, DAY_POINT.getKeywords());
        int mon = getIndexBatch(wordList, MONTH.getKeywords());
        int week = getIndexBatch(wordList, WEEK.getKeywords());

        // 获取秒钟
        // 每*秒属于时间段
        if (secPoint >= 1 && (secPoint - 2 >= 0 && !"每".equals(list.get(secPoint - 2).word))) {
            int number = Integer.valueOf(list.get(secPoint - 1).word);
            if (number >= 0) {
                cron.append(number + " ");
            } else {
                cron.append("* ");
            }
        } else if (secPeriod >= 1) {
            int number = Integer.valueOf(list.get(secPeriod - 1).word);
            if (number >= 0) {
                cron.append("0/" + number + " ");
            } else {
                cron.append("* ");
            }
        } else {
            cron.append("0 ");
        }


        // 获取分钟
        // 时间点
        if (minPoint >= 1 && (minPoint - 2 >= 0 && !"每".equals(list.get(minPoint - 2).word))) {
            int number = Integer.valueOf(list.get(minPoint - 1).word);
            if (number >= 0) {
                cron.append(number + " ");
            } else {
                cron.append("0 ");
            }
        } else if (minPeriod >= 1) { // 时间断
            int number = Integer.valueOf(list.get(minPeriod - 1).word);
            if (number >= 0) {
                cron.append("0/" + number + " ");
            } else {
                cron.append("* ");
            }
        } else if (hourPeriod >= 1 || hourPoint >= 1) { // 例如 3点20，没有分、分钟，但是语义确实表示了分钟
            int hourIndex = 1;
            if (hourPeriod >= 0) {
                hourIndex += hourPeriod;
            } else {
                hourIndex += hourPoint;
            }
            if (hourIndex < list.size()) {
                int number = Integer.valueOf(list.get(hourIndex).word);
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
            int number = Integer.valueOf(list.get(hourPoint - 1).word);
            String timeWord = list.get(hourPoint - 2).word;
            if (hourPoint - 2 >= 0 && ("晚上".equals(timeWord) || "下午".equals(timeWord))) {
                if (number + 12 <= 24) {
                    number += 12;
                }
            }
            if (number >= 0) {
                cron.append(number + " ");
            } else {
                cron.append("* ");
            }
        } else if (hourPeriod >= 1) {
            int number = Integer.valueOf(list.get(hourPeriod - 1).word);
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
            int number = Integer.valueOf(list.get(dayPoint - 1).word);
            if (number >= 0) {
                cron.append(number + " ");
            } else {
                cron.append("* ");
            }
        } else if (dayPeriod >= 1) {
            int number = Integer.valueOf(list.get(dayPeriod - 1).word);
            if (number >= 0) {
                cron.append("1/" + number + " ");
            } else {
                cron.append("* ");
            }
        } else if (mon >= 1) {
            int monIndex = mon + 1;
            if (monIndex < list.size()) {
                int number = Integer.valueOf(list.get(monIndex).word);
                if (number >= 0) {
                    cron.append(number + " ");
                }
            } else {
                cron.append("* ");
            }
        } else {
            cron.append("* ");
        }


        // 月份
        if (mon >= 1) {
            int number = Integer.valueOf(list.get(mon - 1).word);
            if (number >= 0) {
                cron.append(number + " ");
            } else {
                cron.append("* ");
            }
        } else {
            cron.append("* ");
        }


        // 星期
        if (week >= 0 && week < list.size() - 2) {
            int number = Integer.valueOf(list.get(week + 1).word);
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
        if (CollectionUtils.isEmpty(result)) {
            return -1;
        }
        return result.stream().min(Integer::compareTo).get();
    }


    /**
     * 将句子中所有中文数字转换为阿拉伯数字
     *
     * @param text
     * @return
     */
    public static String chiNumTranformation(String text) {
        char[] chars = text.toCharArray();

        StringBuilder result = new StringBuilder();

        for (int i = 0; i < chars.length; i++) {
            int num = 0;
            boolean unit = false;
            while (i < chars.length && ChiNumEnum.isChiNum(chars[i])) {
                Integer numByChi = ChiNumEnum.getNumByChi(chars[i]);
                if (numByChi < 10) {
                    if (!unit) {
                        num *= 10;  // 如果上一个中文数字不是单位
                    } else { // 如果是单位，而目前数字为0
                        if (numByChi == 0) {
                            continue;
                        }
                    }
                    num += numByChi;
                    unit = false;
                } else if (numByChi >= 10) {
                    if (num == 0) {
                        num += numByChi;
                    } else {
                        num *= numByChi;
                        unit = true;
                    }
                }
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
        System.out.println(getCronFromString("下午一点二十"));
    }

}
