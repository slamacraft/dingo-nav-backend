package com.dingdo.component.schedule.factory;

import com.dingdo.component.schedule.model.interfacor.ITaskInfo;
import com.dingdo.component.schedule.model.interfacor.ITaskList;
import com.dingdo.util.scanner.PackageScanner;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 定时任务的构造工厂
 * 对指定包下的所有类进行搜索，获取所有继承ITaskList和ITaskInfo接口的类型
 *
 * @author slamacraft
 * @date: 2020/8/27 19:05
 * @since JDK 1.8
 */
@Component
public class TaskFactory<TaskList extends ITaskList, TaskInfo extends ITaskInfo> {

    private final Map<Class<TaskInfo>, Class<TaskList>> taskClazzMap = new HashMap<>();


    /**
     * 无参数构造方法
     * 默认搜索的包路径为
     *
     * @throws ClassNotFoundException   class未发现异常
     */
    public TaskFactory() throws ClassNotFoundException {
        this("com.dingdo.component.schedule");
    }


    /**
     * 指定包名的构造方法
     * 搜索指定包路径下所有的类，并获取所有的继承了ITaskList的非接口对象
     *
     * @param packageName 包名
     * @throws ClassNotFoundException class未发现异常
     */
    public TaskFactory(String packageName) throws ClassNotFoundException {
        PackageScanner packageScannerComponent = new PackageScanner();
        PackageScanner packageScanner = packageScannerComponent
                .findAllClasses(packageName,
                        clazz -> ITaskList.class.isAssignableFrom(clazz) && !clazz.isInterface());

        Set<Class<?>> allTaskListClasses = packageScanner.getClasses();

        for (Class clazz : allTaskListClasses) {
            try {
                Object o = clazz.newInstance();
                Class infoType = ((ITaskList) o).getInfoType();
                taskClazzMap.put(infoType, clazz);
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 通过TaskInfo的实例，创建对应的用于装载TaskInfo的TaskList
     *
     * @param taskInfo  定时任务实例
     * @return  对应的TaskList实例
     */
    public TaskList getTaskListInstance(TaskInfo taskInfo) {
        Class<? extends ITaskInfo> taskInfoClass = taskInfo.getClass();

        List<Class<TaskInfo>> clazzList = new ArrayList<>(taskClazzMap.keySet());

        Class<TaskList> taskListClass = null;
        for (Class<TaskInfo> clazz : clazzList) {
            if (clazz.isAssignableFrom(taskInfoClass)) {
                taskListClass = taskClazzMap.get(clazz);
            }
        }

        try {
            TaskList list = Objects.requireNonNull(taskListClass).newInstance();
            list.setListInfo(taskInfo);
            return list;
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

}
