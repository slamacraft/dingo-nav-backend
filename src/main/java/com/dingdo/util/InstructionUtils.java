package com.dingdo.util;

import cn.hutool.core.util.CharUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

/**
 * 指令分析工具类
 */
public class InstructionUtils {

    /**
     * 验证语句是否是符合指令规范
     *
     * @param instruction 语句
     * @return
     */
    public static boolean DFA(String instruction) {
        int status = 1; // 有穷自动机状态
        char[] chars = instruction.toCharArray();

        if (StringUtils.isBlank(instruction) || (chars[0] != '.' && chars[0] != '。')) {
            return false;
        }

        for (int i = 0; i < chars.length; i++) {
            switch (status) {
//                case 0: {    // 状态1
//                    if (chars[i] == '.') {
//                        status = 1;
//                    }
//                }
//                break;
                case 1: {
                    if (!CharUtil.isBlankChar(chars[i]) && chars[i] != '=' && chars[i] != '-') { // 数字/字符，转移到状态2
                        status = 2;
                    } else {
                        status = 9; // 直接失败
                    }
                }
                break;
                case 2: {
                    if (CharUtil.isBlankChar(chars[i])) { // 空格字符，转移到状态3
                        status = 3;
                    } else if (chars[i] == '=' || chars[i] == '-') {    // 输入=/-号，直接失败
                        status = 9;
                    }
                }
                break;
                case 3: {
                    if (chars[i] == '=') {    // 输入=号，直接失败
                        status = 9;
                    } else if (chars[i] == '-') {
                        status = 7;
                    } else if (!CharUtil.isBlankChar(chars[i])) { // 数字/字符，转移到状态4
                        status = 4;
                    }
                }
                break;
                case 4: {
                    if (chars[i] == '=') {    // 输入=号，转移到状态6
                        status = 6;
                    } else if (chars[i] == '-') {    // 输入=号，转移到状态6
                        status = 9;
                    } else if (CharUtil.isBlankChar(chars[i])) { // 空格，转移到状态5
                        status = 5;
                    }
                }
                break;
                case 5: {
                    if (chars[i] == '=') {    // 输入=号，转移到状态6
                        status = 6;
                    } else if (!CharUtil.isBlankChar(chars[i])) { // 数字/字符/=号，直接失败
                        status = 9;
                    }
                }
                break;
                case 6: {
                    if (CharUtil.isBlankChar(chars[i])) {    // 空格，转移到状态7
                        status = 7;
                    } else if (chars[i] == '=' || chars[i] == '-') {    // 输入=号，直接失败
                        status = 9;
                    } else { // 数字/字符，转移到状态8
                        status = 2;
                    }
                }
                break;
                case 7: {
                    if (chars[i] == '=' || chars[i] == '-') {    // 输入=号，直接失败
                        status = 9;
                    } else if (!CharUtil.isBlankChar(chars[i])) { // 数字/字符，转移到状态8
                        status = 2;
                    }
                }
                break;
//                case 8: {
//                    if (chars[i] == '=') {    // 输入=号，直接失败
//                        status = 9;
//                    }
//                    if (chars[i] == ' ') {    // 空格，转移到状态3
//                        status = 3;
//                    }
//                }
//                break;
                case 9: {   // 失败状态
                    return false;
                }
            }
        }

        // 结束时停留在2，3，8状态，指令识别成功
        if (status == 2 || status == 3) {
            return true;
        }
        return false;
    }


    /**
     * 解析指令变量
     * 指令规则 args = value
     *
     * @param args
     * @return
     */
    public static Map<String, String> analysisInstruction(String... args) {
        Map<String, String> argsMap = new HashMap<>();
        if (args.length < 2) {
            return argsMap;
        }

        for (int i = 1; i < args.length; i++) {
            if (StringUtils.isBlank(args[i])) {
                continue;
            }
            String[] values = args[i].split("=");
            if(values.length>=2){
                argsMap.put(values[0].trim(), values[1].trim());
            }else {
                String[] arg = args[i].split("-");
                argsMap.put(arg[1].trim(), "开启");
            }

        }
        return argsMap;
    }


    /**
     * 从参数Map中通过参数关键字/中文描述获取参数值
     *
     * @param params
     * @param key
     * @param discrption
     * @return
     */
    public static String getParamValue(Map<String, String> params, String key, String discrption) {
        String result = params.get(key);
        if (result == null) {
            result = params.get(discrption);
        }
        return result;
    }


    /**
     * 从参数Map中通过参数关键字/中文描述获取一个Integer参数值
     *
     * @param params
     * @param key
     * @param discrption
     * @return
     * @throws NumberFormatException
     */
    public static Integer getParamValueOfInteger(Map<String, String> params, String key, String discrption) throws NumberFormatException {
        String value = getParamValue(params, key, discrption);
        if (StringUtils.isBlank(value)) {
            return 0;
        }
        return Integer.valueOf(value);
    }


    /**
     * 从参数Map中通过参数关键字/中文描述获取一个Long参数值
     *
     * @param params
     * @param key
     * @param discrption
     * @return
     * @throws NumberFormatException
     */
    public static Long getParamValueOfLong(Map<String, String> params, String key, String discrption) throws NumberFormatException {
        String value = getParamValue(params, key, discrption);
        if (StringUtils.isBlank(value)) {
            return null;
        }
        Long result = Long.valueOf(value);
        return result;
    }


    /**
     * 从参数Map中通过参数关键字中文描述获取一个Double参数值
     *
     * @param params
     * @param key
     * @param discrption
     * @return
     * @throws NumberFormatException
     */
    public static Double getParamValueOfDouble(Map<String, String> params, String key, String discrption) throws NumberFormatException {
        String value = getParamValue(params, key, discrption);
        if (StringUtils.isBlank(value)) {
            return null;
        }
        Double result = Double.valueOf(value);
        return result;
    }
}
