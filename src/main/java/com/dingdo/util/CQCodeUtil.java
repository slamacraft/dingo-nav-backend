package com.dingdo.util;

/**
 * 酷q码工具类
 *
 * @author slamacraft
 * @Description:
 * @date: 2020/8/31 8:40
 * @since JDK 1.8
 */
public class CQCodeUtil {

    /**
     * 移除句子中的CQ码
     * @return
     */
    public static String removeAllCQCode(String msg){
        return msg.replaceAll("\\[CQ:.*?]", "");
    }


    /**
     * 在句首at某人
     *
     * @param msg  句子
     * @param userId    艾特的用户qq号
     */
    public static String atTarget(String msg, String userId) {
        return "[CQ:at,qq=" + userId + "]" + msg;
    }


    /**
     * 在句子某个位置at某人
     * @param msg  句子
     * @param userId    艾特的qq号
     * @param index 插入at的位置
     * @return
     */
    public static String atTarget(String msg, String userId, int index){
        return msg.substring(0, index) + atTarget(msg.substring(index, msg.length()), userId);
    }


    /**
     * 删除句子中的at某人
     *
     * @param msg   句子
     * @param userId    用户qq
     */
    public static String removeAtUser(String msg, String userId) {
        return msg.replaceAll("\\[CQ:at,qq=" + userId + ".*?\\]", "");
    }


    /**
     * 删除句子中所有的at某人
     *
     * @param msg   句子
     */
    private String removeAtUser(String msg) {
        return msg.replaceAll("\\[CQ:at,qq=.*?\\]", "");
    }

}
