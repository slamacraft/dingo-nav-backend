package com.dingdo.entities;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

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
    private String tomato;
}
