package com.dingdo.model.musicFromQQ;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
public class SongQQ {
    private List<SongList> list;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class SongList {
        private String mid;
        private String name;
        private String subtitle;
        private String id;
        private List<SingerList> singer;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class SingerList {
        private String id;
        private String mid;
        private String name;
    }
}
