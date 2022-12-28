package com.dingo.component.schedule.model;

import com.dingo.component.schedule.model.interfacor.IPrivateTaskInfo;
import com.dingo.component.schedule.model.interfacor.ITaskInfo;
import com.dingo.component.schedule.model.interfacor.ITaskList;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * 一些声明信息
 *
 * @author slamacraft
 * @Description:
 * @date: 2020/8/28 8:52
 * @since JDK 1.8
 */
public class PrivateTaskList <PrivateTaskInfo extends IPrivateTaskInfo> implements ITaskList {

    private String userId;

    private List<PrivateTaskInfo> taskInfoList = new ArrayList<>();

    /**
     * 不带参数的构造函数，为了工程反射实例化
     */
    public PrivateTaskList() {
    }

    public PrivateTaskList(String userId, PrivateTaskInfo... list) {
        this.userId = userId;
        for (PrivateTaskInfo info : list) {
            taskInfoList.add(info);
        }
    }

    @Override
    public String toString() {
        StringBuffer result = new StringBuffer();
        for (int i = 0; i < taskInfoList.size(); i++) {
            PrivateTaskInfo privateTaskInfo = taskInfoList.get(i);
            result.append((i + 1) + "、" + privateTaskInfo.toString());
        }
        return result.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (!(obj instanceof PrivateTaskList)) return false;
        if (this == obj) return true;
        return userId.equals(((PrivateTaskList) obj).userId);
    }

    @Override
    public Class getInfoType() {
        return IPrivateTaskInfo.class;
    }

    @Override
    public List getTaskList() {
        return taskInfoList;
    }

    @Override
    public Stream stream() {
        return taskInfoList.stream();
    }

    @Override
    public void setListInfo(ITaskInfo taskInfo) {
        if(!(taskInfo instanceof IPrivateTaskInfo)){
            return;
        }
        PrivateTaskInfo privateTaskInfo = (PrivateTaskInfo)taskInfo;
        this.userId = privateTaskInfo.getUserId();
        this.taskInfoList.add(privateTaskInfo);
    }

    @Override
    public Boolean belongThisList(ITaskInfo taskInfo) {
        if(!(taskInfo instanceof IPrivateTaskInfo)){
            return false;
        }
        return this.userId.equals(((IPrivateTaskInfo)taskInfo).getUserId());
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
