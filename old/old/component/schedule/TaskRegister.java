package com.example.old.component.schedule;

import com.dingdo.component.schedule.factory.TaskFactory;
import com.dingdo.component.schedule.model.ScheduledTask;
import com.dingdo.component.schedule.model.interfacor.ITaskInfo;
import com.dingdo.component.schedule.model.interfacor.ITaskList;
import com.dingdo.mvc.service.MessageTaskService;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.config.CronTask;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 定时任务注册组件
 * 用于定时任务的执行，新增，移出
 */
@Component
public class TaskRegister<TaskList extends ITaskList, TaskInfo extends ITaskInfo> implements DisposableBean {

    private final Map<TaskInfo, ScheduledTask> scheduledTasks = new ConcurrentHashMap<>(16);

    private final List<TaskList> AllTaskList = new LinkedList<>();

    private final TaskScheduler taskScheduler;
    private final TaskFactory taskFactory;
    private final MessageTaskService messageTaskService;

    @Autowired
    public TaskRegister(TaskScheduler taskScheduler,
                        TaskFactory taskFactory,
                        MessageTaskService messageTaskService) {
        this.taskScheduler = taskScheduler;
        this.taskFactory = taskFactory;
        this.messageTaskService = messageTaskService;

        List<ITaskInfo> allTaskInfo = messageTaskService.getAllTaskInfo();
        for(ITaskInfo taskInfo : allTaskInfo){
            addCronTask((TaskInfo) taskInfo);
        }
    }


    /**
     * 获取springboot的定时任务器
     * @return  定时任务器
     */
    public TaskScheduler getScheduler() {
        return this.taskScheduler;
    }


    /**
     * 从当前的定时任务列表中是否包含指定的TaskInfo实例
     * @param instance  TaskInfo实例
     * @return  是否包含实例
     */
    public boolean containTaskInfo(TaskInfo instance) {
        return getTaskInfo(instance) != null;
    }


    /**
     * 通过传入的TaskInfo实例获取定时任务列表中相同的实例
     *
     * @param instance
     * @return
     */
    public TaskInfo getTaskInfo(TaskInfo instance) {
        Map<Class<TaskInfo>, List<TaskList>> taskListByClazzMap = AllTaskList.stream()
                .collect(Collectors.groupingBy(ITaskList::getInfoType));

        List<Class<TaskInfo>> clazzList = new ArrayList<>(taskListByClazzMap.keySet());

        List<TaskList> taskList = null;
        for (Class<TaskInfo> clazz : clazzList) {
            if (clazz.isAssignableFrom(instance.getClass())) {
                taskList = taskListByClazzMap.get(clazz);
            }
        }
        if(taskList == null){
            return null;
        }

        TaskList AllTaskList = taskList.stream().reduce((item1, item2) -> {
            item1.getTaskList().addAll(item2.getTaskList());
            return item1;
        }).get();

        int index = AllTaskList.getTaskList().indexOf(instance);
        if (index >= 0) {
            return (TaskInfo) AllTaskList.getTaskList().get(index);
        }

        return null;
    }

    /**
     * 通过TaskInfo实例获取定时任务列表中的TaskList实例
     *
     * @param instance
     * @return
     */
    public TaskList getTaskList(TaskInfo instance) {
        for (TaskList list : this.AllTaskList) {
            if (list.belongThisList(instance)) {
                return list;
            }
        }
        return null;
    }


    /**
     * 通过TaskList实例获取定时任务列表中的实例
     *
     * @param listInstance
     * @return
     */
    public TaskList getTaskList(TaskList listInstance) {
        int index = AllTaskList.indexOf(listInstance);
        if (index >= 0) {
            return AllTaskList.get(index);
        }
        return null;
    }


    public boolean removeCronTaskAndSave(TaskInfo task, int index){
        TaskInfo removedTaskInfo = removeTaskInfoFromList(task, index);
        ScheduledTask scheduledTask = scheduledTasks.remove(removedTaskInfo);
        if(scheduledTask == null){
            return false;
        }
        scheduledTask.cancel();
        messageTaskService.deleteTaskEntity(removedTaskInfo);
        return true;
    }


    /**
     * 移除定时任务
     *
     * @param instance 通过ITaskInfo实例来移除
     * @return
     */
    public boolean removeTask(TaskInfo instance) {
        TaskList taskList = getTaskList(instance);
        if (taskList == null) {
            return false;
        }
        // 暂停正在运行的定时任务
        ScheduledTask toRemoveTask = scheduledTasks.remove(instance);
        toRemoveTask.cancel();
        return taskList.getTaskList().remove(instance);
    }


    /**
     * 通过TaskList的索引移除定时任务
     *
     * @param index TaskList的索引
     */
    public boolean removeTask(TaskInfo instance, int index) {
        TaskInfo removedTaskInfo = removeTaskInfoFromList(instance, index);
        ScheduledTask scheduledTask = scheduledTasks.remove(removedTaskInfo);
        scheduledTask.cancel();
        return true;
    }


    public TaskInfo removeTaskInfoFromList(TaskInfo instance, int index){
        TaskList taskList = getTaskList(instance);
        if (index < 0 || index >= taskList.getTaskList().size()) {
            return null;
        }

        return (TaskInfo)taskList.getTaskList().remove(index);
    }


    public void addCronTaskAndSave(TaskInfo task){
        addCronTask(task);
        messageTaskService.saveTask(task);
    }


    /**
     * 新增定时任务
     *
     * @param task
     */
    public void addCronTask(TaskInfo task) {
        if (this.containTaskInfo(task)) {
            removeTask(task);
        }

        TaskList taskList = getTaskList(task);
        if (taskList == null) {
            taskList = (TaskList) taskFactory.getTaskListInstance(task);
            AllTaskList.add(taskList);
        } else {
            taskList.getTaskList().add(task);
        }

        CronTask cronTask = new CronTask((Runnable) task, task.getCron());
        scheduledTasks.put(task, scheduleCronTask(cronTask));
    }


    /**
     * 注册定时任务
     * 到这里才是真正开始执行定时任务
     *
     * @param cronTask
     * @return
     */
    public ScheduledTask scheduleCronTask(CronTask cronTask) {
        ScheduledTask scheduledTask = new ScheduledTask();
        scheduledTask.future = this.taskScheduler.schedule(cronTask.getRunnable(), cronTask.getTrigger());
        return scheduledTask;
    }


    /**
     * 被销毁时关闭所有定时任务
     */
    @Override
    public void destroy() {
        for (ScheduledTask task : this.scheduledTasks.values()) {
            task.cancel();
        }
        this.scheduledTasks.clear();
    }
}
