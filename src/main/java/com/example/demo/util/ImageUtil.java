package com.example.demo.util;

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGEncodeParam;
import com.sun.image.codec.jpeg.JPEGImageEncoder;
import org.apache.log4j.Logger;
import org.springframework.lang.Nullable;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import static org.apache.commons.math3.util.FastMath.pow;

/**
 * 图像工具类
 * 功能:从远程网络获取图片、二值化图片、获取点的灰度值、
 * 图片锐化、相对灰度计算、更改图片Dpi、通过OSTU大津算法获得二值化分割点
 */
public class ImageUtil {

    private static Logger logger = Logger.getLogger(ImageUtil.class);

    /**
     * 去除黑线时的中点赋值方式
     * 使用范围内灰度最大的随机一个点的颜色值赋值给该点
     */
    public static final int RANDOM_MAXPOINT = 0;
    /**
     * 去除黑线时的中点赋值方式
     * 使用范围内灰度最大的点的颜色均值赋值给该点
     */
    public static final int MEAN_MAXPOINT = 1;

//    static {
//        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);  //加载动态链接库
//    }

    /**
     * 获取远程网络图片信息
     *
     * @param imageURL
     * @return
     */
    public static BufferedImage getRemoteBufferedImage(String imageURL) {
        URL url = null;
        InputStream is = null;
        BufferedImage bufferedImage = null;
        try {
            url = new URL(imageURL);
            is = url.openStream();
            bufferedImage = ImageIO.read(is);
        } catch (MalformedURLException e) {
            logger.error("图片: " + imageURL + ",无效!", e);
            return null;
        } catch (IOException e) {
            logger.error("图片: " + imageURL + ",读取失败!", e);
            return null;
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
                logger.error("图片: " + imageURL + ",流关闭异常!", e);
                return null;
            }
        }
        return bufferedImage;
    }

    /**
     * 获取一张二值化图片
     *
     * @param bufferImg
     * @return
     */
    public static BufferedImage binaryImage(BufferedImage bufferImg) {
        BufferedImage image = bufferImg;
        int height = image.getHeight();
        int width = image.getWidth();
        int[][] grayArray = getImageGrayArray(image);

        int threshold = otsuThreshold(grayArray, height, width, null, null);

        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_BINARY);//  构造一个类型为预定义图像类型之一的 BufferedImage，TYPE_BYTE_BINARY（表示一个不透明的以字节打包的 1、2 或 4 位图像。）

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if (grayArray[i][j] > threshold) {
                    int black = new Color(255, 255, 255).getRGB();
                    bufferedImage.setRGB(i, j, black);
                } else {
                    int white = new Color(0, 0, 0).getRGB();
                    bufferedImage.setRGB(i, j, white);
                }
            }
        }
        return bufferedImage;
    }

    /**
     * 获取点的灰度值
     *
     * @param rgbValue rgb值
     * @return
     */
    public static int getImageGray(int rgbValue) {
        String argb = Integer.toHexString(rgbValue); // 将十进制的颜色值转为十六进制
        // argb分别代表透明,红,绿,蓝 分别占16进制2位
        int r = (int) (Integer.parseInt(argb.substring(2, 4), 16) * 1.1 + 30);//后面参数为使用进制
        int g = (int) (Integer.parseInt(argb.substring(4, 6), 16) * 1.1 + 30);
        int b = (int) (Integer.parseInt(argb.substring(6, 8), 16) * 1.1 + 30);
        if (r > 255) {
            r = 255;
        }
        if (g > 255) {
            g = 255;
        }
        if (b > 255) {
            b = 255;
        }
        int result = (int) Math
                .pow((Math.pow(r, 2.2) * 0.2973 + Math.pow(g, 2.2)
                        * 0.6274 + Math.pow(b, 2.2) * 0.0753), 1 / 2.2);

        return result;
    }

    /**
     * 获取图像的灰度值数组
     *
     * @param image 图片
     * @return
     */
    public static int[][] getImageGrayArray(BufferedImage image) {
        int height = image.getHeight();
        int width = image.getWidth();
        int grayArray[][] = new int[width][height];

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                grayArray[i][j] = getImageGray(image.getRGB(i, j));
            }
        }

        return grayArray;
    }

    /**
     * 获取点的RGB数组
     *
     * @param i
     * @return
     */
    public static int[] getRgbArray(int i) {
        String argb = Integer.toHexString(i); // 将十进制的颜色值转为十六进制
        // argb分别代表透明,红,绿,蓝 分别占16进制2位
        int r = (int) (Integer.parseInt(argb.substring(2, 4), 16));//后面参数为使用进制
        int g = (int) (Integer.parseInt(argb.substring(4, 6), 16));
        int b = (int) (Integer.parseInt(argb.substring(6, 8), 16));
        if (r > 255) {
            r = 255;
        }
        if (g > 255) {
            g = 255;
        }
        if (b > 255) {
            b = 255;
        }

        return new int[]{r, g, b};
    }

    /**
     * 前景色钝化
     *
     * @param bImage
     * @param count
     * @return 返回钝化后的图片
     * 如果钝化失败，返回null
     */
    public static BufferedImage foregroundSmooth(BufferedImage bImage, int count, int checkType) {
        BufferedImage image = bImage;
        for (int i = 0; i < count; i++) {
            image = removeBlackLine(image, checkType);
        }
        return image;
    }

    /**
     * 去除超分辨率处理后前景色的黑色细线
     *
     * @param bImage
     * @return
     */
    public static BufferedImage removeBlackLine(BufferedImage bImage, int checkType) {

        int w = bImage.getWidth(null);
        int h = bImage.getHeight(null);
        // 复制图片
        BufferedImage image = new BufferedImage(w, h,
                5);
        Graphics2D g2 = image.createGraphics();
        g2.drawImage(bImage, 0, 0, null);

        int height = image.getHeight();
        int width = image.getWidth();
        int rgbArray[][] = getImageGrayArray(image);
        // 获取前景色阈值
        int threshold = otsuThreshold(rgbArray, height, width, null, null);

        for (int i = 0; i < width; i++) {
            PrintBarUtil.print("正在优化前景色", i, width, width / 100);
            for (int j = 0; j < height; j++) {
                if (rgbArray[i][j] > threshold * 1.2) {   // 将前景色的像素点进行最大值池化
                    checkBlackPoint(bImage, image, 1.15f,
                            rgbArray, i, j, 2, threshold, checkType);
                }
            }
        }

        BufferedImage smooth = smooth(image);
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if (rgbArray[i][j] > threshold * 1.2) {   // 将前景色的像素点进行最大值池化
                    image.setRGB(i, j, smooth.getRGB(i, j));
                }
            }
        }

        return image;
    }

    /**
     * 对范围框内所有大于阈值的点的RGB颜色取均值
     *
     * @param source
     * @param target
     * @param rgbArray
     * @param x
     * @param y
     * @param range
     * @param threshold
     */
    public static void smoothTransition(BufferedImage source, BufferedImage target, int[][] rgbArray, int x, int y, int range, int threshold) {
        int height = source.getHeight();
        int width = source.getWidth();
        int[] rgb = getRgbArray(source.getRGB(x, y));
        for (int i = x - range; i < x + range && i >= 0 && i < width; i++) {
            for (int j = y - range; j < y + range && j >= 0 && j < height; j++) {
                if (i == x && j == y) continue;
                if (rgbArray[i][j] < threshold) {
                    rgb = colorMean(rgb, getRgbArray(source.getRGB(i, j)));
                }
            }
        }
        int result = new Color(rgb[0], rgb[1], rgb[2]).getRGB();
        target.setRGB(x, y, result);
    }

    /**
     * 获取颜色均值
     *
     * @param array1
     * @param array2
     * @return
     */
    public static int[] colorMean(int[] array1, int[] array2) {
        int r = (array1[0] + array2[0]) / 2;
        int g = (array1[1] + array2[1]) / 2;
        int b = (array1[2] + array2[2]) / 2;
        return new int[]{r, g, b};
    }

    /**
     * 随机选择范围内一个灰度最大的点覆盖中心点
     *
     * @param source    原图片
     * @param target    目标图片
     * @param ratio     阈值设定比率
     * @param grayArray 相对灰度数组
     * @param x         x坐标
     * @param y         y坐标
     * @param range     池化范围
     * @param threshold 前景色阈值
     * @param checkType 像素点赋值方式
     */
    public static void checkBlackPoint(
            BufferedImage source, BufferedImage target, float ratio,
            int[][] grayArray, int x, int y, int range, int threshold,
            int checkType) {
        int height = source.getHeight();
        int width = source.getWidth();
        int max = grayArray[x][y];
        List<Integer> list = new LinkedList<>();
        list.add(x);
        list.add(y);

        for (int i = x - range; i < x + range && i >= 0 && i < width; i++) {
            for (int j = y - range; j < y + range && j >= 0 && j < height; j++) {
                if (i == x && j == y) {
                    continue;
                }
                if (grayArray[i][j] * 1.0 >= threshold * ratio) {
                    if (max < grayArray[i][j]) {
                        max = grayArray[i][j];
                        list.clear();
                        list.add(i);
                        list.add(j);
                        continue;
                    } else if (max == grayArray[i][j]) {
                        list.add(i);
                        list.add(j);
                    }
                }
            }
        }

        Color color = null;
        if (checkType == ImageUtil.RANDOM_MAXPOINT) {
            color = randomCheckBlackPoint(source, list);
        } else if (checkType == ImageUtil.MEAN_MAXPOINT) {
            color = meanCheckBlackPoint(source, list);
        }

        target.setRGB(x, y, color.getRGB());
    }

    /**
     * 随机选择点集合内的一个点的颜色
     *
     * @param source    原图像
     * @param pointList 待选点坐标集合
     * @return Color
     */
    public static Color meanCheckBlackPoint(BufferedImage source, List<Integer> pointList) {
        int size = pointList.size() / 2;
        int[] rgb = new int[3];

        for (int i = 0; i < size; i++) {
            int[] rgbArray = getRgbArray(source.getRGB(pointList.get(i * 2), pointList.get(i * 2 + 1)));
            for (int j = 0; j < 3; j++) {
                rgb[j] += rgbArray[j];
            }
        }
        rgb[0] /= size;
        rgb[1] /= size;
        rgb[2] /= size;
        return new Color(rgb[0], rgb[1], rgb[2]);
    }

    /**
     * 获取点集合内所有点的颜色均值
     *
     * @param source    原图像
     * @param pointList 待选点坐标集合
     * @return Color
     */
    public static Color randomCheckBlackPoint(BufferedImage source, List<Integer> pointList) {
        int size = pointList.size() / 2;
        int index = 0;
        if (size - 1 > 0) {
            index = new Random().nextInt(size - 1);
        }
        int[] rgb = getRgbArray(source.getRGB(pointList.get(index * 2), pointList.get(index * 2 + 1)));
        return new Color(rgb[0], rgb[1], rgb[2]);
    }

    /**
     * 图片锐化
     *
     * @param image
     * @return
     */
    public static BufferedImage sharpen(BufferedImage image) {
        float[] elements = {0.0f, -1.0f, 0.0f, -1.0f, 5.0f, -1.0f, 0.0f, -1.0f, 0, 0f};
        return imageConvolveOP(image, elements);
    }

    /**
     * 图片钝化
     * 这里采用高斯算子钝化
     *
     * @param image
     * @return
     */
    public static BufferedImage smooth(BufferedImage image) {
        float[] elements = {0.0625f, 0.125f, 0.0625f, 0.125f, 0.25f, 0.125f,
                0.0625f, 0.125f, 0.0625f};
//        float[] elements = {0.125f, 0.0625f, 0.125f, 0.0625f, 0.0625f, 0.0625f,
//                0.125f, 0.0625f, 0.125f};
        return imageConvolveOP(image, elements);
    }

    /**
     * 使用算子对图像做卷积
     *
     * @param elements
     * @param image
     * @return
     */
    public static BufferedImage imageConvolveOP(BufferedImage image, float[] elements) {
        BufferedImage bimg = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
        Kernel kernel = new Kernel(3, 3, elements);
        ConvolveOp cop = new ConvolveOp(kernel, ConvolveOp.EDGE_NO_OP, null);
        cop.filter(image, bimg);
        return bimg;
    }

    /**
     * 自己加周围8个灰度值再除以9，算出其相对灰度值
     *
     * @param gray 灰度值数组
     * @param x    x坐标
     * @param y    y坐标
     * @param w    图像宽度
     * @param h    图像高度
     * @return 返回该点相对灰度
     */
    public static int getRelativeGray(int gray[][], int x, int y, int w, int h) {
        int rs = gray[x][y] + (x == 0 ? 255 : gray[x - 1][y])
                + (x == 0 || y == 0 ? 255 : gray[x - 1][y - 1])
                + (x == 0 || y == h - 1 ? 255 : gray[x - 1][y + 1])
                + (y == 0 ? 255 : gray[x][y - 1])
                + (y == h - 1 ? 255 : gray[x][y + 1])
                + (x == w - 1 ? 255 : gray[x + 1][y])
                + (x == w - 1 || y == 0 ? 255 : gray[x + 1][y - 1])
                + (x == w - 1 || y == h - 1 ? 255 : gray[x + 1][y + 1]);
        return rs / 9;
    }

    /**
     * 改变图片DPI
     *
     * @param file
     * @param xDensity
     * @param yDensity
     */
    public static BufferedImage getImgAndSetDpi(File file, int xDensity, int yDensity) {
        BufferedImage image = null;
        try {
            image = ImageIO.read(file);
            JPEGImageEncoder jpegEncoder = JPEGCodec.createJPEGEncoder(new FileOutputStream(file));
            JPEGEncodeParam jpegEncodeParam = jpegEncoder.getDefaultJPEGEncodeParam(image);
            jpegEncodeParam.setDensityUnit(JPEGEncodeParam.DENSITY_UNIT_DOTS_INCH);
            jpegEncoder.setJPEGEncodeParam(jpegEncodeParam);
            jpegEncodeParam.setQuality(0.75f, false);
            jpegEncodeParam.setXDensity(xDensity);
            jpegEncodeParam.setYDensity(yDensity);
            jpegEncoder.encode(image, jpegEncodeParam);
            image.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }

    /**
     * 通过OTSU大津算法计算分割阈值
     *
     * @param grayArray      图像的灰度数组
     * @param height
     * @param width
     * @param thresholdValue 是否需要设定分割阈值，只有小于阈值才将其计入OSTU阈值分割计算点之内
     * @param type           分割阈值的类型， type < 0 =>小于thresholdValue  type > 0 =>小于thresholdValue
     * @return
     */
    public static int otsuThreshold(int grayArray[][], int height, int width,
                                    @Nullable Integer thresholdValue, @Nullable Integer type) {
        final int GrayScale = 256;
        // 每个灰度像素的数量
        int[] pixelCount = new int[GrayScale];
        // 像素点的数量
        int pixelSum = 0;

        //=========================统计灰度级中每个像素在整幅图像中的个数===============
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if (thresholdValue != null) {
                    if (type < 0 && grayArray[i][j] >= thresholdValue) {
                        continue;
                    } else if (type > 0 && grayArray[i][j] <= thresholdValue) {
                        continue;
                    }
                }
                pixelSum += 1;
                pixelCount[grayArray[i][j]]++;
            }
        }

        return calculateOSTUThreshold(pixelCount, pixelSum);
    }

    public static int calculateOSTUThreshold(int[] pixelCount, int pixelSum) {
        final int GrayScale = 256;
        // 每个像素点所占的比例
        float[] pixelPro = new float[GrayScale];
        // 分割的阈值点
        int threshold = 0;

        // ========================计算每个像素在整幅图像中的比例=========================
        for (int i = 0; i < GrayScale; i++) {
            pixelPro[i] = (float) pixelCount[i] / pixelSum;
        }

        // ===========================遍历灰度级[0,255]======================================
        // backgroundRatio为背景像素点占整幅图像的比例
        // prospectRatio为前景像素点占整幅图像的比例
        // backGrayAverage为背景像素的平均灰度
        // proGrayAverage为前景像素的平均灰度
        // grayAverage为整幅图像的平均灰度
        // 公式:g = backgroundRatio * pow((backGrayAverage - grayAverage), 2) + prospectRatio * pow((proGrayAverage - grayAverage), 2)
        // deltaTmp，deltaMax记录中间值与结果最大值
        float backgroundRatio, prospectRatio, u0tmp, u1tmp, backGrayAverage, proGrayAverage, grayAverage;
        double deltaTmp, deltaMax = 0;
        for (int i = 0; i < GrayScale; i++)     // i作为阈值
        {
            // 初始化
            backgroundRatio = prospectRatio = u0tmp = u1tmp = backGrayAverage = proGrayAverage = grayAverage = 0;
            deltaTmp = 0;
            for (int j = 0; j < GrayScale; j++) {
                if (j <= i)   //背景部分
                {
                    backgroundRatio += pixelPro[j];
                    u0tmp += j * pixelPro[j]; // u0tmp=像素的灰度*像素占的比例
                } else   //前景部分
                {
                    prospectRatio += pixelPro[j];
                    u1tmp += j * pixelPro[j];
                }
            }
            backGrayAverage = u0tmp / backgroundRatio;
            proGrayAverage = u1tmp / prospectRatio;
            grayAverage = u0tmp + u1tmp;
            deltaTmp = backgroundRatio * pow((backGrayAverage - grayAverage), 2) + prospectRatio * pow((proGrayAverage - grayAverage), 2);
            if (deltaTmp > deltaMax) {
                deltaMax = deltaTmp;
                threshold = i;
            }
        }

        return threshold;
    }

//
//    public static List<BufferedImage> getSegImgList(BufferedImage bufferedImage, String type){
//        Mat imgMat = getImgMat(bufferedImage, type);
//        List<Mat> mats = handleByOpenCV(imgMat);
//        List<BufferedImage> bufferedImages = matToBufferImag(mats);
//        return bufferedImages;
//    }
//
//    public static List<BufferedImage> getSegImgList(String imgSrc){
//        Mat imgMat = getImgMat(imgSrc);
//        List<Mat> mats = handleByOpenCV(imgMat);
//        List<BufferedImage> bufferedImages = matToBufferImag(mats);
//        return bufferedImages;
//    }
//
//    public static Mat getImgMat(String src){
//        if (!new File(src).exists()) {
//            System.out.println("文件不存在");
//            return null;
//        }
//        Mat img = Imgcodecs.imread(src);
//        return img;
//    }
//
//    public static Mat getImgMat(BufferedImage original, String type){
//        if (original == null) {
//            return null;
//        }
//        ByteArrayOutputStream out = new ByteArrayOutputStream();
//        try {
//            ImageIO.write(original, type, out);
//        } catch (IOException e) {
//            //log.error(e.getMessage());
//        }
//        ByteBuffer data = ByteBuffer.wrap(out.toByteArray());
//        return new Mat(original.getWidth(), original.getHeight(), original.getType(), data);
//    }
//
//    /**
//     * MSER+NMS检测法识别文字位置
//     * @param img
//     * @return
//     * @throws Exception
//     */
//    public static List<Mat> handleByOpenCV(Mat img) {
//        List<Mat> result = new LinkedList<>();
//        Mat gray = new Mat();  // 灰度化图片
//        cvtColor(img, gray, COLOR_BGR2GRAY); // 灰度化
//
//        // 调用MSER算法
//        MSER mser = MSER.create();
//        List<MatOfPoint> msers = new ArrayList<MatOfPoint>() {
//        }; // 文本区域点坐标集合
//        MatOfRect bboxes = new MatOfRect();
//        mser.detectRegions(gray, msers, bboxes);
//
//        Rect[] rects = nms(bboxes.toArray(), 0.5);
//
//        // 将图片按识别结果集切分
//        for (Rect rect : rects){
//            Mat segmentationImg = new Mat(img, rect);
//            Mat resultImg = new Mat();
//            segmentationImg.copyTo(resultImg);
//
//            result.add(resultImg);
//        }
//
//        for (Rect rect : rects) {
//            Imgproc.rectangle(gray, rect, new Scalar(0, 255, 0));
//        }
//        BufferedImage testImg = (BufferedImage) HighGui.toBufferedImage(gray);
//        try {
//            ImageIO.write(testImg, "jpg", new File("D:" + File.separator + "new123.jpg"));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        return result;
////        HighGui.imshow("test", gray);
////        HighGui.waitKey(0);
//    }
//
//    /**
//     * 非极大值抑制算法(NMS)
//     *
//     * @param rects
//     * @param overlapThresh
//     * @return
//     */
//    public static Rect[] nms(@NotNull Rect[] rects, double overlapThresh) {
//        if (rects.length == 0) {
//            return rects;
//        }
//        List<Rect> result = new LinkedList<>();
//
//        // 对矩形按右下角坐标进行排序
//        Arrays.sort(rects, new Comparator<Rect>() {
//            @Override
//            public int compare(Rect o1, Rect o2) {
//                if (o1.y + o1.height > o2.y + o2.height) {
//                    return 1;
//                } else if (o1.y + o1.height < o2.y + o2.height) {
//                    return -1;
//                } else {
//                    return 0;
//                }
//            }
//        });
//
//        List<Rect> cache = new LinkedList<>();
//        while (rects.length > 0) {
//            Rect rectOrder = rects[rects.length - 1];
//            result.add(rectOrder);
//
//            cache.clear();
//            for (int i = 0; i < rects.length - 1; i++) {
//                Rect rect = rects[i];
//                if (rect == null){
//                    continue;
//                }
//                // 计算重叠区域的面积
//                int x1 = FastMath.max(rectOrder.x, rect.x);  // 左上角的点
//                int x2 = FastMath.min(rectOrder.x + rectOrder.width, rect.x + rectOrder.width);   // 右下角的点
//                int y1 = FastMath.max(rectOrder.y, rect.y); // 左上角的点
//                int y2 = FastMath.min(rectOrder.y + rectOrder.height, rect.y + rectOrder.height); // 右下角的点
//
//                int width = 0 > x2 - x1 + 1 ? 0 : x2 - x1 + 1;    // 计算两矩形重叠的宽度
//                int height = 0 > y2 - y1 + 1 ? 0 : y2 - y1 + 1;   // 计算两矩形重叠的高度
//
//                long intersection = width * height;             // 重叠的面积
//                double overlap = intersection / rect.area();      // 重叠的比率
//
//                // 重叠的比率小于一定值，保存，进行下一步运算
//                if (overlap < overlapThresh) {
//                    cache.add(rect);
//                }
//            }
//            Rect[] cacheArray = new Rect[cache.size()];
//            cache.toArray(cacheArray);
//            rects = cacheArray;
//        }
//
//        Rect[] resultArray = new Rect[result.size()];
//        result.toArray(resultArray);
//        return resultArray;
//    }
//
//    public static List<BufferedImage> matToBufferImag(List<Mat> matList){
//        List<BufferedImage> result = new ArrayList<>(matList.size());
//        matList.forEach(item->{
//            result.add((BufferedImage) HighGui.toBufferedImage(item));
//        });
//        return result;
//    }
//
//
//    public static void main(String args[]) throws Exception {
////        String image = "D:\\迅雷下载\\CQA-tuling\\酷Q Air\\data\\image\\F5CF36E2586D4852364723EAB792296C.jpg.cqimg";
////        String img2 = "C:\\Users\\Administrator\\Desktop\\timg.jpg";
////        String img3 = "D:\\image\\00A5A7AE8C5F7C4D4A9FC09447095EE0.jpg";
////        File file = new File(img3);
////        BufferedImage read = null;
////        try {
////            read = ImageIO.read(file);
////        } catch (IOException e) {
////            e.printStackTrace();
////        }
////        System.out.println(read);
//
//        String scr = "C:\\Users\\Administrator\\Desktop\\QQ20200611111037.jpg";
//        String scr2 = "D:\\new123.jpg";
//        Mat imgMat = getImgMat(scr2);
//        handleByOpenCV(imgMat);
//    }
//

}
