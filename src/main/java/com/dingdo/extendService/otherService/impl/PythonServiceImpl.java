package com.dingdo.extendService.otherService.impl;

import com.dingdo.extendService.otherService.PythonService;
import com.dingdo.util.ImageUtil;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;

@Service
public class PythonServiceImpl implements PythonService {

    // 使用log4j打印日志
    private static Logger logger = Logger.getLogger(PythonServiceImpl.class);
    @Autowired
    private RestTemplate restTemplate;

    public BufferedImage doWDSR(String imagePath){
        RestTemplate restTemplate = new RestTemplate();
        MultiValueMap<String, Object> bodyMap = new LinkedMultiValueMap<>();
        bodyMap.add("image", new FileSystemResource(imagePath));
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(bodyMap, headers);
        BufferedImage resultImage = null;
        try {
            byte[] bytes = restTemplate.postForObject("http://47.112.225.39:8000/predict_by_util/",request, byte[].class);
            ByteArrayInputStream in = new ByteArrayInputStream(bytes);    //将b作为输入流；
            resultImage = ImageIO.read(in);
            resultImage = ImageUtil.foregroundSmooth(resultImage, 3, ImageUtil.MEAN_MAXPOINT);
        } catch (Exception e) {
            logger.error("获取超分辨图像时出现错误:", e);
        }

        return resultImage;
    }
}
