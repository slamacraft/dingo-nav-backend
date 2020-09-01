package com.dingdo.msgHandler.factory;

import com.dingdo.msgHandler.model.CQCodeList;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 一些声明信息
 *
 * @author slamacraft
 * @Description:
 * @date: 2020/8/31 14:53
 * @since JDK 1.8
 */
public class CQCodeFactory {

    public static CQCodeList cqCodeListBuilder(String msg) {
        Pattern p= Pattern.compile("\\[CQ:.*?]");
        Matcher m=p.matcher("你好[CQ:image,url=]!");

        return null;
    }

    public static void main(String args[]) {
        Pattern p= Pattern.compile("\\[CQ:.*?]");
        Matcher m=p.matcher("你好[CQ:image,url=]!");

        String result = "";
        while (m.find()){
            System.out.println(m.group());
        }

    }

}
