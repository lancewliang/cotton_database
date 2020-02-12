package ui.util;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.awt.image.PixelGrabber;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class ImageDemo {

  public void binaryImage(String path1, String path2) throws IOException {
    File file = new File(path1);
    BufferedImage image = ImageIO.read(file);

    int width = image.getWidth();
    int height = image.getHeight();

    BufferedImage grayImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_BINARY);
    for (int i = 0; i < width; i++) {
      for (int j = 0; j < height; j++) {
        int rgb = image.getRGB(i, j);
        grayImage.setRGB(i, j, rgb);
      }
    }

    File newFile = new File(path2);
    ImageIO.write(grayImage, "jpg", newFile);
  }

  public void grayImage(String path1, String path2) throws IOException {
    File file = new File(path1);
    BufferedImage image = ImageIO.read(file);

    int width = image.getWidth();
    int height = image.getHeight();

    BufferedImage grayImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
    for (int i = 0; i < width; i++) {
      for (int j = 0; j < height; j++) {
        int rgb = image.getRGB(i, j);
        grayImage.setRGB(i, j, rgb);
      }
    }

    File newFile = new File(path2);
    ImageIO.write(grayImage, "jpg", newFile);
  }

  public void scaling(String f1, String f2) throws IOException {
    double s = 2;
    BufferedImage originalPic = ImageIO.read(new File(f1));
    int srcW = originalPic.getWidth();

    int srcH = originalPic.getHeight();
    AffineTransform tx = new AffineTransform();

    tx.scale(s, s);
    BufferedImage newPic = new BufferedImage((int) (srcW * s), (int) (srcH * s), BufferedImage.TYPE_3BYTE_BGR);
    AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR);

    op.filter(originalPic, newPic);
    ImageIO.write(newPic, "jpg", new File(f2));
  }

  public void scale2(String f1, String f2) throws IOException {
    BufferedImage originalPic = ImageIO.read(new File(f1));
    float s = 1f;
    int srcW = originalPic.getWidth();

    int srcH = originalPic.getHeight();

    int newW = Math.round(srcW * s);

    int newH = Math.round(srcH * s);

    // 先做水平方向上的伸缩变换

    BufferedImage tmp = new BufferedImage(newW, newH, originalPic.getType());

    Graphics2D g = tmp.createGraphics();

    for (int x = 0; x < newW; x++) {

      g.setClip(x, 0, 1, srcH);

      // 按比例放缩

      g.drawImage(originalPic, x - x * srcW / newW, 0, null);

    }

    // 再做垂直方向上的伸缩变换

    BufferedImage dst = new BufferedImage(newW, newH, originalPic.getType());

    g = dst.createGraphics();

    for (int y = 0; y < newH; y++) {

      g.setClip(0, y, newW, 1);

      // 按比例放缩

      g.drawImage(tmp, 0, y - y * srcH / newH, null);

    }
    ImageIO.write(dst, "jpg", new File(f2));

  }

  public void sharp(String f1, String f2) {

    try {

      BufferedImage originalPic = ImageIO.read(new File(f1));

      int imageWidth = originalPic.getWidth();
      int imageHeight = originalPic.getHeight();

      BufferedImage newPic = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_3BYTE_BGR);
      // float[] data = { -1.0f, -1.0f, -1.0f, -1.0f, 10.0f, -1.0f, -1.0f,
      // -1.0f, -1.0f };
      float[] data = { -1.0f, -1.0f, -1.0f, -1.0f, 30.0f, -1.0f, -1.0f, -1.0f, -1.0f };
      Kernel kernel = new Kernel(3, 3, data);
      ConvolveOp co = new ConvolveOp(kernel, ConvolveOp.EDGE_NO_OP, null);
      co.filter(originalPic, newPic);
      ImageIO.write(newPic, "jpg", new File(f2));
    } catch (Exception e) {
      e.printStackTrace();
    }

  }

  /*
   * public void sharp2(String f1, String f2) throws Exception { int iw, ih;
   * int[] pixels; BufferedImage image = ImageIO.read(new File(f1)); iw =
   * image.getWidth(); ih = image.getHeight();
   * 
   * pixels = new int[iw * ih]; PixelGrabber pg = new
   * PixelGrabber(image.getSource(), 0, 0, iw, ih, pixels, 0, iw); try {
   * pg.grabPixels(); } catch (InterruptedException e) { e.printStackTrace(); }
   * // 象素的中间变量 int tempPixels[] = new int[iw * ih]; for (int i = 0; i < iw *
   * ih; i++) { tempPixels[i] = pixels[i]; } // 对图像进行尖锐化处理，Alpha值保持不变 ColorModel
   * cm = ColorModel.getRGBdefault(); for (int i = 1; i < ih - 1; i++) {
   * 
   * for (int j = 1; j < iw - 1; j++) {
   * 
   * int alpha = cm.getAlpha(pixels[i * iw + j]);
   * 
   * // 对图像进行尖锐化 int red6 = cm.getRed(pixels[i * iw + j + 1]); int red5 =
   * cm.getRed(pixels[i * iw + j]); int red8 = cm.getRed(pixels[(i + 1) * iw +
   * j]); int sharpRed = Math.abs(red6 - red5) + Math.abs(red8 - red5); int
   * green5 = cm.getGreen(pixels[i * iw + j]); int green6 = cm.getGreen(pixels[i
   * * iw + j + 1]); int green8 = cm.getGreen(pixels[(i + 1) * iw + j]); int
   * sharpGreen = Math.abs(green6 - green5) + Math.abs(green8 - green5); int
   * blue5 = cm.getBlue(pixels[i * iw + j]); int blue6 = cm.getBlue(pixels[i *
   * iw + j + 1]); int blue8 = cm.getBlue(pixels[(i + 1) * iw + j]); int
   * sharpBlue = Math.abs(blue6 - blue5) + Math.abs(blue8 - blue5); if (sharpRed
   * > 255) { sharpRed = 255; } if (sharpGreen > 255) { sharpGreen = 255; } if
   * (sharpBlue > 255) { sharpBlue = 255; } tempPixels[i * iw + j] = alpha << 24
   * | sharpRed << 16 | sharpGreen << 8 | sharpBlue; } } BufferedImage newPic =
   * new BufferedImage(iw, ih, BufferedImage.TYPE_3BYTE_BGR);
   * ImageIO.write(newPic, "jpg", new File(f2)); }
   */
  static String _1path1 = "D:\\lwwork\\ExamKing\\tesseract-ocr-test\\ex_pic\\1011jks1.jpg";
  static String _1path11 = "D:\\lwwork\\ExamKing\\tesseract-ocr-test\\ex_pic\\1011jks1_00000.jpg";
  static String _1path2 = "D:\\lwwork\\ExamKing\\tesseract-ocr-test\\ex_pic\\1011jks1_11111.jpg";
  static String _1path3 = "D:\\lwwork\\ExamKing\\tesseract-ocr-test\\ex_pic\\1011jks1_22222.jpg";

  static String _1path4 = "D:\\lwwork\\ExamKing\\tesseract-ocr-test\\ex_pic\\1011jks1_3333.jpg";
  static String _1path5 = "D:\\lwwork\\ExamKing\\tesseract-ocr-test\\ex_pic\\1011jks1_4444.jpg";

  public static void main(String[] args) throws Exception {
    ImageDemo demo = new ImageDemo();

   // demo.scale2(_1path1, _1path11);
    demo.scaling(_1path1, _1path2);
    demo.binaryImage(_1path2, _1path3);
    demo.grayImage(_1path3, _1path4);
    demo.sharp(_1path4, _1path5);

  }

}