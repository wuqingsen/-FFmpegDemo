package com.wuqingsen.opencvall;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * OpenCV重要的四个类
 * Mat(矩阵)：主要定义Mat对象；bitmap位图早OpenCV都要转换成mat
 * Core：对Mat进行计算
 * ImgProc:对图像的处理
 * Utils：Mat和Bitmap之间的转化
 * <p>
 * 官网说明文档：https://docs.opencv.org/java/3.0.0/
 */
public class TwoActivity extends AppCompatActivity {

    Button button1, button2, button3, button4;
    ImageView image;
    TextView text;

    Mat srcmat, dstmat, hsvmat;
    Bitmap resultBitmap;

    List<MatOfPoint> contours = new ArrayList<>();//轮廓列表
    int contoursCounts;//有多少个轮廓

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_two);

        button1 = findViewById(R.id.button1);
        image = findViewById(R.id.image);
        button2 = findViewById(R.id.button2);
        button3 = findViewById(R.id.button3);
        button4 = findViewById(R.id.button4);
        text = findViewById(R.id.text);

        srcmat = new Mat();

        try {
            srcmat = Utils.loadResource(this, R.drawable.icon_two);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //切割
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //图片切割
                Rect rect = new Rect(1300, 600, 2300, 1200);
                dstmat = new Mat(srcmat, rect);
                resultBitmap = Bitmap.createBitmap(dstmat.width(), dstmat.height(), Bitmap.Config.ARGB_8888);
                Imgproc.cvtColor(dstmat, dstmat, Imgproc.COLOR_BGR2RGB);
                Utils.matToBitmap(dstmat, resultBitmap);
                image.setImageBitmap(resultBitmap);

            }
        });

        //颜色识别
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //识别红色
                hsvmat = new Mat();
                //RGB到HSV转换
                Imgproc.cvtColor(dstmat, hsvmat, Imgproc.COLOR_RGB2HSV);
                //scalar取红色的值(橘黄色0-22，黄色22-38，绿色38-75，蓝色75-130，紫色130-160，红色160-179)；
                //后两个参数饱和度与亮度
                Core.inRange(hsvmat, new Scalar(160, 90, 90), new Scalar(179, 255, 255), hsvmat);
                Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(3, 3));
                Imgproc.morphologyEx(hsvmat, hsvmat, Imgproc.MORPH_OPEN, kernel);//开运算
                Imgproc.morphologyEx(hsvmat, hsvmat, Imgproc.MORPH_CLOSE, kernel);//闭运算
                Utils.matToBitmap(hsvmat, resultBitmap);
                image.setImageBitmap(resultBitmap);
            }
        });

        //轮廓识别,轮廓绘制
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //轮廓识别
                Mat outmat = new Mat();
                Imgproc.findContours(hsvmat, contours, outmat, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
                contoursCounts = contours.size();
                Log.w("wqs", "轮廓数量: " + contoursCounts);
                //轮廓绘制
                Imgproc.drawContours(dstmat, contours, -1, new Scalar(0, 0, 255), 4);
                Utils.matToBitmap(dstmat, resultBitmap);
                image.setImageBitmap(resultBitmap);
            }
        });

        //形状识别
        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MatOfPoint2f contour2f;
                MatOfPoint2f approxCurve;
                double epsilon;
                int tri, rect, circle;
                tri = rect = circle = 0;
                for (int i = 0; i < contoursCounts; i++) {
                    contour2f = new MatOfPoint2f(contours.get(i).toArray());//得到第几个轮廓
                    epsilon = 0.04 * Imgproc.arcLength(contour2f, true);
                    approxCurve = new MatOfPoint2f();//多边形拟合顶点集合
                    Imgproc.approxPolyDP(contour2f, approxCurve, epsilon, true);
                    if (approxCurve.rows() == 3) {
                        tri++;
                    }
                    if (approxCurve.rows() == 4) {
                        rect++;
                    }
                    if (approxCurve.rows() > 4) {
                        circle++;
                    }
                    text.setText("轮廓数量：" + contoursCounts + "\n三角形：" + tri + "\n矩形:" + rect + "\n圆形:" + circle);
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        srcmat.release();
        if (dstmat!=null){
            dstmat.release();
        }
        if (hsvmat != null) {
            hsvmat.release();
        }
    }
}