package com.dingdo.schedule.model.interfacor;

import java.util.List;
import java.util.stream.Stream;

/**
 * 一些声明信息
 *
 * @author slamacraft
 * @Description:
 * @date: 2020/8/27 15:27
 * @since JDK 1.8
 */
public interface ITaskList<TaskInfo extends ITaskInfo> {

    Class<TaskInfo> getInfoType();

    List<TaskInfo> getTaskList();

    Stream<TaskInfo> stream();

    void setListInfo(TaskInfo taskInfo);

    Boolean belongThisList(TaskInfo taskInfo);

}
