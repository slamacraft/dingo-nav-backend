package com.dingdo.extendService.model.specialReply;

import lombok.Data;

import java.util.Date;

/**
 * @author slamacraft
 * @version 1.0
 * @date 2020/9/22 10:33
 * @since JDK 1.8
 */
@Data
public class MusicConfig {

    private boolean autosize = true;
    private long ctime = new Date().getTime();
    private boolean forward = true;
    private String token = "";
    private String type = "normal";

}
