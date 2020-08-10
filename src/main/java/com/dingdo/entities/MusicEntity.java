package com.dingdo.entities;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

@Data
@TableName("music")
public class MusicEntity {

    /**
     * 音乐mid
     */
    @TableField("music_mid")
    private String musicMid;
    /**
     * 音乐来源
     */
    @TableField("music_type")
    private String musicType;
    /**
     * 音乐名称
     */
    @TableField("music_name")
    private String musicName;
    /**
     * 音乐作者
     */
    @TableField("music_author")
    private String musicAuthor;
}
