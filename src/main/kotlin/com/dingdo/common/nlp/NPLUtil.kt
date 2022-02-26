package com.dingdo.common.nlp

import com.dingdo.common.nlp.TimeEnum.*
import com.hankcs.hanlp.HanLP
import com.hankcs.hanlp.seg.Segment
import com.hankcs.hanlp.seg.common.Term
import java.util.*

object NPLUtil {

    // 简易分词器
    private val nativeSegment = HanLP.newSegment()

    // 启用地名识别的分词器
    private val placeSegment = HanLP.newSegment().enablePlaceRecognize(true)

    // 启用命名实体识别的分词器
    private val NERSegment = HanLP.newSegment().enableAllNamedEntityRecognize(true)

    /**
     * 获取一个全命名实体识别的分词器
     *
     */
    fun getNERSegment(): Segment = NERSegment

    /**
     * 获取一个简易分词器
     *
     */
    fun getNativeSegment(): Segment = nativeSegment

    /**
     * 获取识别地名的分词器
     *
     */
    fun getPlaceSegment(): Segment = placeSegment

    /**
     * 语句抽象化，将指定词性的词替换成词性的简写
     *
     * @param querySentence 句子
     * @return 返回抽象并分词后的句子词组
     */
    fun queryAbstract(querySentence: String, natures: Array<String>): List<Term> {
        // 句子抽象化
        val segment = getPlaceSegment()
        val terms = segment.seg(querySentence)
        for (term in terms) {
            for (nature in natures) {
                if (term.nature.toString() == nature) {
                    term.word = term.nature.toString()
                }
            }
        }
        return terms
    }

    /**
     * 命名实体识别，提取文本中所有的实体
     *
     * @return 分词后的句子词组
     */
    fun getNER(msg: String): List<Term> {
        val terms = NERSegment.seg(msg)
        // "^[n|g].*"
        return terms.filter { it.nature.toString().matches(Regex("^[n|g].*")) }
            .toList()
    }


    /**
     * 从自然语言中提取cron表达式
     *
     * @param text 句子
     * @return 一个cron表达式，例如"星期三下午三点三十三", 将会返回"0 33 15 * * 3"
     */
    fun getCronFromString(text: String): String {
        val list = nativeSegment.seg(chiNumTransformation(text))
        val wordList = list.map { item -> item.word }
        return getSec(wordList) +
                getMin(wordList) +
                getHour(wordList) +
                getDay(wordList) +
                getMonth(wordList) +
                getWeek(wordList)
    }


    private fun getSec(wordList: List<String>): String {
        val secPeriodIndex = getIndexBatch(wordList, SEC_PERIOD.keywords)
        val secPointIndex = getIndexBatch(wordList, SEC_POINT.keywords)

        // 获取秒钟
        // 每*秒属于时间段
        return if (secPointIndex >= 1 && (secPointIndex - 2 >= 0 && "每" != wordList[secPointIndex - 2])) {
            val number = wordList[secPointIndex - 1].toInt()
            if (number >= 0) "$number "
            else "* "
        } else if (secPeriodIndex >= 1) {
            val number = wordList[secPeriodIndex - 1].toInt()
            if (number >= 0) "0/$number "
            else "* "
        } else "0 "
    }


    private fun getMin(wordList: List<String>): String {
        val minPeriodIndex = getIndexBatch(wordList, MIN_PERIOD.keywords)
        val minPointIndex = getIndexBatch(wordList, MIN_POINT.keywords)
        val hourPeriodIndex = getIndexBatch(wordList, HOUR_PERIOD.keywords)
        val hourPointIndex = getIndexBatch(wordList, HOUR_POINT.keywords)
        // 获取分钟
        // 时间点
        return if (minPointIndex >= 1 && (minPointIndex - 2 >= 0 && "每" != wordList[minPointIndex - 2])) {
            val number = wordList[minPointIndex - 1].toInt()
            if (number >= 0) "$number "
            else "0 "
        } else if (minPeriodIndex >= 1) { // 时间断
            val number = wordList[minPeriodIndex - 1].toInt()
            if (number >= 0) "0/$number "
            else "* "
        } else if (hourPeriodIndex >= 1 || hourPointIndex >= 1) { // 例如 3点20，没有分、分钟，但是语义确实表示了分钟
            val hourIndex = if (hourPeriodIndex >= 0) hourPeriodIndex + 1
            else hourPointIndex + 1
            if (hourIndex < wordList.size) {
                val number = wordList[hourIndex].toInt()
                if (number >= 0) "$number "
                else "0 "
            } else "0 "
        } else "0 "
    }


    private fun getHour(wordList: List<String>): String {
        val hourPeriodIndex = getIndexBatch(wordList, HOUR_PERIOD.keywords)
        val hourPointIndex = getIndexBatch(wordList, HOUR_POINT.keywords)

        fun _getHour(hour: Int, prefix: String): String {
            if (hour >= 1) {
                var number = wordList[hour - 1].toInt()
                val timeWord = wordList[hour - 2]

                if (("晚上" == timeWord || "下午" == timeWord) && number + 12 <= 24)
                    number = if (number == 0) {
                        number + 24
                    } else {
                        number + 12
                    }

                return if (number >= 0) "$prefix$number "
                else "* "
            }
            return "* "
        }

        return when {
            hourPointIndex >= 1 -> _getHour(hourPointIndex, "")
            hourPeriodIndex >= 1 -> _getHour(hourPeriodIndex, "0/")
            else -> "* "
        }
    }


    private fun getDay(wordList: List<String>): String {
        val dayPeriodIndex = getIndexBatch(wordList, DAY_PERIOD.keywords)
        val dayPointIndex = getIndexBatch(wordList, DAY_POINT.keywords)
        val monIndex = getIndexBatch(wordList, MONTH.keywords)
        return if (dayPointIndex >= 1 && (dayPointIndex - 2 >= 0 && "每" != wordList[dayPointIndex - 2])) {
            val number = wordList[dayPointIndex - 1].toInt()
            if (number >= 0) "$number "
            else "* "
        } else if (dayPeriodIndex >= 1) {
            val number = wordList[dayPeriodIndex - 1].toInt()
            if (number >= 0) "1/$number "
            else "* "
        } else if (monIndex >= 1) {
            val monthIndex = monIndex + 1
            if (monthIndex < wordList.size) {
                val number = wordList[monthIndex].toInt()
                if (number >= 0) "$number "
                else "* "
            } else "* "
        } else "* "
    }


    private fun getMonth(wordList: List<String>): String {
        val monIndex = getIndexBatch(wordList, MONTH.keywords)
        return if (monIndex >= 1) {
            val number = wordList[monIndex - 1].toInt()
            if (number >= 0) "$number "
            else "* "
        } else "* "
    }


    private fun getWeek(wordList: List<String>): String {
        val weekIndex = getIndexBatch(wordList, WEEK.keywords)
        // 星期
        return if (weekIndex >= 0 && weekIndex < wordList.size - 2) {
            val number = wordList[weekIndex + 1].toInt()
            if (number >= 0) number.toString()
            else "1"
        } else "?"
    }


    /**
     * 获取strList在list中的位置
     * 如果存在多个，取最前端的位置
     *
     * @param list    单词列表
     * @param strList 想要查询单词的集合
     * @return 单词的位置
     */
    private fun getIndexBatch(list: List<String>, strList: Array<String>): Int {
        val minIndex = strList.map { list.indexOf(it) }
            .filter { it > -1 }
            .minOrNull()
        return Optional.ofNullable(minIndex)
            .orElse(-1)
    }


    /**
     * 将句子中所有中文数字转换为阿拉伯数字
     *
     * @param text 文本
     * @return 改为阿拉伯数字的句子
     */
    private fun chiNumTransformation(text: String): String {
        val chars = text.toCharArray()
        val result = StringBuilder()
        var i = 0
        while (i < chars.size) {
            var num = 0
            var unit = false
            var zero = true
            var isNum = false
            while (i < chars.size && ChiNumEnum.isChiNum(chars[i])) {
                // 直到下一个非数字为止，不断迭代列表的数字字符
                val numByChi = ChiNumEnum.getNumByChi(chars[i])
                if (numByChi > 0) zero = false
                isNum = true
                if (numByChi < 10) {
                    // 如果字面量小于10，那么就不是单位量
                    // 如果上一个中文数字不是单位，那么进行进位
                    if (!unit) num *= 10
                    if (numByChi >= 0) {
                        // 跳过前置零
                        num += numByChi // 进位的数值加上遍历的数字
                        unit = false
                    }
                } else { // 如果汉字的字面量大于等于10，那么代表它是单位量
                    if (num == 0) { // 如果值为0，那么直接加上单位量，比如 百二十 = 100 + 20
                        num += numByChi
                    } else { // 否则把之前的值乘以单位量，比如三千 = 3 * 1000
                        num *= numByChi
                        unit = true
                    }
                }
                // 向后移位
                i += 1
            }
            if (num > 0 || (zero && isNum)) result.append(num)
            if (i < chars.size) result.append(chars[i])

            i += 1
        }
        return result.toString()
    }
}

fun main() {
    val msg = "二月三号下午零点三十三"
    println(NPLUtil.getCronFromString(msg))
}
