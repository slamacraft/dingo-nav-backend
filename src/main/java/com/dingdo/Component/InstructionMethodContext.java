package com.dingdo.Component;

import com.dingdo.Component.classifier.NaiveBayesClassifierComponent;
import com.dingdo.Component.classifier.NaiveBayesComponent;
import com.dingdo.common.annotation.Instruction;
import com.dingdo.common.annotation.VerifiAnnotation;
import com.dingdo.common.exception.CheckException;

import com.dingdo.enums.ClassicEnum;
import com.dingdo.extendService.MsgExtendService;
import com.dingdo.model.msgFromMirai.ReqMsg;
import com.dingdo.util.InstructionUtils;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 指令方法容器
 */
@Component
public class InstructionMethodContext {

    @Autowired
    private NaiveBayesClassifierComponent naiveBayesClassifierComponent;

    private static ApplicationContext applicationContext;
    // 指令对应的实例Map
    private Map<String, Object> beanMap = new HashMap<>();
    // 指令对应的方法Map
    private Map<String, Method> methodMap = new HashMap<>();
    // 指令对应方法的错误信息Map
    private Map<Method, String> errorMsgMap = new HashMap<>();

    protected static final Map<Double, MsgExtendService> extendServiceMap = new HashedMap();


    /**
     * 获取applicationContext，并初始化容器
     *
     * @param applicationContext
     */
    public static void setApplicationContext(ApplicationContext applicationContext) {
        InstructionMethodContext.applicationContext = applicationContext;
        if (applicationContext == null && applicationContext != null) {
            InstructionMethodContext.applicationContext = applicationContext;
        }
        InstructionMethodContext thisBean = applicationContext.getBean(InstructionMethodContext.class);
        thisBean.initContext();

        Map<String, MsgExtendService> beansOfType = applicationContext.getBeansOfType(MsgExtendService.class);
        Collection<MsgExtendService> values = beansOfType.values();
        Iterator<MsgExtendService> iterator = values.iterator();
        for (int i = 0; i < values.size(); i++) {
            MsgExtendService item = iterator.next();
            String simpleName = item.getClass().getSimpleName();
            ClassicEnum enumByServiceName = ClassicEnum.getEnumByServiceName(simpleName);
            if(enumByServiceName != null){
                extendServiceMap.put(enumByServiceName.getValue(), item);
            }
        }
    }

    /**
     * 初始化容器
     * 这里主要将含有@Instruction注解的方法以及所在的实例保存至本地Map中
     */
    public void initContext() {
        String[] beanDefinitionNames = applicationContext.getBeanDefinitionNames(); // 所有以加载的bean的名称
        for (String beanName : beanDefinitionNames) {
            Object bean = applicationContext.getBean(beanName);
            Method[] methods = bean.getClass().getDeclaredMethods();
            for (Method method : methods) {
                // 由于使用aop代理得到的对象并不是目标对象，所以无法获得方法上的注解
//                Instruction annotation = method.getAnnotation(Instruction.class);   // 获取该方法上的该注解
                // 正确的方式是使用注解工具在目标对象上查找注解
                Instruction annotation = AnnotationUtils.findAnnotation(method, Instruction.class);
                if (annotation != null) {
                    String name = annotation.name();
                    String description = annotation.description();
                    String errorMsg = annotation.errorMsg();
                    methodMap.put(name, method);
                    methodMap.put(description, method);
                    beanMap.put(name, bean);
                    beanMap.put(description, bean);
                    errorMsgMap.put(method, errorMsg);
                }
            }
        }
        System.out.println("方法容器准备完毕");
    }

    @Instruction(name = "help", description = "菜单", inMenu = false)
    public String help(ReqMsg reqMsg, Map<String, String> params) {
        StringBuffer result = new StringBuffer();
        List<Method> methodList = methodMap.values().stream().distinct().collect(Collectors.toList());
        int index = 1;
        for (int i = 0; i < methodList.size(); i++) {
            Method method = methodList.get(i);
            VerifiAnnotation verifiAnnotation = AnnotationUtils.findAnnotation(method, VerifiAnnotation.class);
            if (verifiAnnotation != null) {
                continue;
            }
            Instruction instruction = AnnotationUtils.findAnnotation(method, Instruction.class);
            if(instruction.inMenu()){
                result.append(index + "、" + instruction.description() + "\n");
                index++;
            }
        }
        return result.toString();
    }

    @VerifiAnnotation
    @Instruction(name = "manager", description = "管理员", inMenu = false)
    public String manager(ReqMsg reqMsg, Map<String, String> params) {
        StringBuffer result = new StringBuffer();
        List<Method> methodList = methodMap.values().stream().distinct().collect(Collectors.toList());
        int index = 1;
        for (int i = 0; i < methodList.size(); i++) {
            Method method = methodList.get(i);
            VerifiAnnotation verifiAnnotation = AnnotationUtils.findAnnotation(method, VerifiAnnotation.class);
            if (verifiAnnotation == null) {
                continue;
            }
            Instruction instruction = AnnotationUtils.findAnnotation(method, Instruction.class);
            result.append(index + "、" + instruction.description() + "\n");
            index++;
        }
        return result.toString();
    }

    public Object getBeanByInstruction(String instruction) {
        return this.beanMap.get(instruction);
    }

    public Method getMethodByInstruction(String instruction) {
        return this.methodMap.get(instruction);
    }

    /**
     * 通过消息包含的指令执行对应的方法
     *
     * @param reqMsg
     * @return
     */
    public Object invokeMethodByMsg(ReqMsg reqMsg) {
        String rawMsg = reqMsg.getRawMessage();
        String instruction = rawMsg.split(" ")[0].split("\\.|。")[1];
        Map<String, String> params = InstructionUtils.analysisInstruction(rawMsg.split("\\.|。")[1].split(" "));
        return this.invokeMethodByInstruction(instruction, reqMsg, params);
    }

    /**
     * 通过指令参数执行对应的方法
     *
     * @param instruction
     * @param params
     * @return
     */
    public Object invokeMethodByInstruction(String instruction, Object... params) {
        Object target = this.getBeanByInstruction(instruction);
        Method method = this.getMethodByInstruction(instruction);
        if (method == null) {
            ReqMsg reqMsg = (ReqMsg)params[0];
            return extendServiceMap.get(naiveBayesClassifierComponent.predict(reqMsg.getRawMessage()))
                    .sendReply(reqMsg);
        }
        return this.invokeMethod(target, method, params);
    }

    /**
     * 反射执行方法
     *
     * @param target
     * @param method
     * @param params
     * @return
     */
    public Object invokeMethod(Object target, Method method, Object... params) {
        Object returnValue = null;

        try {
            ReflectionUtils.makeAccessible(method);
            if (null != params && params.length > 0) {
                returnValue = method.invoke(target, params);
            } else {
                returnValue = method.invoke(target);
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.getCause().printStackTrace();
            if (e.getCause() instanceof CheckException) {
                returnValue = ((CheckException) e.getCause()).getmessage();
            }
        } catch (Exception e) {
            e.printStackTrace();
            returnValue = errorMsgMap.get(method);
        }

        return returnValue;
    }

    /**
     * 通过反射执行方法的静态方法
     *
     * @param target
     * @param methodName
     * @param params
     * @return
     */
    public static Object invokeMethodByName(Object target, String methodName, Object... params) {
        Object returnValue = null;

        Method method = null;
        try {
            if (null != params && params.length > 0) {
                Class<?>[] paramCls = new Class[params.length];
                for (int i = 0; i < params.length; i++) {
                    paramCls[i] = params[i].getClass();
                }
                method = target.getClass().getDeclaredMethod(methodName, paramCls);
            } else {
                method = target.getClass().getDeclaredMethod(methodName);
            }
            ReflectionUtils.makeAccessible(method);
            if (null != params && params.length > 0) {
                returnValue = method.invoke(target, params);
            } else {
                returnValue = method.invoke(target);
            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        return returnValue;
    }

}
