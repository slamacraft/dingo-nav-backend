package com.dingo.model.entities;


import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("message_task")
public class MessageTaskEntity {

    @TableField("id")
    private String id;

    @TableField("task_name")
    private String taskName;

    @TableField("cron")
    private String cron;

    /**
     * 0-私聊定时任务
     * 1-群聊定时任务
     */
    @TableField("type")
    private String type;

    @TableField("bot_id")
    private String botId;

    @TableField("target_id")
    private String targetId;

    @TableField("message")
    private String message;

    @TableField("create_by")
    private String createBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
