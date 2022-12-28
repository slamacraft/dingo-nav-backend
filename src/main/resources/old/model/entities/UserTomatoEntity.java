package com.dingo.model.entities;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 一些声明信息
 *
 * @author slamacraft
 * @Description:
 * @date: 2020/8/25 11:04
 * @since JDK 1.8
 */

@Data
@TableName("user_tomato")
public class UserTomatoEntity {

    @TableField("id")
    private String id;

    @TableField("tomato")
    private int tomato;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss" ,timezone ="GMT+8")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss" ,timezone ="GMT+8")
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
