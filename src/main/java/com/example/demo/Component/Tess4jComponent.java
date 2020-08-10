package com.example.demo.Component;

import com.alibaba.fastjson.JSONObject;
import com.example.demo.common.annotation.Instruction;
import com.example.demo.common.annotation.VerifiAnnotation;
import com.example.demo.enums.UrlEnum;
import com.example.demo.extendService.otherService.PythonService;
import com.example.demo.model.msgFromCQ.ReceiveMsg;
import com.example.demo.util.ImageUtil;
import com.example.demo.util.InstructionUtils;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Map;

/**
 * tess4j的图像文字提取组件
 */
@PropertySource("classpath:Tess4j.properties")
@Component
public class Tess4jComponent implements ApplicationRunner {

    // 使用log4j打印日志
    private static Logger logger = Logger.getLogger(Tess4jComponent.class);

    // 超分辨率功能启用开关
    private boolean enableWDSR = false;

    @VerifiAnnotation
    @Instruction(name = "enableWDSR", descrption = "开启超分辨率识别")
    public String enableWDSR(ReceiveMsg receiveMsg, Map<String, String> params) {
        this.enableWDSR = true;
        return "超分辨率识别已开启";
    }

    @VerifiAnnotation
    @Instruction(name = "disableWDSR", descrption = "关闭超分辨率识别")
    public String disableWDSR(ReceiveMsg receiveMsg, Map<String, String> params) {
        this.enableWDSR = false;
        return "超分辨率识别已关闭";
    }

    private static Tesseract tesseract = new Tesseract();

    @Value("${config.tessdata.languagePath}")
    private String languagePath;

    @Autowired
    private PythonService pythonService;

    /**
     * 通过消息CQ码获取图片，然后进行文字提取
     *
     * @param imgCode
     * @return
     */
    public String tessOCR(String imgCode) {
        String imgUrl = imgCode.split("file=")[1].split("]")[0];
        String imageSrc = getImageSrc(imgUrl);
        if (imageSrc == null) {
            return "";
        }

        BufferedImage imgBuffer = null;
        String result = null;
        try {
            // 是否启用超分辨率服务
            if (enableWDSR) {
                imgBuffer = pythonService.doWDSR(imageSrc);
            } else {
                // 没有启动超分辨服务，使用dpi扩增
                imgBuffer = ImageUtil.getImgAndSetDpi(new File(imageSrc), 300, 300);
            }
            imgBuffer = ImageUtil.binaryImage(imgBuffer);   // 图片二值化
            result = tesseract.doOCR(imgBuffer);            // 图片文字提取
        } catch (TesseractException e) {
            logger.error(e);
        }
        return result.replaceAll("[^\\u4e00-\\u9fa5]", "");
    }

    /**
     * 从消息给与的图片码中获取获取图片
     *
     * @param imgCode
     * @return
     */
    public String getImageSrc(String imgCode) {
        RestTemplate restTemplate = new RestTemplate();
        JSONObject json = new JSONObject();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);

        json.put("file", imgCode);

        HttpEntity<JSONObject> request = new HttpEntity<>(json, headers);
        try {
            ResponseEntity<Object> response = restTemplate.postForEntity(UrlEnum.URL + UrlEnum.GET_IMAGE.toString(), request, Object.class);
            String responseBody = response.getBody().toString();
            System.out.println("请求的结果为:"+responseBody);
            String imgSrc = null;
            if (StringUtils.isNotBlank(responseBody) && responseBody.contains("file=")) {
                imgSrc = response.getBody().toString().split("file=")[1].split("},")[0];
            }
            System.out.println("获取图片路径为:" + imgSrc);
            return imgSrc;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * tess4j初始化方法
     */
    private void initTess4j() {
        logger.warn("开始初始化Tess4j：" + languagePath);
        logger.warn("加载Tess4J语言包地址：" + languagePath);
        tesseract.setDatapath(languagePath);
        tesseract.setLanguage("chi_sim");
        logger.warn("完成初始化Tess4j：" + languagePath);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        initTess4j();
    }
}
