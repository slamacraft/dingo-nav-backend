//package com.dingdo.mvc.service;
//
//import com.baomidou.mybatisplus.extension.service.IService;
//import com.dingdo.mvc.entities.MessageTaskEntity;
//import com.dingdo.component.schedule.model.interfacor.ITaskInfo;
//
//import java.util.List;
//
///**
// * 一些声明信息
// *
// * @author slamacraft
// * @Description:
// * @date: 2020/8/28 14:59
// * @since JDK 1.8
// */
//public interface MessageTaskService extends IService<MessageTaskEntity> {
//
//    /**
//     * 持久化TaskInfo
//     *
//     * @param taskInfo  定时任务
//     */
//    void saveTask(ITaskInfo taskInfo);
//
//    /**
//     * 批量持久化TaskInfo
//     *
//     * @param insertList TaskInfo列表
//     */
//    void saveTaskBatch(List<ITaskInfo> insertList);
//
//    /**
//     * 通过定时任务创建MessageTaskEntity
//     *
//     * @param taskInfo TaskInfo
//     * @return  创建的定时消息实体
//     */
//    MessageTaskEntity createEntityByTaskInfo(ITaskInfo taskInfo);
//
//    /**
//     * 删除定时提醒
//     * @param taskInfo  定时任务
//     */
//    void deleteTaskEntity(ITaskInfo taskInfo);
//
//    /**
//     * 从数据库中获取所有的定时任务
//     * @return  ITaskInfo列表
//     */
//    List<ITaskInfo> getAllTaskInfo();
//
//    /**
//     * 通过entity获取TaskInfo
//     *
//     * @param entity
//     * @return
//     */
//    ITaskInfo getTaskInfoByTaskEntity(MessageTaskEntity entity);
//
//}
