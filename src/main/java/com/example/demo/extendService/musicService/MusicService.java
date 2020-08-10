package com.example.demo.extendService.musicService;

import com.example.demo.extendService.MsgExtendService;
import com.example.demo.model.msgFromCQ.ReceiveMsg;
import com.example.demo.model.msgFromCQ.ReplyMsg;

/**
 * 点歌服务接口
 */
public interface MusicService extends MsgExtendService {

    /**
     * 从自然语言中获取歌曲名或歌曲名近似值
     * @param receiveMsg
     * @return
     */
    String getKeyword(ReceiveMsg receiveMsg);

    /**
     * 获取音乐
     * @param keyword
     * @return
     */
    ReplyMsg getMusic(String keyword);
}
