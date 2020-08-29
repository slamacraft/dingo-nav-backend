package com.dingdo.extendService.otherService;

import java.awt.image.BufferedImage;

public interface PythonService {

    /**
     * 从python端获取超分辨率图片
     * 默认分辨率X4
     * @param imagePath
     * @return 返回超分辨处理后的图片
     * @error 返回null
     */
    BufferedImage doWDSR(String imagePath);
}
