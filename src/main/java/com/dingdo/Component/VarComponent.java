package com.dingdo.Component;

import com.alibaba.fastjson.JSONObject;
import com.dingdo.enums.UrlEnum;
import com.dingdo.model.msgFromCQ.FriendList;
import com.dingdo.model.msgFromCQ.GroupList;
import com.dingdo.model.msgFromCQ.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * 存储变量的组件
 * 包括全局变量，QQ登录者的信息变量
 */
@Component
public class VarComponent implements ApplicationRunner {

    private Long userId = 2270374713L;

    private String robotName = "人工小天才";

    private List<FriendList.FriendListData> friendList;

    private List<GroupList.GroupListData> groupList;

    public List<Character> shenmuList = new ArrayList<>();

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private TomatoClockComponent tomatoClockComponent;

    /**
     * 初始化登录用户信息
     */
    private void getUserInfo() {
        JSONObject json = new JSONObject();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);

        HttpEntity<JSONObject> request = new HttpEntity<>(json, headers);

        try {
            // 获取机器人信息
            System.out.println("向地址：" + UrlEnum.URL + "获取机器人QQ信息");
            String getUserInfoUrl = UrlEnum.URL + UrlEnum.GET_LOGIN_INFO.toString();
            ResponseEntity<LoginUser> responseOfInfo = restTemplate.postForEntity(getUserInfoUrl, request, LoginUser.class);
            this.userId = responseOfInfo.getBody().getData().getUser_id();
            this.robotName = responseOfInfo.getBody().getData().getNickname();

            // 获取群列表
            String getGroupListUrl = UrlEnum.URL + UrlEnum.GET_GROUP_LIST.toString();
            ResponseEntity<String> responseEntity = restTemplate.postForEntity(getGroupListUrl, request, String.class);
            GroupList groupList = JSONObject.parseObject(responseEntity.getBody(), GroupList.class);
            this.groupList = groupList.getData();

            // 获取好友列表
            String getFriendListUrl = UrlEnum.URL + UrlEnum.GET_FRIEND_LIST.toString();
            responseEntity = restTemplate.postForEntity(getFriendListUrl, request, String.class);
            FriendList friendList = JSONObject.parseObject(responseEntity.getBody(), FriendList.class);
            this.friendList = friendList.getData();

            // 番茄钟的核心线程数等于好友或者群数量较小的哪一个
            // 最大线程数等于群数量与好友数量之和
            int coreSize = this.groupList.size() < this.friendList.size() ? this.groupList.size() : this.friendList.size();
            int maxSize = this.groupList.size() + this.friendList.size();
            tomatoClockComponent.setTomatoCoreSize(coreSize);
            tomatoClockComponent.setTomatoMaxSize(maxSize);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initShengmuList() {
        shenmuList.add('b');
        shenmuList.add('p');
        shenmuList.add('m');
        shenmuList.add('f');
        shenmuList.add('d');
        shenmuList.add('t');
        shenmuList.add('n');
        shenmuList.add('l');
        shenmuList.add('g');
        shenmuList.add('k');
        shenmuList.add('h');
        shenmuList.add('j');
        shenmuList.add('q');
        shenmuList.add('x');
        shenmuList.add('z');
        shenmuList.add('c');
        shenmuList.add('s');
        shenmuList.add('r');
        shenmuList.add('y');
        shenmuList.add('w');
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
//        getUserInfo();
//        initShengmuList();
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getRobotName() {
        return robotName;
    }

    public void setRobotName(String robotName) {
        this.robotName = robotName;
    }

    public List<FriendList.FriendListData> getFriendList() {
        return friendList;
    }

    public List<GroupList.GroupListData> getGroupList() {
        return groupList;
    }
}
