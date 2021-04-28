package com.wuqingsen.opencvall;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
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

/**
 * OpenCV重要的四个类
 * Mat(矩阵)：主要定义Mat对象；bitmap位图早OpenCV都要转换成mat
 * Core：对Mat进行计算
 * ImgProc:对图像的处理
 * Utils：Mat和Bitmap之间的转化
 * <p>
 * 官网说明文档：https://docs.opencv.org/java/3.0.0/
 */
public class FiveActivity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2 {
    private JavaCameraView cameraView;
    CascadeClassifier classifier, classifiereye;
    Button switchCamera;
    boolean isFrontCamera;
    Mat mRgba;
    int mAbsoluteFaceSize = 0;
    Scalar EYE_RECT_COLOR = new Scalar(0, 0, 255);
    Mat leftEye_template, rightEye_template;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initWindowSetting();
        setContentView(R.layout.activity_five);

        cameraView = findViewById(R.id.cameraView);
        cameraView.setCvCameraViewListener(this);
        switchCamera = findViewById(R.id.switchCamera);

        //默认打开前置
        isFrontCamera = true;
        cameraView.setCameraPermissionGranted();
        cameraView.setCameraIndex(CameraBridgeViewBase.CAMERA_ID_FRONT);
        cameraView.enableView();

        initClassifier();
        initClassifierEye();
        leftEye_template = new Mat();
        rightEye_template = new Mat();

        switchCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isFrontCamera) {
                    cameraView.setCameraIndex(CameraBridgeViewBase.CAMERA_ID_BACK);
                    isFrontCamera = false;
                } else {
                    cameraView.setCameraIndex(CameraBridgeViewBase.CAMERA_ID_FRONT);
                    isFrontCamera = true;
                }
                if (cameraView != null) {
                    cameraView.disableView();
                }
                cameraView.enableView();
            }
        });

    }

    private void initWindowSetting() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onCameraViewStarted(int width, int height) {
        mRgba = new Mat();
    }

    @Override
    public void onCameraViewStopped() {
        mRgba.release();
    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        mRgba = inputFrame.rgba();
        float mRelativeFaceSize = 0.2f;
        if (mAbsoluteFaceSize == 0) {
            int height = mRgba.rows();
            if (Math.round(height * mRelativeFaceSize) > 0) {
                mAbsoluteFaceSize = Math.round(height * mRelativeFaceSize);
            }
        }
        MatOfRect faces = new MatOfRect();
        if (classifier != null) {
            classifier.detectMultiScale(mRgba, faces, 1.1,
                    2, 2, new Size(mAbsoluteFaceSize, mAbsoluteFaceSize), new Size());
        }
        Rect[] faceArray = faces.toArray();
        Scalar faceRectColor = new Scalar(0, 255, 0, 255);
        for (int i = 0; i < faceArray.length; i++) {
            Imgproc.rectangle(mRgba, faceArray[i].tl(), faceArray[i].br(),
                    faceRectColor, 2);
            selectEyesArea(faceArray[i], mRgba);

        }

        return mRgba;
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

    private void initClassifierEye() {
        try {
            InputStream is = getResources().openRawResource(R.raw.haarcascade_eye_tree_eyeglasses);
            File cascadeDir = getDir("cascade", Context.MODE_PRIVATE);
            File cascadeFile = new File(cascadeDir, "haarcascade_eye_tree_eyeglasses.xml");
            FileOutputStream os = new FileOutputStream(cascadeFile);
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            is.close();
            os.close();
            classifiereye = new CascadeClassifier(cascadeFile.getAbsolutePath());
            cascadeFile.delete();
            cascadeDir.delete();

        } catch (FileNotFoundException fileNotFoundException) {
            fileNotFoundException.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //选定眼睛区域
    private void selectEyesArea(Rect faceROI, Mat frame) {
        int offy = (int) (faceROI.height * 0.15f);
        int offx = (int) (faceROI.width * 0.12f);
        int sh = (int) (faceROI.height * 0.35f);
        int sw = (int) (faceROI.width * 0.3f);
        int gap = (int) (faceROI.width * 0.025f);
        Point lp_eye = new Point(faceROI.tl().x + offx, faceROI.tl().y + offy);
        Point lp_end = new Point(lp_eye.x + sw, lp_eye.y + sh);
        Imgproc.rectangle(frame, lp_eye, lp_end, EYE_RECT_COLOR, 2);

        int right_offx = (int) (faceROI.width * 0.08f);

        Point rp_eye = new Point(faceROI.x + faceROI.width / 2 + right_offx, faceROI.tl().y + offy);
        Point rp_end = new Point(rp_eye.x + sw, rp_eye.y + sh);
        Imgproc.rectangle(frame, rp_eye, rp_end, EYE_RECT_COLOR, 2);

        //检测眼睛
        MatOfRect eyes = new MatOfRect();

        Rect left_eye_roi = new Rect();
        left_eye_roi.x = (int) lp_eye.x;
        left_eye_roi.y = (int) lp_eye.y;
        left_eye_roi.width = (int) (lp_end.x - lp_eye.x);
        left_eye_roi.height = (int) (lp_end.y - lp_eye.y);

        Rect right_eye_roi = new Rect();
        right_eye_roi.x = (int) rp_eye.x;
        right_eye_roi.y = (int) rp_eye.y;
        right_eye_roi.width = (int) (rp_end.x - rp_eye.x);
        right_eye_roi.height = (int) (rp_end.y - rp_eye.y);

        Mat leftEye = frame.submat(left_eye_roi);
        Mat rightEye = frame.submat(right_eye_roi);

        //左眼
        classifiereye.detectMultiScale(mRgba.submat(left_eye_roi), eyes, 1.15,
                2, 0, new Size(30, 30), new Size());
        Rect[] eyesArray = eyes.toArray();
        for (int i = 0; i < eyesArray.length; i++) {
            leftEye.submat(eyesArray[i]).copyTo(leftEye_template);
        }
        if (eyesArray.length == 0) {
            Rect left_roi = matchEyeTemplate(leftEye, true);
            if (left_roi != null) {
                Imgproc.rectangle(leftEye, left_roi.tl(), left_roi.br(), EYE_RECT_COLOR, 2);
            }
        }
        eyes.release();

        //右眼
        eyes = new MatOfRect();
        classifiereye.detectMultiScale(mRgba.submat(right_eye_roi), eyes, 1.15,
                2, 0, new Size(30, 30), new Size());
        eyesArray = eyes.toArray();
        for (int i = 0; i < eyesArray.length; i++) {
            rightEye.submat(eyesArray[i]).copyTo(rightEye_template);
            Imgproc.rectangle(rightEye, eyesArray[i].tl(), eyesArray[i].br(), EYE_RECT_COLOR, 2);
        }
        if (eyesArray.length == 0) {
            Rect right_roi = matchEyeTemplate(rightEye, false);
            if (right_roi != null) {
                Imgproc.rectangle(rightEye, right_roi.tl(), right_roi.br(), EYE_RECT_COLOR, 2);
            }
        }

    }


    //眼睛模板匹配
    private Rect matchEyeTemplate(Mat src, boolean left) {
        Mat tpl = left ? leftEye_template : rightEye_template;
        if (tpl.cols() == 0 || tpl.rows() == 0) {
            return null;
        }

        int height = src.rows() - tpl.rows() + 1;
        int width = src.cols() - tpl.cols() + 1;
        if (height < 1 || width < 1) {
            return null;
        }
        Mat result = new Mat(height, width, CvType.CV_32FC1);

        int method = Imgproc.TM_CCOEFF_NORMED;
        Imgproc.matchTemplate(src, tpl, result, method);
        Core.MinMaxLocResult minMaxResult = Core.minMaxLoc(result);
        Point maxloc = minMaxResult.maxLoc;

        Rect rect = new Rect();
        rect.x = (int) (maxloc.x);
        rect.y = (int) (maxloc.y);
        rect.width = tpl.cols();
        rect.width = tpl.rows();

        result.release();
        return rect;
    }

}