package com.dingdo.common.aspect;

import com.dingdo.common.exception.CheckException;

import com.dingdo.model.msgFromMirai.ReqMsg;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Aspect
@Component
public class VerifiAspect {

    @Autowired
    private RedisTemplate redisTemplate;

    @Pointcut("@annotation(com.dingdo.common.annotation.VerifiAnnotation)")
    public void verifiCut() {
    }

    @Before("verifiCut()")
    public void userVerification(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        for (Object item : args) {
            if (item instanceof ReqMsg) {
                ReqMsg reqMsg = (ReqMsg) item;
                String userId = reqMsg.getUserId();
                String password = (String) redisTemplate.opsForValue().get("ManagerServiceImpl$" + userId);
                if (StringUtils.isBlank(password)) {
                    throw new CheckException("该指令为管理员指令，请先登录");
                }
                // 刷新自动下线时间
                redisTemplate.opsForValue().set("ManagerServiceImpl$" + userId, password, 30, TimeUnit.MINUTES);
            }
        }
    }

}
