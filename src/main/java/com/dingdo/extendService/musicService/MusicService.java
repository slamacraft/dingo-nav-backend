package com.dingdo.extendService.musicService;

import com.dingdo.extendService.MsgExtendService;

import com.dingdo.msgHandler.model.ReqMsg;

/**
 * 点歌服务接口
 */
public interface MusicService extends MsgExtendService {

    /**
     * 从消息中获取歌曲卡片的cq码
     * @param reqMsg    请求消息
     * @return  请求结果
     */
    String getMusicCQCode(ReqMsg reqMsg);
}
