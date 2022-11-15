package com.dingdo.common.util.nlp

import com.dingdo.common.util.nlp.TimeEnum._
import com.hankcs.hanlp.HanLP
import com.hankcs.hanlp.seg.Segment
import com.hankcs.hanlp.seg.common.Term

import java.util
import scala.collection.JavaConverters._

object NLPUtil {

  // 简易分词器
  private val nativeSegment = HanLP.newSegment

  // 启用地名识别的分词器
  private val placeSegment = HanLP.newSegment.enablePlaceRecognize(true)

  // 启用命名实体识别的分词器
  private val NERSegment = HanLP.newSegment.enableAllNamedEntityRecognize(true)

  /**
   * 获取一个全命名实体识别的分词器
   *
   */
  def getNERSegment: Segment = NERSegment

  /**
   * 获取一个简易分词器
   *
   */
  def getNativeSegment: Segment = nativeSegment

  /**
   * 获取识别地名的分词器
   *
   */
  def getPlaceSegment: Segment = placeSegment

  /**
   * 语句抽象化，将指定词性的词替换成词性的简写
   *
   * @param querySentence 句子
   * @return 返回抽象并分词后的句子词组
   */
  def queryAbstract(querySentence: String, natures: String*): util.List[Term] = {
    // 句子抽象化
    val segment = NLPUtil.getPlaceSegment
    val terms = segment.seg(querySentence).asScala
    for {term <- terms; nature <- natures
         if term.nature.toString == nature} {
      term.word = term.nature.toString
    }
    terms.asJava
  }

  /**
   * 命名实体识别，提取文本中所有的实体
   *
   * @return 分词后的句子词组
   */
  def getNER(msg: String): util.List[Term] = {
    val terms = NERSegment.seg(msg).asScala
    terms.filter((item: Term) => item.nature.toString.matches("^[n|g].*")).toList.asJava
  }


  /**
   * 从自然语言中提取cron表达式
   *
   * @param text 句子
   * @return 一个cron表达式，例如"星期三下午三点三十三", 将会返回"0 33 15 * * 3"
   */
  def getCronFromString(text: String): String = {
    val list = nativeSegment.seg(chiNumTransformation(text)).asScala
    val wordList = list.map((item: Term) => item.word).toList
    getSec(wordList.asJava) +
      getMin(wordList.asJava) +
      getHour(wordList.asJava) +
      getDay(wordList.asJava) +
      getMonth(wordList.asJava) +
      getWeek(wordList.asJava)
  }


  private def getSec(wordList: util.List[String]) = {
    val secPeriodIndex = getIndexBatch(wordList, SEC_PERIOD.getKeywords)
    val secPointIndex = getIndexBatch(wordList, SEC_POINT.getKeywords)

    // 获取秒钟
    // 每*秒属于时间段
    if (secPointIndex >= 1 && (secPointIndex - 2 >= 0 && !("每" == wordList.get(secPointIndex - 2)))) {
      val number = wordList.get(secPointIndex - 1).toInt
      if (number >= 0) s"$number "
      else "* "
    }
    else if (secPeriodIndex >= 1) {
      val number = wordList.get(secPeriodIndex - 1).toInt
      if (number >= 0) s"0/$number "
      else "* "
    }
    else "0 "
  }


  private def getMin(wordList: util.List[String]) = {
    val minPeriodIndex = getIndexBatch(wordList, MIN_PERIOD.getKeywords)
    val minPointIndex = getIndexBatch(wordList, MIN_POINT.getKeywords)
    val hourPeriodIndex = getIndexBatch(wordList, HOUR_PERIOD.getKeywords)
    val hourPointIndex = getIndexBatch(wordList, HOUR_POINT.getKeywords)
    // 获取分钟
    // 时间点
    if (minPointIndex >= 1 && (minPointIndex - 2 >= 0 && !("每" == wordList.get(minPointIndex - 2)))) {
      val number = wordList.get(minPointIndex - 1).toInt
      if (number >= 0) s"$number "
      else "0 "
    }
    else if (minPeriodIndex >= 1) { // 时间断
      val number = wordList.get(minPeriodIndex - 1).toInt
      if (number >= 0) s"0/$number "
      else "* "
    }
    else if (hourPeriodIndex >= 1 || hourPointIndex >= 1) { // 例如 3点20，没有分、分钟，但是语义确实表示了分钟
      val hourIndex = if (hourPeriodIndex >= 0) hourPeriodIndex + 1
      else hourPointIndex + 1
      if (hourIndex < wordList.size) {
        val number = wordList.get(hourIndex).toInt
        if (number >= 0) s"$number "
        else "0 "
      }
      else "0 "
    }
    else "0 "
  }


  private def getHour(wordList: util.List[String]) = {
    val hourPeriodIndex = getIndexBatch(wordList, HOUR_PERIOD.getKeywords)
    val hourPointIndex = getIndexBatch(wordList, HOUR_POINT.getKeywords)

    def getHour_(hour: Int, prefix: String) = {
      if (hour >= 1) {
        var number = wordList.get(hour - 1).toInt
        val timeWord = wordList.get(hour - 2)
        if (("晚上" == timeWord || "下午" == timeWord) && number + 12 <= 24)
          number = if (number == 0) number + 24
          else number + 12
        if (number >= 0) s"$prefix$number "
        else "* "
      }
    }

    if (hourPointIndex >= 1) getHour_(hourPointIndex, "")
    else if (hourPeriodIndex >= 1) getHour_(hourPeriodIndex, "0/")
    else "* "
  }


  private def getDay(wordList: util.List[String]) = {
    val dayPeriodIndex = getIndexBatch(wordList, DAY_PERIOD.getKeywords)
    val dayPointIndex = getIndexBatch(wordList, DAY_POINT.getKeywords)
    val monIndex = getIndexBatch(wordList, MONTH.getKeywords)
    if (dayPointIndex >= 1 && (dayPointIndex - 2 >= 0 && !("每" == wordList.get(dayPointIndex - 2)))) {
      val number = wordList.get(dayPointIndex - 1).toInt
      if (number >= 0) s"$number "
      else "* "
    }
    else if (dayPeriodIndex >= 1) {
      val number = wordList.get(dayPeriodIndex - 1).toInt
      if (number >= 0) s"1/$number "
      else "* "
    }
    else if (monIndex >= 1) {
      val monthIndex = monIndex + 1
      if (monthIndex < wordList.size) {
        val number = wordList.get(monthIndex).toInt
        if (number >= 0) s"$number "
        else "* "
      }
      else "* "
    }
    else "* "
  }


  private def getMonth(wordList: util.List[String]) = {
    val monIndex = getIndexBatch(wordList, MONTH.getKeywords)
    if (monIndex >= 1) {
      val number = wordList.get(monIndex - 1).toInt
      if (number >= 0) s"$number "
      else "* "
    }
    else "* "
  }


  private def getWeek(wordList: util.List[String]) = {
    val weekIndex = getIndexBatch(wordList, WEEK.getKeywords)
    // 星期
    if (weekIndex >= 0 && weekIndex < wordList.size - 2) {
      val number = wordList.get(weekIndex + 1).toInt
      if (number >= 0) number.toString
      else "1"
    }
    else "?"
  }


  /**
   * 获取strList在list中的位置
   * 如果存在多个，取最前端的位置
   *
   * @param list    单词列表
   * @param strList 想要查询单词的集合
   * @return 单词的位置
   */
  def getIndexBatch(list: util.List[String], strList: Array[String]): Int = {
    val result = for {str <- strList; i = list.indexOf(str) if i >= 0} yield i
    if (result.length == 0) -1
    else result.min
  }


  /**
   * 将句子中所有中文数字转换为阿拉伯数字
   *
   * @param text 文本
   * @return 改为阿拉伯数字的句子
   */
  def chiNumTransformation(text: String): String = {
    val chars = text.toCharArray
    val result = new StringBuilder
    var i = 0
    while (i < chars.length) {
      var num = 0
      var unit = false
      var zero = true
      var isNum = false
      while (i < chars.length && ChiNumEnum.isChiNum(chars(i))) {
        // 直到下一个非数字为止，不断迭代列表的数字字符
        val numByChi = ChiNumEnum.getNumByChi(chars(i))
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
        }
        else { // 如果汉字的字面量大于等于10，那么代表它是单位量
          if (num == 0) { // 如果值为0，那么直接加上单位量，比如 百二十 = 100 + 20
            num += numByChi
          }
          else { // 否则把之前的值乘以单位量，比如三千 = 3 * 1000
            num *= numByChi
            unit = true
          }
        }
        // 向后移位
        i += 1
      }
      if (num > 0 || (zero && isNum)) result.append(num)
      if (i < chars.length) result.append(chars(i))

      i += 1
    }
    result.toString
  }

  def main(args: Array[String]): Unit = {
    val msg = "二月三号下午零点三十三"
    System.out.println(getCronFromString(msg))
  }
}
