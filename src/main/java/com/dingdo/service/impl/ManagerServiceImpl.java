package com.dingdo.service.impl;

import com.dingdo.common.annotation.Instruction;
import com.dingdo.common.annotation.VerifiAnnotation;
import com.dingdo.dao.RobotManagerDao;
import com.dingdo.entities.RobotManagerEntity;
import com.dingdo.enums.VerificationEnum;
import com.dingdo.model.msgFromMirai.ReqMsg;
import com.dingdo.service.ManagerService;
import com.dingdo.util.InstructionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class ManagerServiceImpl implements ManagerService {

    @Autowired(required = false)
    private RobotManagerDao robotManagerDao;

    @Autowired
    private RedisTemplate redisTemplate;


    /**
     * 超级管理员注销掉开发人员账户
     *
     * @param reqMsg
     * @param params
     * @return
     */
    @Transactional
    @Instruction(description = "注销账号")
    @VerifiAnnotation(level = VerificationEnum.ROOT)
    public String cancel(ReqMsg reqMsg, Map<String, String> params) {
        String resultMsg = "注销成功！";
        String toCancelId = InstructionUtils.getParamValue(params, "QQCode", "qq号");
        robotManagerDao.deleteById(toCancelId);
        return resultMsg;
    }


    /**
     * 开发人员即以上权限者，注册别人的账号为开发人员
     *
     * @param reqMsg
     * @param params
     * @return
     */
    @Transactional
    @VerifiAnnotation(level = VerificationEnum.DEVELOPER)
    @Instruction(description = "注册账号",
            errorMsg = "设置错误，指令的参数格式为:\n" +
                    "qq号=【数字】】")
    public String register(ReqMsg reqMsg, Map<String, String> params) {
        // 获取并用户id和密码
        String userId = InstructionUtils.getParamValue(params, "QQCode", "qq号");

        // 校验qq号和密码是否为空
        if (userId == null) {
            return "qq号是必须填的！";
        }

        // 查询数据库是否已经存在该用户
        RobotManagerEntity robotManagerEntity = robotManagerDao.selectById(userId);
        if (robotManagerEntity != null) {
            return "这个用户已经是管理员了哦！请不要重复注册";
        }

        // 插入数据
        RobotManagerEntity insertEntity = new RobotManagerEntity();
        insertEntity.setId(userId);
        insertEntity.setLevel(1);
        insertEntity.setCreateBy(reqMsg.getUserId());
        robotManagerDao.insert(insertEntity);
        return "新增管理员成功！";
    }

    public void setManagerStatus(String userId, String password) {
        redisTemplate.opsForValue().set("ManagerServiceImpl$" + userId, password, 30, TimeUnit.MINUTES);
    }
}
