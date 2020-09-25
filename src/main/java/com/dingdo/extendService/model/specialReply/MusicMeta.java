package com.dingdo.extendService.model.specialReply;

import lombok.Data;

/**
 * @author slamacraft
 * @version 1.0
 * @date 2020/9/22 10:33
 * @since JDK 1.8
 */
@Data
public class MusicMeta {

    private Music music;

    public MusicMeta() {
    }

    public MusicMeta(String desc, String jumpUrl, String musicUrl, String preview, String tag, String title) {
        this.music = new Music();
        music.setDesc(desc);
        music.setJumpUrl(jumpUrl);
        music.setMusicUrl(musicUrl);
        music.setPreview(preview);
        music.setTag(tag);
        music.setTitle(title);
    }

    @Data
    class Music {
        private String action = "";
        private String android_pkg_name = "";
        private long app_type = 1;
        private long appid = 100495085;
        private String desc;
        private String jumpUrl;
        private String musicUrl;
        private String preview;
        private String sourceMsgId = "0";
        private String source_icon = "";
        private String source_url = "";
        private String tag;
        private String title;
    }

}
