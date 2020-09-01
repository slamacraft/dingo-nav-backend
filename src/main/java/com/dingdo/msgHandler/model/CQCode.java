package com.dingdo.msgHandler.model;

import com.dingdo.common.exception.CQCodeConstructException;
import com.dingdo.enums.CQCodeEnum;
import com.dingdo.util.InstructionUtils;
import lombok.Data;

import java.util.Map;

/**
 * 一些声明信息
 *
 * @author slamacraft
 * @Description:
 * @date: 2020/8/31 14:00
 * @since JDK 1.8
 */
@Data
public class CQCode {

    // CQ码类型
    private CQCodeEnum code;
    // CQ码的变量值
    private Map<String, String> values;

    public CQCode(int index, String cqCode) throws CQCodeConstructException {
        String[] params = cqCode.replaceAll("\\[CQ:|]", "").split(",");
        if (params.length < 2) {
            throw new CQCodeConstructException("提取CQ码失败");
        }
        this.code = CQCodeEnum.getCQCode(params[0]);
        this.values = InstructionUtils.analysisInstruction(params);
    }
}
