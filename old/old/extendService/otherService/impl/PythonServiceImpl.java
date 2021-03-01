package com.example.old.extendService.otherService.impl;

import com.dingdo.common.annotation.Instruction;
import com.dingdo.enums.CQCodeEnum;
import com.example.old.extendService.otherService.PythonService;
import com.dingdo.msgHandler.model.CQCode;
import com.dingdo.msgHandler.model.ReqMsg;
import com.dingdo.util.FileUtil;
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
import java.io.File;
import java.util.List;
import java.util.Map;

@Service
public class PythonServiceImpl implements PythonService {

    // 使用log4j打印日志
    private static Logger logger = Logger.getLogger(PythonServiceImpl.class);

    private final RestTemplate restTemplate;

    @Autowired
    public PythonServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }


    @Override
    @Instruction(description = "图片增强", isShortened = true)
    public String getEnhanceImage(ReqMsg reqMsg, Map<String, String> params){
        List<CQCode> cqCodeList = reqMsg.getCqCodeList();

        for(CQCode cqCode : cqCodeList){
            if(cqCode.getCode() == CQCodeEnum.IMAGE){
                String imgName = cqCode.getValues().get("image");
                // 去除{,},\,.mirai字符
                imgName = imgName.replaceAll("(\\{|\\}|\\\\|\\.mirai)", "");
                String imageSrc = FileUtil.getImagePath(imgName);
                if (imageSrc == null) {
                    return "";
                }
                BufferedImage imgBuffer = this.doWDSR(imageSrc);
                if(imgBuffer == null){
                    return "我觉得这张图有问题【";
                }
                String resultSrc = FileUtil.saveImage(imgBuffer, imgName + "X4");

                return "[CQ:image,image=" + resultSrc + "]";
            }
        }

        if(params.get("短接") == null){
            return "你发送的下一张图片会被我施加魔法！";
        }

        return "";
    }


    @Override
    public BufferedImage doWDSR(String imagePath){
        MultiValueMap<String, Object> bodyMap = new LinkedMultiValueMap<>();
        bodyMap.add("image", new FileSystemResource(new File(imagePath)));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
//        headers.setConnection("close");

        HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(bodyMap, headers);

        BufferedImage resultImage = null;
        try {
            logger.info("开始进行超分辨率放大");
            byte[] bytes = restTemplate.postForObject("http://106.53.85.24:8000/predict_by_util/", request, byte[].class);
//            byte[] bytes = restTemplate.postForObject("http://localhost:8000/predict_by_util/", request, byte[].class);
            ByteArrayInputStream in = new ByteArrayInputStream(bytes);    //将b作为输入流；
            resultImage = ImageIO.read(in);
            logger.info("超分辨率已完成，开始优化前景色");
            resultImage = ImageUtil.foregroundSmooth(resultImage, 3, ImageUtil.MEAN_MAXPOINT);
            logger.info("前景色优化已完成");
        } catch (Exception e) {
            logger.error("获取超分辨图像时出现错误:", e);
        }

        return resultImage;
    }
}
