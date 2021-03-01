package com.example.old.component.schedule.model;

import com.dingdo.component.schedule.model.interfacor.ISystemTaskInfo;
import com.dingdo.component.schedule.model.interfacor.ITaskInfo;
import com.dingdo.component.schedule.model.interfacor.ITaskList;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * 一些声明信息
 *
 * @author slamacraft
 * @Description:
 * @date: 2020/8/29 8:51
 * @since JDK 1.8
 */
public class SystemTaskList<SystemTaskInfo extends ISystemTaskInfo> implements ITaskList {

    private List<SystemTaskInfo> taskInfoList = new ArrayList<>();

    /**
     * 不带参数的构造函数，为了工程反射实例化
     */
    public SystemTaskList() {
    }

    public SystemTaskList(SystemTaskInfo... list) {
        for (SystemTaskInfo info : list) {
            taskInfoList.add(info);
        }
    }

    @Override
    public String toString() {
        StringBuffer result = new StringBuffer();
        for (int i = 0; i < taskInfoList.size(); i++) {
            SystemTaskInfo systemTaskInfo = taskInfoList.get(i);
            result.append((i + 1) + "、" + systemTaskInfo.toString());
        }
        return result.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (!(obj instanceof PrivateTaskList)) return false;
        if (this == obj) return true;
        return true;
    }

    @Override
    public Class getInfoType() {
        return ISystemTaskInfo.class;
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
        if(!(taskInfo instanceof ISystemTaskInfo)){
            return;
        }
        SystemTaskInfo systemTaskInfo = (SystemTaskInfo)taskInfo;
        this.taskInfoList.add(systemTaskInfo);
    }

    @Override
    public Boolean belongThisList(ITaskInfo taskInfo) {
        if(!(taskInfo instanceof ISystemTaskInfo)){
            return false;
        }
        return true;
    }
}
