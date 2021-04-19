package com.wuqingsen.opencvface;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.hardware.Camera;
import android.media.FaceDetector;
import android.os.Bundle;
import android.os.Environment;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.TextView;

import com.wuqingsen.opencvface.util.CameraHelper;
import com.wuqingsen.opencvface.util.Utils;

import java.io.File;

//wqs
public class MainActivity extends AppCompatActivity implements SurfaceHolder.Callback, Camera.PreviewCallback {
    private OpencvJni openCvJni;
    private CameraHelper cameraHelper;
    int cameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;

    //人脸识别
//    FaceDetector faceDetector =

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        openCvJni = new OpencvJni();
        SurfaceView surfaceView = findViewById(R.id.surfaceView);
        surfaceView.getHolder().addCallback(this);
        cameraHelper = new CameraHelper(cameraId);
        cameraHelper.setPreviewCallback(this);

        Utils.copyAssets(this, "lbpcascade_frontalface.xml");
    }

    @Override
    protected void onResume() {
        super.onResume();
        String path = new File(Environment.getExternalStorageDirectory(), "lbpcascade_frontalface.xml").getAbsolutePath();
        cameraHelper.startPreview();
        openCvJni.init(path);
    }

    public void switchCamera(View view) {

    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int i, int i1, int i2) {
        openCvJni.setSurface(holder.getSurface());

    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder surfaceHolder) {

    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        openCvJni.postData(data, CameraHelper.WIDTH, CameraHelper.HEIGHT, cameraId);
    }
}