package com.example.demo.service.impl;

import com.example.demo.common.annotation.Instruction;
import com.example.demo.common.annotation.VerifiAnnotation;
import com.example.demo.dao.RobotManagerDao;
import com.example.demo.entities.RobotManagerEntity;
import com.example.demo.model.msgFromCQ.ReceiveMsg;
import com.example.demo.model.msgFromCQ.ReplyMsg;
import com.example.demo.service.ManagerService;
import com.example.demo.util.InstructionUtils;
import org.apache.hadoop.yarn.webapp.hamlet.Hamlet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class ManagerServiceImpl implements ManagerService {

    @Autowired(required = false)
    private RobotManagerDao robotManagerDao;
    @Autowired
    private RedisTemplate redisTemplate;

    @Instruction(name = "login", descrption = "登录",
        errorMsg = "登录，指令的参数格式为:\n" +
                "密码=【字符】")
    public String login(ReceiveMsg receiveMsg, Map<String, String> params) {
        // 获取并用户id和密码
        Long userId = InstructionUtils.getParamValueOfLong(params, "id", "qq号");
        if (userId == null) {
            userId = receiveMsg.getSender().getUser_id();
        }
        String password = InstructionUtils.getParamValue(params, "password", "密码");
        if (password == null) {
            return "密码要必须要填！";
        }

        // 校验id是否存在
        RobotManagerEntity robotManagerEntity = robotManagerDao.selectById(userId);
        if (robotManagerEntity == null) {
            return "很遗憾，你还不是管理员~";
        }

        // 校验密码是否正确
        if (!robotManagerEntity.getPassword().equals(password)) {
            return "很遗憾，密码不对~";
        }

        // 登录成功！
        redisTemplate.opsForValue().set(Long.toString(receiveMsg.getSender().getUser_id()), password, 30, TimeUnit.MINUTES);
        return "登录成功！欢迎使用~";
    }


    @Instruction(name = "cancel", descrption = "注销")
    @VerifiAnnotation
    public String cancel(ReceiveMsg receiveMsg, Map<String, String> params) {
        String resultMsg = "注销成功！";
        try {
            redisTemplate.delete(Long.toString(receiveMsg.getSender().getUser_id()));
        } catch (Exception e) {
            resultMsg = "注销失败，你还没登录呢";
        }

        return resultMsg;
    }

    @VerifiAnnotation
    @Instruction(name = "register", descrption = "注册",
        errorMsg = "设置错误，指令的参数格式为:\n" +
            "qq号=【数字】 密码=【字符】")
    public String register(ReceiveMsg receiveMsg, Map<String, String> params) {
        // 获取并用户id和密码
        Long userId = InstructionUtils.getParamValueOfLong(params, "id", "qq号");
        String password = InstructionUtils.getParamValue(params, "password", "密码");

        // 校验qq号和密码是否为空
        if (userId == null || password == null) {
            return "qq号和密码都是必须填的！";
        }

        // 查询数据库是否已经存在该用户
        RobotManagerEntity robotManagerEntity = robotManagerDao.selectById(userId);
        if (robotManagerEntity != null) {
            return "这个用户已经是管理员了哦！请不要重复注册";
        }

        // 插入数据
        RobotManagerEntity insertEntity = new RobotManagerEntity();
        insertEntity.setId(userId);
        insertEntity.setNickName(receiveMsg.getSender().getNickname());
        insertEntity.setPassword(password);
        robotManagerDao.insert(insertEntity);
        return "新增管理员成功！";
    }

}
