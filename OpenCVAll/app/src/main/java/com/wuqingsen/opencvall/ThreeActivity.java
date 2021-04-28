package com.wuqingsen.opencvall;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.io.IOException;

/**
 * OpenCV重要的四个类
 * Mat(矩阵)：主要定义Mat对象；bitmap位图早OpenCV都要转换成mat
 * Core：对Mat进行计算
 * ImgProc:对图像的处理
 * Utils：Mat和Bitmap之间的转化
 * <p>
 * 官网说明文档：https://docs.opencv.org/java/3.0.0/
 */
public class ThreeActivity extends AppCompatActivity {

    Button button1, button2, button3, button4, button5;
    ImageView image1, image2;
    Bitmap srcBit, dstBit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_three);

        boolean isSuccess = OpenCVLoader.initDebug();
        if (isSuccess) {
            Log.w("wqs", "OpenCVLoader成功");
        } else {
            Log.w("wqs", "OpenCVLoader失败");
        }
        button1 = findViewById(R.id.button1);
        button2 = findViewById(R.id.button2);
        button3 = findViewById(R.id.button3);
        button4 = findViewById(R.id.button4);
        button5 = findViewById(R.id.button5);
        image1 = findViewById(R.id.image1);
        image2 = findViewById(R.id.image2);

        //灰色
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                srcBit = ((BitmapDrawable) ((ImageView) image1).getDrawable()).getBitmap();
                Mat mat1 = null;
                try {
                    mat1 = Utils.loadResource(ThreeActivity.this, R.drawable.icon_one);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Imgproc.cvtColor(mat1, mat1, Imgproc.COLOR_BGRA2GRAY);
                dstBit = Bitmap.createBitmap(mat1.width(), mat1.height(), Bitmap.Config.ARGB_8888);
                Utils.matToBitmap(mat1, dstBit);
                image2.setImageBitmap(dstBit);
                mat1.release();
            }
        });

        //灰度（二值化）
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Mat mat1 = null;
                try {
                    mat1 = Utils.loadResource(ThreeActivity.this, R.drawable.icon_one);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //图片变灰色
                Imgproc.cvtColor(mat1, mat1, Imgproc.COLOR_BGRA2GRAY);
                dstBit = Bitmap.createBitmap(mat1.width(), mat1.height(), Bitmap.Config.ARGB_8888);
                //灰度
                Imgproc.adaptiveThreshold(mat1, mat1, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C
                        , Imgproc.THRESH_BINARY, 13, 5);
                dstBit = Bitmap.createBitmap(mat1.width(), mat1.height(), Bitmap.Config.ARGB_8888);
                Utils.matToBitmap(mat1, dstBit);
                image2.setImageBitmap(dstBit);
                mat1.release();
            }
        });

        //怀旧
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Mat mat1 = new Mat();
                srcBit = ((BitmapDrawable) ((ImageView) image1).getDrawable()).getBitmap();
                Utils.bitmapToMat(srcBit, mat1);

                //通道数
                int channel = mat1.channels();
                int width = mat1.cols();//宽度
                int height = mat1.rows();//高度
                Log.w("wqs", "通道数: " + channel + "\n宽度：" + width + "\n高度：" + height);
                byte[] p = new byte[channel];//保存一个像素点的数据，有多少通道数，p的长度是多少就是多少

                Mat matDst = new Mat(width, height, CvType.CV_8UC4);
                int b = 0, g = 0, r = 0;

                //读像素点的值
                for (int row = 0; row < height; row++) {
                    for (int col = 0; col < width; col++) {
                        mat1.get(row, col, p);
                        b = p[0] & 0xff;
                        g = p[1] & 0xff;
                        r = p[2] & 0xff;

                        int AB = (int) (0.272 * r + 0.534 * g + 0.131 * b);
                        int AG = (int) (0.349 * r + 0.686 * g + 0.168 * b);
                        int AR = (int) (0.393 * r + 0.769 * g + 0.189 * b);

                        AR = (AR > 255 ? 255 : (AR < 0 ? 0 : AR));
                        AG = (AG > 255 ? 255 : (AG < 0 ? 0 : AG));
                        AB = (AB > 255 ? 255 : (AB < 0 ? 0 : AB));

                        p[0] = (byte) AB;
                        p[1] = (byte) AG;
                        p[2] = (byte) AR;
                        matDst.put(row, col, p);
                    }
                }

                dstBit = Bitmap.createBitmap(mat1.width(), mat1.height(), Bitmap.Config.ARGB_8888);
                Utils.matToBitmap(matDst, dstBit);
                image2.setImageBitmap(dstBit);
                mat1.release();
                matDst.release();
            }
        });

        //连环画
        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Mat mat1 = new Mat();
                srcBit = ((BitmapDrawable) ((ImageView) image1).getDrawable()).getBitmap();
                Utils.bitmapToMat(srcBit, mat1);

                //通道数
                int channel = mat1.channels();
                int width = mat1.cols();//宽度
                int height = mat1.rows();//高度
                Log.w("wqs", "通道数: " + channel + "\n宽度：" + width + "\n高度：" + height);
                byte[] p = new byte[width * channel];//保存一行像素点数据

                Mat matDst = new Mat(width, height, CvType.CV_8UC4);
                int b = 0, g = 0, r = 0;

                //读像素点的值
                for (int row = 0; row < height; row++) {

                    mat1.get(row, 0, p);//第二个参数为0，从每一行第一列读取像素对象
                    for (int col = 0; col < width; col++) {
                        int index = channel * col;//某一行第0个像素点p[0],p[1],p[2],p[3]
                        //第一个像素点 p[4],p[5],p[6],p[7]
                        b = p[index] & 0xff;
                        g = p[index + 1] & 0xff;
                        r = p[index + 2] & 0xff;

                        int AB = Math.abs(b - g + b + r) * g / 256;
                        int AG = Math.abs(b - g + b + r) * r / 256;
                        int AR = Math.abs(g - b + g + r) * r / 256;

                        AR = (AR > 255 ? 255 : (AR < 0 ? 0 : AR));
                        AG = (AG > 255 ? 255 : (AG < 0 ? 0 : AG));
                        AB = (AB > 255 ? 255 : (AB < 0 ? 0 : AB));

                        p[index] = (byte) AB;
                        p[index + 1] = (byte) AG;
                        p[index + 2] = (byte) AR;
                    }
                    matDst.put(row, 0, p);//进行一行数据写入
                }

                dstBit = Bitmap.createBitmap(mat1.width(), mat1.height(), Bitmap.Config.ARGB_8888);
                Utils.matToBitmap(matDst, dstBit);
                image2.setImageBitmap(dstBit);
                mat1.release();
                matDst.release();
            }
        });

        //浮雕
        button5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Mat mat1 = new Mat();
                srcBit = ((BitmapDrawable) ((ImageView) image1).getDrawable()).getBitmap();
                Utils.bitmapToMat(srcBit, mat1);

                //通道数
                int channel = mat1.channels();
                int width = mat1.cols();//宽度
                int height = mat1.rows();//高度
                Log.w("wqs", "通道数: " + channel + "\n宽度：" + width + "\n高度：" + height);
                byte[] p = new byte[width * height * channel];//保存整个图片像素点数据
                byte[] pDst = new byte[width * height * channel];//保存整个图片像素点数据

                Mat matDst = new Mat(width, height, CvType.CV_8UC4);
                int b1 = 0, g1 = 0, r1 = 0, b2 = 0, g2 = 0, r2 = 0;

                mat1.get(0, 0, p);//获取全部像素数据，前两个参数为0表示从第一个像素的第一个通道读取像素点的值，
                //读像素点的值
                for (int row = 0; row < height; row++) {
                    for (int col = 0; col < width; col++) {
                        int index1 = channel * ((col + 1) * width + col + 1);//前一个像素点的位置
                        b1 = p[index1] & 0xff;
                        g1 = p[index1 + 1] & 0xff;
                        r1 = p[index1 + 2] & 0xff;

                        int index2 = channel * ((row - 1) * width + col - 1);//后一个像素点的位置
                        Log.w("wqs", "index2: " + index2 + "\nchannel:" + channel + "\nrow:" + row + "\nwidth:" + width + "\ncol:" + col);
                        b2 = p[index2] & 0xff;
                        g2 = p[index2 + 1] & 0xff;
                        r2 = p[index2 + 2] & 0xff;

                        int AB = b1 - b2 + 128;
                        int AG = g1 - g2 + 128;
                        int AR = r1 - r2 + 128;

                        AR = (AR > 255 ? 255 : (AR < 0 ? 0 : AR));
                        AG = (AG > 255 ? 255 : (AG < 0 ? 0 : AG));
                        AB = (AB > 255 ? 255 : (AB < 0 ? 0 : AB));

                        int index = channel * (row * width + col);
                        pDst[index] = (byte) AB;
                        pDst[index + 1] = (byte) AG;
                        pDst[index + 2] = (byte) AR;
                    }
                }
                matDst.put(0, 0, pDst);//全部写入

                dstBit = Bitmap.createBitmap(mat1.width(), mat1.height(), Bitmap.Config.ARGB_8888);
                Utils.matToBitmap(matDst, dstBit);
                image2.setImageBitmap(dstBit);
                mat1.release();
                matDst.release();
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}