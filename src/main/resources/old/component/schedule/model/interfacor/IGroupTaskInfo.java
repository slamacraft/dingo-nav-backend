package com.dingdo.component.schedule.model.interfacor;

/**
 * 一些声明信息
 *
 * @author slamacraft
 * @Description:
 * @date: 2020/8/27 15:23
 * @since JDK 1.8
 */
public interface IGroupTaskInfo extends IUserTaskInfo {

    /**
     * 获取群号
     * @return
     */
    String getGroupId();

}
