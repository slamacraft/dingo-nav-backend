package com.dingdo.schedule.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dingdo.schedule.model.GroupMsgTaskInfo;
import com.dingdo.schedule.model.PrivateMsgTaskInfo;
import com.dingdo.schedule.dao.MessageTaskMapper;
import com.dingdo.schedule.entities.MessageTaskEntity;
import com.dingdo.schedule.model.interfacor.ITaskInfo;
import com.dingdo.schedule.service.MessageTaskService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 一些声明信息
 *
 * @author slamacraft
 * @Description:
 * @date: 2020/8/28 14:58
 * @since JDK 1.8
 */
@Service
public class MessageTaskServiceImpl extends ServiceImpl<MessageTaskMapper, MessageTaskEntity> implements MessageTaskService {


    /**
     * 持久化TaskInfo
     *
     * @param taskInfo
     */
    @Override
    public void saveTask(ITaskInfo taskInfo) {
        MessageTaskEntity entityByTaskInfo = createEntityByTaskInfo(taskInfo);
        if(entityByTaskInfo !=null){
            baseMapper.insert(entityByTaskInfo);
        }
    }

    /**
     * 批量持久化TaskInfo
     *
     * @param insertList
     */
    @Override
    public void saveTaskBatch(List<ITaskInfo> insertList) {
        List<MessageTaskEntity> toInsertEntities = insertList.stream()
                .map(item -> createEntityByTaskInfo(item))
                .filter(item -> item != null)
                .collect(Collectors.toList());

        saveBatch(toInsertEntities);
    }


    /**
     * 通过定时任务创建MessageTaskEntity
     *
     * @param taskInfo
     * @return
     */
    @Override
    public MessageTaskEntity createEntityByTaskInfo(ITaskInfo taskInfo) {
        MessageTaskEntity result = new MessageTaskEntity();
        if (taskInfo instanceof PrivateMsgTaskInfo) {
            return createEntityByPrivateTaskInfo(taskInfo);
        } else if (taskInfo instanceof GroupMsgTaskInfo) {
            return createEntityByGroupTaskInfo(taskInfo);
        }
        return null;
    }

    public MessageTaskEntity createEntityByPrivateTaskInfo(ITaskInfo taskInfo) {
        MessageTaskEntity result = new MessageTaskEntity();
        PrivateMsgTaskInfo privateMsgTaskInfo = (PrivateMsgTaskInfo) taskInfo;
        result.setId(UUID.randomUUID().toString().substring(0,32));
        result.setBotId(privateMsgTaskInfo.getBotId());
        result.setCreateBy(privateMsgTaskInfo.getCreatorId());
        result.setCron(privateMsgTaskInfo.getCron());
        result.setMessage(privateMsgTaskInfo.getMessage());
        result.setType("0");
        result.setTargetId(privateMsgTaskInfo.getCreatorId());
        result.setTaskName(privateMsgTaskInfo.getTaskName());
        return result;
    }

    public MessageTaskEntity createEntityByGroupTaskInfo(ITaskInfo taskInfo) {
        MessageTaskEntity result = new MessageTaskEntity();
        GroupMsgTaskInfo groupMsgTaskInfo = (GroupMsgTaskInfo) taskInfo;
        result.setId(UUID.randomUUID().toString().substring(0,32));
        result.setBotId(groupMsgTaskInfo.getBotId());
        result.setCreateBy(groupMsgTaskInfo.getCreatorId());
        result.setCron(groupMsgTaskInfo.getCron());
        result.setMessage(groupMsgTaskInfo.getMessage());
        result.setType("1");
        result.setTargetId(groupMsgTaskInfo.getGroupId());
        result.setTaskName(groupMsgTaskInfo.getTaskName());
        return result;
    }


    @Override
    public void deleteTaskEntity(ITaskInfo taskInfo){
        MessageTaskEntity entityByTaskInfo = createEntityByTaskInfo(taskInfo);
        QueryWrapper<MessageTaskEntity> wrapper = new QueryWrapper<>();

        wrapper.eq("bot_id", entityByTaskInfo.getBotId());
        wrapper.eq("cron", entityByTaskInfo.getCron());
        wrapper.eq("task_name", entityByTaskInfo.getTaskName());
        wrapper.eq("target_id", entityByTaskInfo.getTargetId());
        wrapper.eq("message", entityByTaskInfo.getMessage());

        baseMapper.delete(wrapper);
    }


    /**
     * 获取所有的TaskInfo
     *
     * @return
     */
    @Override
    public List<ITaskInfo> getAllTaskInfo() {
        List<MessageTaskEntity> allTaskEntity = baseMapper.selectList(null);

        return allTaskEntity.stream()
                .map(item -> getTaskInfoByTaskEntity(item))
                .filter(item -> item != null)
                .collect(Collectors.toList());
    }


    /**
     * 通过entity获取TaskInfo
     *
     * @param entity
     * @return
     */
    @Override
    public ITaskInfo getTaskInfoByTaskEntity(MessageTaskEntity entity) {
        if ("0".equals(entity.getType())) {
            return new PrivateMsgTaskInfo(entity);
        } else if ("1".equals(entity.getType())) {
            return new GroupMsgTaskInfo(entity);
        }
        return null;
    }

}
