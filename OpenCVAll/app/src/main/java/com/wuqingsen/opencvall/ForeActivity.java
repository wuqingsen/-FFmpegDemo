package com.wuqingsen.opencvall;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
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
public class ForeActivity extends AppCompatActivity {
    CascadeClassifier classifier;
    ImageView image1, image2;
    Button button;
    Bitmap src_photo, dst_photo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fore);

        image1 = findViewById(R.id.image1);
        image2 = findViewById(R.id.image2);
        button = findViewById(R.id.button);

        initClassifier();
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                src_photo = ((BitmapDrawable) ((ImageView) image1).getDrawable()).getBitmap();
                dst_photo = faceDetect(src_photo);
                image2.setImageBitmap(dst_photo);
            }
        });
    }

    private void initClassifier() {
        try {
            InputStream is = getResources().openRawResource(R.raw.lbpcascade_frontalface_improved);
            File cascadeDir = getDir("cascade", Context.MODE_PRIVATE);
            File cascadeFile = new File(cascadeDir, "lbpcascade_frontalface_improved.xml");
            FileOutputStream os = new FileOutputStream(cascadeFile);
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            is.close();
            os.close();
            classifier = new CascadeClassifier(cascadeFile.getAbsolutePath());
            cascadeFile.delete();
            cascadeDir.delete();

        } catch (FileNotFoundException fileNotFoundException) {
            fileNotFoundException.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Bitmap faceDetect(Bitmap photo) {
        Mat matSrc = new Mat();
        Mat matDst = new Mat();
        Mat matGray = new Mat();//灰度图矩阵

        Utils.bitmapToMat(photo, matSrc);
        Imgproc.cvtColor(matSrc, matGray, Imgproc.COLOR_BGRA2GRAY);
        MatOfRect faces = new MatOfRect();
        //进行人脸检测
        classifier.detectMultiScale(matGray, faces, 1.05,
                3, 0, new Size(30, 30), new Size());
        List<Rect> faceList = faces.toList();
        matSrc.copyTo(matDst);
        if (faceList.size() > 0) {
            for (Rect rect : faceList) {
                //绘制矩形
                Imgproc.rectangle(matDst, rect.tl(), rect.br(),
                        new Scalar(255, 0, 0, 255), 4,8,0);
            }
        }

        Bitmap dstBit = Bitmap.createBitmap(photo.getWidth(), photo.getHeight(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(matDst, dstBit);
        matSrc.release();
        matDst.release();
        matGray.release();
        return dstBit;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}