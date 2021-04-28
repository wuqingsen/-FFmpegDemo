package com.wuqingsen.opencvall;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
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
public class MainActivity extends AppCompatActivity {

    Button button1, button2, button3,button4,button5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initOpenCV();
        button1 = findViewById(R.id.button1);
        button2 = findViewById(R.id.button2);
        button3 = findViewById(R.id.button3);
        button4 = findViewById(R.id.button4);
        button5 = findViewById(R.id.button5);

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, OneActivity.class));
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, TwoActivity.class));
            }
        });
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, ThreeActivity.class));
            }
        });
        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, ForeActivity.class));
            }
        });
        button5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, FiveActivity.class));
            }
        });
    }

    private void initOpenCV() {
        boolean isSuccess = OpenCVLoader.initDebug();
        if (isSuccess) {
            Log.w("wqs", "OpenCVLoader成功");
        } else {
            Log.w("wqs", "OpenCVLoader失败");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}