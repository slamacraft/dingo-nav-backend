package com.example.demo.model.musicFromQQ;

import lombok.Data;

@Data
public class MusicQQData {
    private String keyword; // 关键字
    private SongQQ song;    // 歌曲列表
}
