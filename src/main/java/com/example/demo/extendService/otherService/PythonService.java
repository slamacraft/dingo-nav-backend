package com.example.demo.extendService.otherService;

import com.example.demo.model.params.VocabularyInfo;

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

    /**
     * 获取词典表信息
     * @return
     */
    @Deprecated
    VocabularyInfo getVocabularyInfo();
}
