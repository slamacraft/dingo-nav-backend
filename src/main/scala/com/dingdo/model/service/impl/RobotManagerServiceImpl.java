//package com.dingdo.model.service.impl;
//
//import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
//import com.dingdo.common.annotation.Instruction;
//import com.dingdo.common.annotation.VerifiAnnotation;
//import com.dingdo.common.exception.BusinessException;
//import com.dingdo.enums.VerificationEnum;
//import com.dingdo.msgHandler.model.ReqMsg;
//import com.dingdo.model.entities.RobotManagerEntity;
//import com.dingdo.model.mapper.RobotManagerMapper;
//import com.dingdo.model.service.RobotManagerService;
//import com.dingdo.util.InstructionUtils;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.Map;
//
///**
// * @author slamacraft
// * @version 1.0
// * @date 2020/9/18 16:06
// * @since JDK 1.8
// */
//@Service
//public class RobotManagerServiceImpl extends ServiceImpl<RobotManagerMapper, RobotManagerEntity> implements RobotManagerService {
//
//
//    /**
//     * 超级管理员注销开发人员账户
//     *
//     * @param reqMsg    机器人消息
//     * @param params    机器人请求参数
//     * @return  执行消息
//     */
//    @Transactional
//    @Instruction(description = "注销账号")
//    @VerifiAnnotation(level = VerificationEnum.ROOT)
//    public String cancel(ReqMsg reqMsg, Map<String, String> params) {
//        String toCancelId = InstructionUtils.getParamValue(params, "QQCode", "qq号");
//        baseMapper.deleteById(toCancelId);
//        return "注销成功！";
//    }
//
//
//    /**
//     * 指令调用入口
//     *
//     * @param reqMsg    机器人消息
//     * @param params    机器人请求参数
//     * @return  执行消息
//     * @see #register(String)
//     */
//    @Transactional
//    @VerifiAnnotation(level = VerificationEnum.DEVELOPER)
//    @Instruction(description = "注册账号",
//            errorMsg = "设置错误，指令的参数格式为:\n" +
//                    "qq号=【数字】】")
//    public String register(ReqMsg reqMsg, Map<String, String> params) {
//        String userId = InstructionUtils.getParamValue(params, "QQCode", "qq号");
//        return register(userId);
//    }
//
//
//    @Transactional
//    public String register(String userId){
//        // 校验qq号和密码是否为空
//        if (userId == null) {
//            throw new BusinessException("qq号是必须填的！");
//        }
//
//        // 查询数据库是否已经存在该用户
//        RobotManagerEntity robotManagerEntity = baseMapper.selectById(userId);
//        if (robotManagerEntity != null) {
//            throw new BusinessException("这个用户已经是管理员了哦！请不要重复注册");
//        }
//
//        // 插入数据
//        RobotManagerEntity insertEntity = new RobotManagerEntity();
//        insertEntity.setId(userId);
//        insertEntity.setLevel(1);
//        insertEntity.setCreateBy(userId);
//        baseMapper.insert(insertEntity);
//        return "新增管理员成功！";
//    }
//
//}
