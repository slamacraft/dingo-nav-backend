package com.dingdo.schedule.model;

import com.dingdo.schedule.model.interfacor.IGroupTaskInfo;
import com.dingdo.schedule.model.interfacor.ITaskInfo;
import com.dingdo.schedule.model.interfacor.ITaskList;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * 一些声明信息
 *
 * @author slamacraft
 * @Description:
 * @date: 2020/8/27 15:29
 * @since JDK 1.8
 */
public class GroupTaskList<GroupTaskInfo extends IGroupTaskInfo> implements ITaskList {

    private String groupId;

    private List<GroupTaskInfo> taskInfoList = new ArrayList<>();

    /**
     * 不带参数的构造函数，方便反射实例化
     */
    public GroupTaskList() {
    }

    public GroupTaskList(String groupId, GroupTaskInfo... list) {
        this.groupId = groupId;
        for (GroupTaskInfo info : list) {
            taskInfoList.add(info);
        }
    }

    @Override
    public String toString() {
        StringBuffer result = new StringBuffer();
        for (int i = 0; i < taskInfoList.size(); i++) {
            GroupTaskInfo groupTaskInfo = taskInfoList.get(i);
            result.append((i + 1) + "、" + groupTaskInfo.toString());
        }
        return result.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (!(obj instanceof GroupTaskList)) return false;
        if (this == obj) return true;
        return groupId.equals(((GroupTaskList) obj).groupId);
    }

    @Override
    public Class getInfoType() {
        return IGroupTaskInfo.class;
    }

    @Override
    public List<GroupTaskInfo> getTaskList() {
        return taskInfoList;
    }

    @Override
    public Stream<GroupTaskInfo> stream() {
        return taskInfoList.stream();
    }

    @Override
    public void setListInfo(ITaskInfo taskInfo) {
        if(!(taskInfo instanceof IGroupTaskInfo)){
            return;
        }
        GroupTaskInfo groupTaskInfo = (GroupTaskInfo)taskInfo;
        this.groupId = groupTaskInfo.getGroupId();
        this.taskInfoList.add(groupTaskInfo);
    }

    @Override
    public Boolean belongThisList(ITaskInfo taskInfo) {
        if(!(taskInfo instanceof IGroupTaskInfo)){
            return false;
        }
        return this.groupId.equals(((IGroupTaskInfo)taskInfo).getGroupId());
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getGroupId() {
        return groupId;
    }
}
