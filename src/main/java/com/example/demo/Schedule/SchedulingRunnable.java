package com.example.demo.Schedule;

import com.example.demo.Component.InstructionMethodContext;
import com.example.demo.util.SpringContextUtils;
import org.apache.log4j.Logger;

import java.util.Objects;

/**
 * 定时任务的执行类
 */
public class SchedulingRunnable implements Runnable {
    private static Logger logger = Logger.getLogger(SchedulingRunnable.class);

    private String beanName;
    private String methodName;
    private Object[] params;

    /**
     * 没有执行参数的构造方法
     * @param beanName
     * @param methodName
     */
    public SchedulingRunnable(String beanName, String methodName) {
        this.beanName = beanName;
        this.methodName = methodName;
    }

    /**
     * 有执行参数的构造方法
     * @param beanName
     * @param methodName
     * @param params
     */
    public SchedulingRunnable(String beanName, String methodName, Object... params) {
        this.beanName = beanName;
        this.methodName = methodName;
        this.params = params;
    }

    /**
     * 这里实际上是通过反射调用其他方法，等于是让这个run（）方法变成其他方法进行定时任务
     */
    @Override
    public void run() {
        logger.info("定时任务开始执行 - bean：{" + beanName + "}，方法：{" + methodName + "}，参数：{" + params + "}");
        long startTime = System.currentTimeMillis();

        try {
            // 通过方法名反射调用具体的方法
            Object target = SpringContextUtils.getBean(beanName);
            InstructionMethodContext.invokeMethodByName(target, methodName, params);
        } catch (Exception ex) {
            logger.error(String.format("定时任务执行异常 - bean：%s，方法：%s，参数：%s ", beanName, methodName, params), ex);
        }

        long times = System.currentTimeMillis() - startTime;
        logger.info(String.format("定时任务执行结束 - bean：%s，方法：%s，参数：%s，耗时：%d 毫秒", beanName, methodName, params, times));
    }

    /**
     * 重写equals方法，判断2个定时任务是否相同
     * Tips：如果beanName相同，methodName相同，输入的参数能够匹配上，那么2个定时任务相同
     * @param obj
     * @return
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || this == null) return false;
        if (getClass() != obj.getClass()) return false;

        SchedulingRunnable that = (SchedulingRunnable) obj;
        if (params == null) {
            return beanName.equals(that.beanName) &&
                    methodName.equals(that.methodName) &&
                    that.params == null;
        }

        boolean flag = true;
        int length = params.length > that.params.length ? params.length : that.params.length;
        for (int i = 0; i < length; i++) {
            if (!params[i].equals(that.params[i])) {
                flag = false;
                break;
            }
        }
        return beanName.equals(that.beanName) &&
                methodName.equals(that.methodName) &&
                flag;
    }

    /**
     * 最暴力的方法，将属性拼接成为字符串
     * @return
     */
    @Override
    public int hashCode() {
        if (params == null) {
            return Objects.hash(beanName + methodName);
        }
        String paramString = "";
        for (int i = 0; i < params.length; i++) {
            paramString += params[i];
        }
        return Objects.hash(beanName + methodName + paramString);
    }
}
