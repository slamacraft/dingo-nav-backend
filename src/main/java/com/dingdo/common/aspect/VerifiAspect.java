package com.dingdo.common.aspect;

import com.dingdo.common.exception.CheckException;
import com.dingdo.model.msgFromCQ.ReceiveMsg;
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
            if (item instanceof ReceiveMsg) {
                ReceiveMsg receiveMsg = (ReceiveMsg) item;
                Long userId = receiveMsg.getUser_id();
                String password = (String) redisTemplate.opsForValue().get(Long.toString(userId));
                if (StringUtils.isBlank(password)) {
                    throw new CheckException("该指令为管理员指令，请先登录");
                }
                // 刷新自动下线时间
                redisTemplate.opsForValue().set(Long.toString(userId), password, 30, TimeUnit.MINUTES);
            }
        }
    }

}
