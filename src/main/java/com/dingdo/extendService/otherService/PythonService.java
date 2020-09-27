package com.dingdo.extendService.otherService;

import com.dingdo.msgHandler.model.ReqMsg;

import java.awt.image.BufferedImage;
import java.util.Map;

public interface PythonService {

    /**
     * 从python端获取超分辨率图片
     * 默认分辨率X4
     *
     * @param imagePath 图片地址
     * @return 返回超分辨处理后的图片
     */
    BufferedImage doWDSR(String imagePath);

    /**
     * 获取增强后的图片
     *
     * @param reqMsg 请求消息
     * @param params 请求参数
     * @return 请求结果
     */
    String getEnhanceImage(ReqMsg reqMsg, Map<String, String> params);
}
