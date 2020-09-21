package com.dingdo.config.customContext;

import com.alibaba.fastjson.JSONObject;
import com.dingdo.common.annotation.Instruction;
import com.dingdo.common.annotation.VerifiAnnotation;
import com.dingdo.common.aspect.VerifiAspect;
import com.dingdo.common.exception.CheckException;
import com.dingdo.common.exception.InstructionExecuteException;
import com.dingdo.component.classifier.NaiveBayesClassifierComponent;
import com.dingdo.enums.ClassicEnum;
import com.dingdo.extendService.MsgExtendService;
import com.dingdo.msgHandler.model.ReqMsg;
import com.dingdo.mvc.entities.RobotManagerEntity;
import com.dingdo.util.FileUtil;
import com.dingdo.util.InstructionUtils;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 指令方法容器
 */
@Component
public class InstructionMethodContext {

    // 使用log4j打印日志
    private static Logger logger = Logger.getLogger(InstructionMethodContext.class);

    private final NaiveBayesClassifierComponent naiveBayesClassifierComponent;
    private final VerifiAspect verifiAspect;

    private static ApplicationContext applicationContext;
    // 指令对应的实例Map
    private Map<String, Object> beanMap = new HashMap<>();
    // 指令对应的方法Map
    private Map<String, Method> methodMap = new HashMap<>();
    // 指令对应方法的错误信息Map
    private Map<Method, String> errorMsgMap = new HashMap<>();
    // 指令的帮助菜单集合
    private Map<String, String> helpMap = new HashMap<>();
    // 功能策略集
    private static final Map<Double, MsgExtendService> extendServiceMap = new HashedMap();
    // 短路指令名称
    private Map<String, String> shortenedInstruction = new HashMap<>();
    // 指令帮助文档路径
    @Value("${resource.doc.helpPath}")
    private String helpDocPath;

    @Autowired
    public InstructionMethodContext(NaiveBayesClassifierComponent naiveBayesClassifierComponent,
                                    VerifiAspect verifiAspect) {
        this.naiveBayesClassifierComponent = naiveBayesClassifierComponent;
        this.verifiAspect = verifiAspect;
    }


    /**
     * 获取applicationContext，并初始化容器
     *
     * @param applicationContext Ioc容器实例
     * @see #initContext
     * @see #initHelpMenu
     * @see #initServiceCollection
     */
    public static void setApplicationContext(ApplicationContext applicationContext) {
        InstructionMethodContext.applicationContext = applicationContext;
        if (applicationContext == null) {
            throw new RuntimeException("方法容器初始化失败：无法获得ApplicationContext");
        }
        InstructionMethodContext thisBean = applicationContext.getBean(InstructionMethodContext.class);

        thisBean.initContext();
        thisBean.initHelpMenu(thisBean.helpDocPath);
        thisBean.initServiceCollection();
    }


    /**
     * 初始化容器
     * 这里主要将含有{@link Instruction}注解的方法以及所在的实例保存至本实例中
     */
    private void initContext() {
        String[] beanDefinitionNames = applicationContext.getBeanDefinitionNames(); // 所有以加载的bean的名称
        for (String beanName : beanDefinitionNames) {
            Object bean = applicationContext.getBean(beanName);
            Method[] methods = bean.getClass().getDeclaredMethods();

            for (Method method : methods) {
                Instruction annotation = AnnotationUtils.findAnnotation(method, Instruction.class);

                if (annotation != null) {
                    String description = annotation.description();
                    String errorMsg = annotation.errorMsg();
                    methodMap.put(description, method);
                    beanMap.put(description, bean);
                    errorMsgMap.put(method, errorMsg);
                }
            }
        }
        logger.info("方法容器准备完毕");
    }


    /**
     * 初始化帮助菜单
     * 本方法会从全局参数{@code resource.doc.helpPath}获取帮助文档地址，并且将
     * 文档以json的仿佛加载到本容器的{@code helpMap}参数中
     *
     * @param helpDocPath 帮助文档地址
     */
    private void initHelpMenu(String helpDocPath) {
        String label = FileUtil.loadFile(helpDocPath);
        JSONObject jsonObject = JSONObject.parseObject(label);

        this.helpMap = ((Map<String, Object>) jsonObject).entrySet()
                .stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey, item -> item.getValue().toString()
                ));
    }


    /**
     * 初始化容器集合
     * 从applicationContext中获取所有继承了{@link MsgExtendService}的Bean，并将得到的Bean保存到
     * {@code Map<Double, MsgExtendService> extendServiceMap}中，作为语义分类后具体的执行实例
     */
    private void initServiceCollection() {
        Map<String, MsgExtendService> beansOfType = applicationContext.getBeansOfType(MsgExtendService.class);
        Collection<MsgExtendService> values = beansOfType.values();
        Iterator<MsgExtendService> iterator = values.iterator();

        for (int i = 0; i < values.size(); i++) {
            MsgExtendService item = iterator.next();
            String simpleName = item.getClass().getSimpleName();
            ClassicEnum enumByServiceName = ClassicEnum.getEnumByServiceName(simpleName);
            if (enumByServiceName != null) {
                extendServiceMap.put(enumByServiceName.getValue(), item);
            }
        }
    }


    /**
     * 获取指令的帮助文档
     *
     * @param reqMsg 请求消息
     * @param params 请求参数
     * @return 请求结果
     */
    @Instruction(description = "帮助", inMenu = false)
    public String help(ReqMsg reqMsg, Map<String, String> params) {
        List<String> paramKeyList = new ArrayList<>(params.keySet());

        StringBuilder result = new StringBuilder();

        for (String param : paramKeyList) {
            String helpInfo = helpMap.get(param);
            if (StringUtils.isNotBlank(helpInfo)) {
                result.append(param).append(":\n").append(helpInfo);
            }
        }

        if (result.length() == 0) {
            return "请问你想了解哪项服务呢";
        }
        return result.toString();
    }


    /**
     * 获取请求的用户能够使用的指令菜单
     *
     * @param reqMsg 请求消息
     * @param params 请求参数
     * @return 请求结果
     */
    @Instruction(description = "菜单", inMenu = false)
    public String menu(ReqMsg reqMsg, Map<String, String> params) {
        StringBuilder result = new StringBuilder();
        List<Method> methodList = methodMap.values().stream().distinct().collect(Collectors.toList());

        RobotManagerEntity robotManager = verifiAspect.getRobotManager(reqMsg.getUserId());

        int index = 1;
        for (Method method : methodList) {
            VerifiAnnotation verifiAnnotation = AnnotationUtils.findAnnotation(method, VerifiAnnotation.class);

            if (verifiAnnotation != null && verifiAspect.checkVerification(reqMsg, verifiAnnotation.level(), robotManager)) {
                continue;
            }

            Instruction instruction = AnnotationUtils.findAnnotation(method, Instruction.class);
            if (Objects.requireNonNull(instruction).inMenu()) {
                result.append(index).append("、").append(instruction.description()).append("\n");
                index++;
            }
        }

        result.append("同样，你也可以通过以下方式访问服务：\n");

        ClassicEnum[] values = ClassicEnum.values();
        List<ClassicEnum> classicEnumList = Arrays.stream(values)
                .filter(item -> StringUtils.isNotBlank(item.getServiceName()))
                .collect(Collectors.toList());

        for (int i = 1; i <= classicEnumList.size(); i++) {
            ClassicEnum classicEnum = classicEnumList.get(i - 1);
            result.append(i).append("、").append(classicEnum.getDescribe()).append("\n");
        }
        return result.toString();
    }


    /**
     * 通过指令获取对应的方法所在实例
     *
     * @param instruction 指令
     * @return 方法所在的实例
     */
    private Object getBeanByInstruction(String instruction) {
        return this.beanMap.get(instruction);
    }


    /**
     * 通过指令获取对应的方法
     *
     * @param instruction 指令
     * @return 对应的方法
     */
    private Method getMethodByInstruction(String instruction) {
        return this.methodMap.get(instruction);
    }


    /**
     * 指令处理方法
     * <p>解析请求的消息，判断请求是属于哪一个具体的功能请求。
     * {@link #invokeShortenedMethod}在使用请求时会判断当前是否是出于某一个功能
     * 请求中（或者叫功能短接），即不再响应或判断是否属于其他的指令请求，所有的指
     * 令请求都会转入之前请求的指令中，直到指令请求过程正常完成。
     *
     * @param reqMsg 请求消息
     * @return 请求的结果
     * @see #invokeMethodByMsg
     */
    public String instructionHandle(ReqMsg reqMsg) {
        String methodInstruction = shortenedInstruction.get(reqMsg.getUserId());
        if (methodInstruction != null) {    // 执行短接方法
            return (String) invokeShortenedMethod(methodInstruction, reqMsg);
        }
        if (!InstructionUtils.DFA(reqMsg.getRawMessage())) {    // 使用DFA确定是否属于指令格式
            return null;
        }
        String result = (String) this.invokeMethodByMsg(reqMsg);
        if (StringUtils.isNotBlank(result)) {
            return result;
        }
        return "未知异常";
    }


    /**
     * 执行短接方法
     * <p>此方法执行时由于未进行指令格式验证，所以不对指令参数进行解析,
     * 转交由具体功能实现自行处理<br>
     * 短接指令会在指令的参数集中自动加入{@code "短接"->"启用"}，由具体的业务
     * 方法对是否是短接请求做出相应的回应<br>
     * 本方法只有返回的语句为非空字符串，才认为短接指令正常完成
     *
     * @param instruction 指令
     * @param reqMsg      请求消息
     * @return 短接指令执行的结果
     */
    private Object invokeShortenedMethod(String instruction, ReqMsg reqMsg) {
        Object target = this.getBeanByInstruction(instruction);
        Method method = this.getMethodByInstruction(instruction);
        HashMap<String, String> params = new HashMap();
        params.put("短接", "启用");

        String result = (String) this.invokeMethod(target, method, reqMsg, params);
        if (StringUtils.isNotBlank(result)) {
            shortenedInstruction.remove(reqMsg.getUserId()); // 直到执行后有返回值，才算短接执行成功
        }

        return result;
    }


    /**
     * 解析消息中包含的指令参数，并执行指令对应的方法
     * <p>
     * 本方法会将指令进行拆解，使用{@link InstructionUtils}工具对拆解的指令进行
     * 解析，将{@code key=value}形式的参数提取为参数集合，同时将后缀表达式{@code -xxx}
     * 提取为{@code xxx=开启}形式的参数，并将指令与参数传递给
     * {@link #invokeMethodByInstruction(String, Object...)}执行
     * </p>
     *
     * @param reqMsg 请求消息
     * @return 指令执行结果
     * @see #invokeMethodByInstruction(String, Object...)
     */
    private Object invokeMethodByMsg(ReqMsg reqMsg) {
        String rawMsg = reqMsg.getRawMessage().replaceAll("[\\s]", " ");
        String instruction = rawMsg.split(" ")[0].split("[.。]")[1];
        Map<String, String> params = InstructionUtils.analysisInstruction(rawMsg.split("[.。]")[1].split(" "));

        CommonParamEnum paramEnum = CommonParamEnum.getParamEnum(params);
        if (paramEnum != null) {
            params.put(instruction, "开启");
            instruction = paramEnum.getDescription();
        }

        return this.invokeMethodByInstruction(instruction, reqMsg, params);
    }


    /**
     * 通过指令参数执行对应的方法
     * <p>
     * 通过传入的{@code instruction}参数执行对应的Method<br>
     * 如果该指令方法属于短接方法，则将该用户的置于短接方法启用中的状态
     * </p>
     *
     * @param instruction 指令
     * @param params      请求消息与指令参数
     * @return 请求的结果
     */
    private Object invokeMethodByInstruction(String instruction, Object... params) {
        Object target = this.getBeanByInstruction(instruction);
        Method method = this.getMethodByInstruction(instruction);
        ReqMsg reqMsg = (ReqMsg) params[0];
        if (method == null) {
            return extendServiceMap.get(naiveBayesClassifierComponent.predict(reqMsg.getRawMessage()))
                    .sendReply(reqMsg);
        }

        // 确认是否是短接指令
        Instruction annotation = method.getAnnotation(Instruction.class);
        if (annotation.isShortened()) {
            shortenedInstruction.put(reqMsg.getUserId(), annotation.description());
        }

        return this.invokeMethod(target, method, params);
    }

    /**
     * 反射执行方法
     * <p>
     *      通过具体的方法与实例，以及方法参数，反射调用方法<br>
     *      如果方法执行异常，则将方法执行的异常消息作为返回值返回
     *      否则将方法{@link Instruction}注解中的{@code errorMsg}作为返回值返回
     * </p>
     *
     * @param target 方法所在的实例
     * @param method 执行的方法
     * @param params 方法执行的参数（必须为{@link ReqMsg}, {@link Map}）
     * @return  方法执行的返回结果
     */
    private Object invokeMethod(Object target, Method method, Object... params) {
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
            Throwable cause = e.getCause();
            if (cause instanceof CheckException) {
                returnValue = ((CheckException) cause).getmessage();
            }
            if (cause instanceof InstructionExecuteException) {
                returnValue = ((InstructionExecuteException) cause).getmessage();
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
     * @param target    执行的实体
     * @param methodName    执行的方法
     * @param params    方法参数
     * @return  方法执行结果
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
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }

        return returnValue;
    }

}
