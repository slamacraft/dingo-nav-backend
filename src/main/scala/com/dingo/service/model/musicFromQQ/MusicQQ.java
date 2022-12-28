package com.dingo.service.model.musicFromQQ;

import lombok.Data;

@Data
public class MusicQQ {
    private String code;
    private MusicQQData data;
    private String message;
    private long time;
    private String tips;
}
