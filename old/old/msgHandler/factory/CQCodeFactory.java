package com.example.old.msgHandler.factory;

import com.dingdo.common.exception.CQCodeConstructException;
import com.dingdo.enums.CQCodeEnum;
import com.dingdo.msgHandler.model.CQCode;
import org.apache.log4j.Logger;

import java.util.LinkedList;
import java.util.List;
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

    // 使用log4j打印日志
    private static Logger logger = Logger.getLogger(CQCodeFactory.class);


    /**
     * 构造CQCodeList实例
     *
     * @param msg
     * @return
     */
    public static List<CQCode> getCQCodeList(String msg) {
        Pattern p = Pattern.compile("\\[CQ:.*?]");
        Matcher m = p.matcher(msg);

        List<CQCode> resultList = new LinkedList<>();

        while (m.find()) {
            String cqCode = "";
            try {
                cqCode = m.group();
                resultList.add(getCQCodeInstance(cqCode));
            } catch (CQCodeConstructException e) {
                logger.error("CQ码提取异常:" + cqCode, e);
            }
        }

        return resultList;
    }


    /**
     * 获取CQCode实例
     *
     * @param cqCode
     * @return
     * @throws CQCodeConstructException
     */
    public static CQCode getCQCodeInstance(String cqCode) throws CQCodeConstructException {
        String[] params = cqCode.replaceAll("\\[CQ:|]", "").split(",");
        if (params.length < 2) {
            throw new CQCodeConstructException("提取CQ码失败");
        }
        CQCode result = new CQCode();
        result.setCode(CQCodeEnum.getCQCode(params[0]));
        result.setValues(InstructionUtils.analysisInstruction(params));

        return result;
    }

}
