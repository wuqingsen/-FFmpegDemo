package com.wuqingsen.opencvall;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import org.opencv.android.Utils;
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
public class OneActivity extends AppCompatActivity {

    ImageView image1, image2, image3, image4, image5;
    Button button;
    private Mat mat1, mat2, mat3;
    private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one);

        image1 = findViewById(R.id.image1);
        image2 = findViewById(R.id.image2);
        image3 = findViewById(R.id.image3);
        image4 = findViewById(R.id.image4);
        image5 = findViewById(R.id.image5);
        button = findViewById(R.id.button);

        mat1 = new Mat();
        mat2 = new Mat();
        mat3 = new Mat();

        try {
            mat1 = Utils.loadResource(this, R.drawable.icon_one);
//            mat2 = Utils.loadResource(this,R.drawable.icon_lenna1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Core.bitwise_and(mat1,mat2,mat3);
                //黑白照片
                Imgproc.cvtColor(mat1, mat3, Imgproc.COLOR_BGRA2GRAY);
                bitmap = Bitmap.createBitmap(mat3.width(), mat3.height(), Bitmap.Config.ARGB_8888);
                Utils.matToBitmap(mat3, bitmap);
                image2.setImageBitmap(bitmap);

                //二值化（手动）
                Imgproc.threshold(mat3, mat3, 125, 255, Imgproc.THRESH_BINARY);
                bitmap = Bitmap.createBitmap(mat3.width(), mat3.height(), Bitmap.Config.ARGB_8888);
                Utils.matToBitmap(mat3, bitmap);
                image3.setImageBitmap(bitmap);

                //二值化（自动）
                Imgproc.adaptiveThreshold(mat3, mat3, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C
                        , Imgproc.THRESH_BINARY, 13, 5);
                bitmap = Bitmap.createBitmap(mat3.width(), mat3.height(), Bitmap.Config.ARGB_8888);
                Utils.matToBitmap(mat3, bitmap);
                image4.setImageBitmap(bitmap);

                //绘制线
                Imgproc.line(mat1, new Point(0, mat1.height()), new Point(mat1.width(), 0),
                        new Scalar(255, 0, 0), 4);
                //文字
                Imgproc.putText(mat1, "The cat", new Point(mat1.height() / 2, mat1.width() / 3),
                        2, 1, new Scalar(0, 255, 0), 1);
                Imgproc.line(mat1, new Point(0, mat1.height()), new Point(mat1.width(), 0),
                        new Scalar(255, 0, 0), 4);

                bitmap = Bitmap.createBitmap(mat1.width(), mat1.height(), Bitmap.Config.ARGB_8888);
                Utils.matToBitmap(mat1, bitmap);
                image5.setImageBitmap(bitmap);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mat1.release();
        mat2.release();
        mat3.release();
    }

    private static String path1 = Environment.getExternalStorageDirectory().getAbsolutePath() + "/cameraWuDemo.png";

    public static void savePNG_After(Bitmap bitmap) {
        File file = new File(path1);
        try {
            FileOutputStream out = new FileOutputStream(file);
            if (bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)) {
                out.flush();
                out.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}