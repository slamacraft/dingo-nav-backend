package com.dingdo.extendService.model.specialReply;

import com.alibaba.fastjson.JSON;
import lombok.Data;

/**
 * @author slamacraft
 * @version 1.0
 * @date 2020/9/22 9:52
 * @since JDK 1.8
 */
@Data
public class MusicReplyCard {

    public MusicReplyCard() {
    }

    public MusicReplyCard(String desc, String jumpUrl, String musicUrl, String preview, String tag, String title) {
        meta = new MusicMeta(desc, jumpUrl, musicUrl, preview, tag, title);
        this.prompt = title;
    }

    private String app="com.tencent.structmsg";
    private MusicConfig config = new MusicConfig();
    private String desc = "音乐";
    private MusicMeta meta;
    private String prompt;
    private String ver ="0.0.0.1";
    private String view = "music";

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder("[CQ:app,content=");
        result.append(JSON.toJSONString(this).replaceAll(",", "&#44;"));
        result.append("]");
        return result.toString();
    }
}
