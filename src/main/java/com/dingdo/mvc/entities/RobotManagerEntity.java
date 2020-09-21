package com.dingdo.mvc.entities;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@TableName("robot_manager")
public class RobotManagerEntity {

    @TableField("id")
    private String id;

    @TableField(value = "level", fill = FieldFill.INSERT)
    private int level;

    @TableField(value = "create_by")
    private String createBy;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss" ,timezone ="GMT+8")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
