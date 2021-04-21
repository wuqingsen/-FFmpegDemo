package com.wuqingsen.bigeye.util;

import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;

/**
 * wuqingsen on 2021/4/12
 * Mailbox:807926618@qq.com
 * annotation:
 */
public class CameraHelper implements Camera.PreviewCallback {
    private static final String TAG = "wqs-CameraHelper";
    public static final int WIDTH = 640;
    public static final int HEIGHT = 480;
    private int mCameraId;
    private Camera mCamera;
    private byte[] buffer;
    private Camera.PreviewCallback mPreviewCallback;
    private SurfaceTexture mSurfaceTexture;

    public CameraHelper(int mCameraId) {
        this.mCameraId = mCameraId;
    }

    public void switchCamera() {
        if (mCameraId == Camera.CameraInfo.CAMERA_FACING_BACK) {
            mCameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;
        } else {
            mCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
        }
        stopPreview();
        startPreview(mSurfaceTexture);
    }

    public int getCameraId() {
        return mCameraId;
    }

    public void stopPreview() {
        if (mCamera != null) {
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }

    public void startPreview(SurfaceTexture surfaceTexture) {
        mSurfaceTexture = surfaceTexture;
        try {
            mCamera = Camera.open(mCameraId);
            Camera.Parameters parameters = mCamera.getParameters();
            parameters.setPreviewFormat(ImageFormat.NV21);
            parameters.setPreviewSize(WIDTH, HEIGHT);
            mCamera.setParameters(parameters);
            buffer = new byte[WIDTH * HEIGHT * 3 / 2];
            //数据缓存区
            mCamera.addCallbackBuffer(buffer);
            mCamera.setPreviewCallback(this);
            //设置预览画面
            mCamera.setPreviewTexture(mSurfaceTexture);
            mCamera.startPreview();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void setPreviewCallback(Camera.PreviewCallback previewCallback) {
        mPreviewCallback = previewCallback;
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        //data数据是倒置的
        if (null != mPreviewCallback) {
            mPreviewCallback.onPreviewFrame(data, camera);
        }
        camera.addCallbackBuffer(buffer);
    }
}
