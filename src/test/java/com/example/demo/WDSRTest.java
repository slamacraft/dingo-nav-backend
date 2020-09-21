package com.example.demo;

import com.dingdo.util.ImageUtil;
import org.junit.Test;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * 一些声明信息
 *
 * @author slamacraft
 * @Description:
 * @date: 2020/8/27 20:07
 * @since JDK 1.8
 */
public class WDSRTest {

    @Test
    public void test() {
        RestTemplate restTemplate = new RestTemplate();
        MultiValueMap<String, Object> bodyMap = new LinkedMultiValueMap<>();
        bodyMap.add("image", new FileSystemResource("C:\\Users\\Administrator\\Desktop\\test.jpg"));
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
//        headers.setConnection("close");
        HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(bodyMap, headers);
        BufferedImage resultImage = null;
        try {
            byte[] bytes = restTemplate.postForObject("http://106.53.85.24:8000/predict_by_util/", request, byte[].class);
//            byte[] bytes = restTemplate.postForObject("http://localhost:8000/predict_by_util/", request, byte[].class);
            ByteArrayInputStream in = new ByteArrayInputStream(bytes);    //将b作为输入流；
            resultImage = ImageIO.read(in);
            ImageIO.write(resultImage, "jpg", new File("C:\\Users\\Administrator\\Desktop\\test1.jpg"));


            BufferedImage myImage = ImageUtil.foregroundSmooth(resultImage, 3, ImageUtil.MEAN_MAXPOINT);
            ImageIO.write(myImage, "jpg", new File("C:\\Users\\Administrator\\Desktop\\test2.jpg"));

            BufferedImage myImage_2 = ImageUtil.sharpen(myImage);
            ImageIO.write(myImage_2, "jpg", new File("C:\\Users\\Administrator\\Desktop\\test3.jpg"));

            BufferedImage image = ImageUtil.foregroundSmooth(resultImage);
            ImageIO.write(image, "jpg", new File("C:\\Users\\Administrator\\Desktop\\test4.jpg"));
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Test
    public void test1() throws IOException {
        BufferedImage image = ImageIO.read(new File("C:\\Users\\Administrator\\Desktop\\test.jpg"));


    }

}
