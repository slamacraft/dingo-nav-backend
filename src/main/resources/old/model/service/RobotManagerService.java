package com.dingo.model.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dingo.model.entities.RobotManagerEntity;

/**
 * @author slamacraft
 * @version 1.0
 * @date 2020/9/18 16:06
 * @since JDK 1.8
 */
public interface RobotManagerService extends IService<RobotManagerEntity> {

    /**
     * 注册账号为开发人员
     * @param userId    用户id
     * @return  执行结果
     */
    String register(String userId);

}
