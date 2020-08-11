package com.dingdo.model.msgFromCQ;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

//例子{"data":[{"group_id":215660727,"group_name":"人工智障实验组","max_member_count":0,"member_count":0},{"group_id":799200346,"group_name":"big bob、slamacraft","max_member_count":0,"member_count":0}],"retcode":0,"status":"ok"}
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@Deprecated
public class GroupList {

    private List<GroupListData> data;
    private Long retcode;
    private String status;

    @Data
    public class GroupListData{
        private Long group_id;
        private String group_name;
        private Long max_member_count;
        private Long member_count;
    }
}
