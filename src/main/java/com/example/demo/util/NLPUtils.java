package com.example.demo.util;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.corpus.tag.Nature;
import com.hankcs.hanlp.seg.Segment;
import com.hankcs.hanlp.seg.common.Term;
import com.hankcs.hanlp.suggest.Suggester;

import java.util.List;
import java.util.stream.Collectors;

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

    static {
        // 启用地名识别
        placeSegment.enablePlaceRecognize(true);
        NERSegment.enableAllNamedEntityRecognize(true);
    }

    /**
     * 获取一个全命名实体识别的分词器
     * @return
     */
    public static Segment getNERSegment() {
        return NERSegment;
    }

    /**
     * 获取一个简易分词器
     * @return
     */
    public static Segment getNativeSegment(){
        return nativeSegment;
    }

    /**
     * 获取识别地名的分词器
     * @return
     */
    public static Segment getPlaceSegment(){
        return placeSegment;
    }

    /**
     * 语句抽象化，将指定词性的词替换成磁性的简写
     *
     * @param querySentence
     * @return
     */
    public static List<Term> queryAbstract(String querySentence, String ...natures) {
        // 句子抽象化
        Segment segment = NLPUtils.getPlaceSegment();
        List<Term> terms = segment.seg(querySentence);
        for (Term term : terms) {
            for(String nature : natures){
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
}
