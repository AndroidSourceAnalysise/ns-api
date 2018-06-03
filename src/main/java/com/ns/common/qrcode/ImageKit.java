
package com.ns.common.qrcode;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;

/**
 * description: 图片操作工具类
 * date: 2017-01-18 9:56
 *
 * @author ArvinZou
 * @version 1.0
 * @since JDK 1.8
 */
public class ImageKit {

    private ImageKit() {
    }

    /**
     * 获取Image对象
     *
     * @param imageURL 图片URL
     * @return BufferedImage
     */
    public static BufferedImage getImageURL(String imageURL) throws MalformedURLException {
//        BufferedInputStream bis = null;
//        HttpURLConnection httpUrl = null;
//        URL url = null;
//        try {
//            url = new URL(imageURL);
//            httpUrl = (HttpURLConnection) url.openConnection();
//            httpUrl.connect();
//            bis = new BufferedInputStream(httpUrl.getInputStream());
//
//            return ImageIO.read(bis);
//        } catch (IOException ioe) {
//            return null;
//        } finally {
//            try {
//                bis.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            httpUrl.disconnect();
//        }

        Image src = Toolkit.getDefaultToolkit().getImage(new URL(imageURL));
        BufferedImage image = toBufferedImage(src);//Image to BufferedImage
        return image;
    }

    /**
     * @param imagePath
     * @return
     */
    public static BufferedImage getImagePath(String imagePath) {
//        FileInputStream fis = null;
//        try {
//            fis = new FileInputStream(imagePath);
//            return ImageIO.read(fis);
//        } catch (FileNotFoundException e) {
//            return null;
//        } catch (IOException ioe) {
//            return null;
//        } finally {
//            try {
//                fis.close();
//            } catch (IOException e) {
//            }
//        }
        Image src = Toolkit.getDefaultToolkit().getImage(imagePath);
        BufferedImage image = toBufferedImage(src);//Image to BufferedImage

        return image;
    }

    /**
     * 压缩图片
     *
     * @param src       源图片BufferedImage
     * @param maxLength 长或者宽的最大值
     */

    public static void compressImage(BufferedImage src, String outImgPath, int maxLength) throws IOException {
        // 调整图片大小，并输出图片
        resize(src, outImgPath, maxLength);
    }

    /**
     * 压缩图片
     *
     * @param is        文件输入流
     * @param maxLength 长或者宽的最大值
     */

    public static void compressImage(InputStream is, String outImgPath, int maxLength) throws IOException {

        // 得到图片
        BufferedImage src = ImageIO.read(is);
        // 调整图片大小，并输出图片
        resize(src, outImgPath, maxLength);
    }

    /**
     * 指定长或者宽的最大值来压缩图片
     *
     * @param srcImgPath :源图片路径
     * @param outImgPath :输出的压缩图片的路径
     * @param maxLength  :长或者宽的最大值
     */
    public static void compressImage(String srcImgPath, String outImgPath, int maxLength) {

        // 得到图片
        BufferedImage src = getBufferedImage(srcImgPath);
        // 调整图片大小，并输出图片
        resize(src, outImgPath, maxLength);
    }

    // 调整图片大小，并输出图片
    private static void resize(BufferedImage src, String outImgPath, int maxLength) {
        if (null != src) {
            int old_w = src.getWidth();
            // 得到源图宽
            int old_h = src.getHeight();
            // 得到源图长
            int new_w = 0;
            // 新图的宽
            int new_h = 0;
            // 新图的长
            // 根据图片尺寸压缩比得到新图的尺寸
            if (old_w > old_h) {
                // 图片要缩放的比例
                new_w = maxLength;
                new_h = (int) Math.round(old_h * ((float) maxLength / old_w));
            } else {
                new_w = (int) Math.round(old_w * ((float) maxLength / old_h));
                new_h = maxLength;
            }
            disposeImage(src, outImgPath, new_w, new_h);
        }
    }

    /**
     * 将图片按照指定的图片尺寸压缩
     *
     * @param srcImgPath 源图片路径
     * @param outImgPath 压缩后输出图片路径
     * @param new_w      新图片宽度
     * @param new_h      新图片高度
     */
    public static void compress(String srcImgPath, String outImgPath, int new_w, int new_h) {
        BufferedImage src = getBufferedImage(srcImgPath);
        disposeImage(src, outImgPath, new_w, new_h);
    }

    private static BufferedImage getBufferedImage(String srcImgPath) {
        BufferedImage srcImage = null;

        try {
            FileInputStream in = new FileInputStream(srcImgPath);
            srcImage = ImageIO.read(in);
        } catch (IOException e) {
            System.out.println("读取图片文件出错！" + e.getMessage());
        }

        return srcImage;
    }

    /**
     * 处理图片
     *
     * @param src        源图片
     * @param outImgPath 输出目标图片路径
     * @param new_w      新宽度
     * @param new_h      新高度
     */
    public synchronized static void disposeImage(BufferedImage src, String outImgPath, int new_w, int new_h) {
        // 得到图片
        int old_w = src.getWidth();
        // 得到源图宽
        int old_h = src.getHeight();
        // 得到源图长
        BufferedImage newImg = new BufferedImage(new_w, new_h, BufferedImage.TYPE_INT_RGB);
        // 判断输入图片的类型
        /*
         * switch (src.getType()) { case 13: // png,gifnewImg = new
		 * BufferedImage(new_w, new_h, // BufferedImage.TYPE_4BYTE_ABGR); break;
		 * default: newImg = new BufferedImage(new_w, new_h,
		 * BufferedImage.TYPE_INT_RGB); break; }
		 */
        Graphics2D g = newImg.createGraphics();
        // 从原图上取颜色绘制新图
        g.drawImage(src, 0, 0, old_w, old_h, null);
        g.dispose();
        // 根据图片尺寸压缩比得到新图的尺寸
        newImg.getGraphics().drawImage(src.getScaledInstance(new_w, new_h, Image.SCALE_SMOOTH), 0, 0, null);
        // 调用方法输出图片文件
        outImage(outImgPath, newImg);
    }

    /**
     * 将图片文件输出到指定的路径，并可设定压缩质量
     *
     * @param outImgPath 文件输出路径
     * @param newImg     压缩后图片
     */
    private static void outImage(String outImgPath, BufferedImage newImg) {
        // 判断输出的文件夹路径是否存在，不存在则创建
        File file = new File(outImgPath);
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        } // 输出到文件流
        try {
            ImageIO.write(newImg, outImgPath.substring(outImgPath.lastIndexOf(".") + 1), new File(outImgPath));
        } catch (FileNotFoundException e) {
            System.out.println("ERROR:" + e.getMessage());
        } catch (IOException e) {
            System.out.println("ERROR:" + e.getMessage());
        }
    }

    /**
     * 绘制图片
     *
     * @param baseImage 绘制底图
     * @param addImage  附加图片
     * @param x         绘画 x 点
     * @param y         绘画 y 点
     * @param width     绘画宽度
     * @param height    绘画高度
     * @param outPath   目标文件生成位置  Demo:
     *                  windows - E:\\out.png , linux - home/out.png
     *                  "."只能出现一次，且在后缀名前面
     */
    public static void draw(BufferedImage baseImage, BufferedImage addImage, int x, int y, int width, int height, String outPath) {
        try {
            Graphics g = baseImage.getGraphics();
            g.drawImage(addImage, x, y, width, height, null);
            OutputStream outImage = new FileOutputStream(outPath);
            String format = outPath.substring(outPath.lastIndexOf(".") + 1, outPath.length());
            ImageIO.write(baseImage, format, outImage);
        } catch (IOException e) {
            System.out.println("Exception:" + e.getMessage());
        }
    }

    /**
     * 图片裁切
     *
     * @param x         选择区域左上角的x坐标
     * @param y         选择区域左上角的y坐标
     * @param width     选择区域的宽度
     * @param height    选择区域的高度
     * @param srcFis    源图片文件输入流
     * @param srcSuffix 源图片文件后缀
     * @param descpath  裁切后图片的保存路径
     */
    public static void cut(int x, int y, int width, int height,
                           InputStream srcFis, String srcSuffix, String descpath) {

        ImageInputStream iis = null;
        try {
            Iterator<ImageReader> it = ImageIO.getImageReadersByFormatName(srcSuffix);
            ImageReader reader = it.next();
            iis = ImageIO.createImageInputStream(srcFis);
            reader.setInput(iis, true);
            ImageReadParam param = reader.getDefaultReadParam();
            Rectangle rect = new Rectangle(x, y, width, height);
            param.setSourceRegion(rect);
            BufferedImage bi = reader.read(0, param);
            ImageIO.write(bi, srcSuffix, new File(descpath));
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (srcFis != null) {
                try {
                    srcFis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                srcFis = null;
            }
            if (iis != null) {
                try {
                    iis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                iis = null;
            }
        }
    }

    /**
     * Image convert to BufferedImage
     *
     * @param image in
     * @return BufferedImage
     */
    public static BufferedImage toBufferedImage(Image image) {
        if (image instanceof BufferedImage) {
            return (BufferedImage) image;
        }
        // This code ensures that all the pixels in the image are loaded
        image = new ImageIcon(image).getImage();
        BufferedImage bimage = null;
        GraphicsEnvironment ge = GraphicsEnvironment
                .getLocalGraphicsEnvironment();
        try {
            int transparency = Transparency.OPAQUE;
            GraphicsDevice gs = ge.getDefaultScreenDevice();
            GraphicsConfiguration gc = gs.getDefaultConfiguration();
            bimage = gc.createCompatibleImage(image.getWidth(null),
                    image.getHeight(null), transparency);
        } catch (HeadlessException e) {
            // The system does not have a screen
        }
        if (bimage == null) {
            // Create a buffered image using the default color model
            int type = BufferedImage.TYPE_INT_RGB;
            bimage = new BufferedImage(image.getWidth(null),
                    image.getHeight(null), type);
        }
        // Copy image to buffered image
        Graphics g = bimage.createGraphics();
        // Paint the image onto the buffered image
        g.drawImage(image, 0, 0, null);
        g.dispose();
        return bimage;
    }


    /**
     * 绘制文本
     *
     * @param color
     * @param font
     * @param x
     * @param y
     * @param text
     * @param baseUrl
     * @param tempPath
     */
    public static void graphics(Color color, Font font, int x, int y, String text, String baseUrl, String tempPath) {
        /*String imageSuffix = baseUrl.substring(baseUrl.lastIndexOf("."));
        BufferedImage imageBase = null;
        try {
            imageBase = ImageKit.getImageURL(baseUrl);
            Graphics graphics = imageBase.getGraphics();

            graphics.setColor(color);
            graphics.setFont(font);
            graphics.drawString(text, x, y);

            FileUtil.mkDir(tempPath);
            OutputStream outImage = new FileOutputStream(tempPath);
            ImageIO.write(imageBase, "jpeg", outImage);
            //关闭流
//            baseIn.close();
            outImage.close();
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }
}