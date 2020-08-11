package com.dingdo.extendService.musicService;

import com.dingdo.extendService.MsgExtendService;

import com.dingdo.model.msgFromMirai.ReqMsg;

/**
 * 点歌服务接口
 */
public interface MusicService extends MsgExtendService {

    /**
     * 从自然语言中获取歌曲名或歌曲名近似值
     * @param reqMsg
     * @return
     */
    String getKeyword(ReqMsg reqMsg);

    /**
     * 获取音乐
     * @param keyword
     * @return
     */
    String getMusic(String keyword);
}
