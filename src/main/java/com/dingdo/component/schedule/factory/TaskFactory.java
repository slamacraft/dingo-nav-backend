package com.dingdo.schedule.factory;

import com.dingdo.schedule.model.ScheduledTask;
import com.dingdo.schedule.model.interfacor.ITaskInfo;
import com.dingdo.schedule.model.interfacor.ITaskList;
import com.dingdo.util.scanner.PackageScanner;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 一些声明信息
 *
 * @author slamacraft
 * @Description:
 * @date: 2020/8/27 19:05
 * @since JDK 1.8
 */
@Component
public class TaskFactory<TaskList extends ITaskList, TaskInfo extends ITaskInfo> {

    private Set<Class<?>> allTaskListClasses;

    private Map<Class<TaskInfo>, Class<TaskList>> taskClazzMap = new HashMap<>();


    /**
     * 组件注入时运行
     * 扫描该类包下所有的class，获取所有实现了ITaskList的类
     *
     * @throws ClassNotFoundException
     */
    @PostConstruct
    public void scannerListClass() throws ClassNotFoundException {
        String packageName = ScheduledTask.class.getPackage().getName();

        PackageScanner packageScannerComponent = new PackageScanner();
        PackageScanner packageScanner = packageScannerComponent
                .findAllClasses(packageName,
                        clazz -> ITaskList.class.isAssignableFrom(clazz) && !clazz.isInterface());

        allTaskListClasses = packageScanner.getClasses();

        for (Class clazz : allTaskListClasses) {
            try {
                Object o = clazz.newInstance();
                Class infoType = ((ITaskList) o).getInfoType();
                taskClazzMap.put(infoType, clazz);
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 通过taskInfo创建TaskList
     *
     * @param taskInfo
     * @return
     */
    public TaskList getTaskListInstance(TaskInfo taskInfo) {
        Class<? extends ITaskInfo> taskInfoClass = taskInfo.getClass();

        List<Class<TaskInfo>> clazzList = taskClazzMap.keySet().stream().collect(Collectors.toList());

        Class<TaskList> taskListClass = null;
        for(Class<TaskInfo> clazz : clazzList ){
            if(clazz.isAssignableFrom(taskInfoClass)){
                taskListClass = taskClazzMap.get(clazz);
            }
        }

        try {
            TaskList list = taskListClass.newInstance();
            list.setListInfo(taskInfo);
            return list;
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

}
