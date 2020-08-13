package com.dingdo.util;

import java.util.LinkedList;
import java.util.List;

/**
 * 一些声明信息
 *
 * @author slamacraft
 * @Description:
 * @date: 2020/8/12 17:52
 * @since JDK 1.8
 */
public class PinyinUtil {

    private static List<String> shenmuList = new LinkedList<>();

    static {
        shenmuList.add("b");
        shenmuList.add("p");
        shenmuList.add("m");
        shenmuList.add("f");
        shenmuList.add("d");
        shenmuList.add("t");
        shenmuList.add("n");
        shenmuList.add("l");
        shenmuList.add("g");
        shenmuList.add("k");
        shenmuList.add("h");
        shenmuList.add("j");
        shenmuList.add("q");
        shenmuList.add("x");
        shenmuList.add("z");
        shenmuList.add("c");
        shenmuList.add("s");
        shenmuList.add("r");
        shenmuList.add("y");
        shenmuList.add("w");
    }

    public static boolean isShenmu(String str){
        return shenmuList.contains(str);
    }
}
