package com.example.demo;

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGEncodeParam;
import com.sun.image.codec.jpeg.JPEGImageEncoder;
import org.junit.Test;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.FileOutputStream;

import static java.awt.image.BufferedImage.TYPE_INT_RGB;

/**
 * @author slamacraft
 * @version 1.0
 * @date 2020/9/23 17:25
 * @since JDK 1.8
 */
public class AwtTest {

    @Test
    public void test() {
        BufferedImage image = new BufferedImage(1000, 1000, TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();
        g.drawImage(image, 0, 0, 1000, 1000, null);

        Color mycolor = Color.black;
//        g.setColor(mycolor);
        g.setBackground(Color.white);
        g.clearRect(0, 0, 1000, 1000);
        g.setPaint(Color.black);
        g.setFont(new Font("宋体", Font.PLAIN, 40));
        g.drawString("测试\n你好呀", 200, 200);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.dispose();
        try {
            FileOutputStream out = new FileOutputStream("C:\\Users\\Administrator\\Desktop\\test.jpg"); //先用一个特定的输出文件名
            JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
            JPEGEncodeParam param = encoder.getDefaultJPEGEncodeParam(image);
            param.setQuality(400, true);  //
            encoder.encode(image, param);
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
