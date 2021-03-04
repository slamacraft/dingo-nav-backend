//package com.dingdo.mvc.service.impl;
//
//import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
//import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
//import com.dingdo.component.schedule.model.GroupMsgTaskInfo;
//import com.dingdo.component.schedule.model.PrivateMsgTaskInfo;
//import com.dingdo.mvc.mapper.MessageTaskMapper;
//import com.dingdo.mvc.entities.MessageTaskEntity;
//import com.dingdo.component.schedule.model.interfacor.ITaskInfo;
//import com.dingdo.mvc.service.MessageTaskService;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//import java.util.Objects;
//import java.util.UUID;
//import java.util.stream.Collectors;
//
///**
// * 定时消息Service
// *
// * @author slamacraft
// * @date: 2020/8/28 14:58
// * @since JDK 1.8
// */
//@Service
//public class MessageTaskServiceImpl extends ServiceImpl<MessageTaskMapper, MessageTaskEntity> implements MessageTaskService {
//
//
//    @Override
//    public void saveTask(ITaskInfo taskInfo) {
//        MessageTaskEntity entityByTaskInfo = createEntityByTaskInfo(taskInfo);
//        if(entityByTaskInfo !=null){
//            baseMapper.insert(entityByTaskInfo);
//        }
//    }
//
//
//    @Override
//    public void saveTaskBatch(List<ITaskInfo> insertList) {
//        List<MessageTaskEntity> toInsertEntities = insertList.stream()
//                .map(this::createEntityByTaskInfo)
//                .filter(Objects::nonNull)
//                .collect(Collectors.toList());
//
//        saveBatch(toInsertEntities);
//    }
//
//
//    @Override
//    public MessageTaskEntity createEntityByTaskInfo(ITaskInfo taskInfo) {
//        MessageTaskEntity result = new MessageTaskEntity();
//        if (taskInfo instanceof PrivateMsgTaskInfo) {
//            return createEntityByPrivateTaskInfo(taskInfo);
//        } else if (taskInfo instanceof GroupMsgTaskInfo) {
//            return createEntityByGroupTaskInfo(taskInfo);
//        }
//        return null;
//    }
//
//
//    /**
//     * 通过定时任务创建定时私聊消息提醒
//     * @param taskInfo  定时任务实例
//     * @return  私聊定时提醒实例
//     */
//    private MessageTaskEntity createEntityByPrivateTaskInfo(ITaskInfo taskInfo) {
//        MessageTaskEntity result = new MessageTaskEntity();
//        PrivateMsgTaskInfo privateMsgTaskInfo = (PrivateMsgTaskInfo) taskInfo;
//        result.setId(UUID.randomUUID().toString().substring(0,32));
//        result.setBotId(privateMsgTaskInfo.getBotId());
//        result.setCreateBy(privateMsgTaskInfo.getCreatorId());
//        result.setCron(privateMsgTaskInfo.getCron());
//        result.setMessage(privateMsgTaskInfo.getMessage());
//        result.setType("0");
//        result.setTargetId(privateMsgTaskInfo.getCreatorId());
//        result.setTaskName(privateMsgTaskInfo.getTaskName());
//        return result;
//    }
//
//
//    /**
//     * 通过定时任务创建定时群聊消息提醒
//     * @param taskInfo  定时任务实例
//     * @return  群聊定时提醒实例
//     */
//    private MessageTaskEntity createEntityByGroupTaskInfo(ITaskInfo taskInfo) {
//        MessageTaskEntity result = new MessageTaskEntity();
//        GroupMsgTaskInfo groupMsgTaskInfo = (GroupMsgTaskInfo) taskInfo;
//        result.setId(UUID.randomUUID().toString().substring(0,32));
//        result.setBotId(groupMsgTaskInfo.getBotId());
//        result.setCreateBy(groupMsgTaskInfo.getCreatorId());
//        result.setCron(groupMsgTaskInfo.getCron());
//        result.setMessage(groupMsgTaskInfo.getMessage());
//        result.setType("1");
//        result.setTargetId(groupMsgTaskInfo.getGroupId());
//        result.setTaskName(groupMsgTaskInfo.getTaskName());
//        return result;
//    }
//
//
//    @Override
//    public void deleteTaskEntity(ITaskInfo taskInfo){
//        MessageTaskEntity entityByTaskInfo = createEntityByTaskInfo(taskInfo);
//        QueryWrapper<MessageTaskEntity> wrapper = new QueryWrapper<>();
//
//        wrapper.eq("bot_id", entityByTaskInfo.getBotId());
//        wrapper.eq("cron", entityByTaskInfo.getCron());
//        wrapper.eq("task_name", entityByTaskInfo.getTaskName());
//        wrapper.eq("target_id", entityByTaskInfo.getTargetId());
//        wrapper.eq("message", entityByTaskInfo.getMessage());
//
//        baseMapper.delete(wrapper);
//    }
//
//
//    @Override
//    public List<ITaskInfo> getAllTaskInfo() {
//        List<MessageTaskEntity> allTaskEntity = baseMapper.selectList(null);
//
//        return allTaskEntity.stream()
//                .map(this::getTaskInfoByTaskEntity)
//                .filter(Objects::nonNull)
//                .collect(Collectors.toList());
//    }
//
//
//    @Override
//    public ITaskInfo getTaskInfoByTaskEntity(MessageTaskEntity entity) {
//        if ("0".equals(entity.getType())) {
//            return new PrivateMsgTaskInfo(entity);
//        } else if ("1".equals(entity.getType())) {
//            return new GroupMsgTaskInfo(entity);
//        }
//        return null;
//    }
//
//}
