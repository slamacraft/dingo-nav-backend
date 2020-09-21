package com.dingdo.schedule.service;

import com.dingdo.schedule.entities.MessageTaskEntity;
import com.dingdo.schedule.model.interfacor.ITaskInfo;

import java.util.List;

/**
 * 一些声明信息
 *
 * @author slamacraft
 * @Description:
 * @date: 2020/8/28 14:59
 * @since JDK 1.8
 */
public interface MessageTaskService {

    void saveTask(ITaskInfo taskInfo);

    void saveTaskBatch(List<ITaskInfo> insertList);

    MessageTaskEntity createEntityByTaskInfo(ITaskInfo taskInfo);

    void deleteTaskEntity(ITaskInfo taskInfo);

    List<ITaskInfo> getAllTaskInfo();

    ITaskInfo getTaskInfoByTaskEntity(MessageTaskEntity entity);

}
