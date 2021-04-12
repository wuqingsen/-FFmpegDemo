package com.wuqingsen.openglmeiyan.view;

import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.view.View;

import com.wuqingsen.openglmeiyan.filter.CameraFilter;
import com.wuqingsen.openglmeiyan.filter.ScreenFilter;
import com.wuqingsen.openglmeiyan.util.CameraHelper;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * wuqingsen on 2021/4/12
 * Mailbox:807926618@qq.com
 * annotation:渲染器
 * 主要使用责任链模式
 */
class WangyiRender implements GLSurfaceView.Renderer, SurfaceTexture.OnFrameAvailableListener {
    private CameraHelper mCameraHelper;
    private WangyiView mView;
    private SurfaceTexture mSurfaceTexture;
    private int[] mTextures;
    private float[] mtx = new float[16];

    CameraFilter mCameraFilter;
    ScreenFilter mScreenFilter;

    public WangyiRender(WangyiView wangyiView) {
        mView = wangyiView;
    }

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        //打开摄像头
        mCameraHelper = new CameraHelper(Camera.CameraInfo.CAMERA_FACING_BACK);
        //纹理==数据的输入，指的是GPU中的某个内存
        mTextures = new int[1];
        //设置纹理id,参数i1：为数组中第几个元素赋值
        GLES20.glGenTextures(mTextures.length, mTextures, 0);
        mSurfaceTexture = new SurfaceTexture(mTextures[0]);
        mSurfaceTexture.setOnFrameAvailableListener(this);
        //获取当前摄像头的矩阵,摄像头数据不会变形
        mSurfaceTexture.getTransformMatrix(mtx);
        mCameraFilter = new CameraFilter(mView.getContext());
        mScreenFilter = new ScreenFilter(mView.getContext());
        mCameraFilter.setMatrix(mtx);
    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int width, int height) {
        mCameraHelper.startPreview(mSurfaceTexture);
        mCameraFilter.onReady(width, height);
        mScreenFilter.onReady(width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl10) {
        //获取到一帧数据
        GLES20.glClearColor(0, 0, 0, 0);
        //执行清空
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        //摄像头数据拿出
        mSurfaceTexture.updateTexImage();
        mSurfaceTexture.getTransformMatrix(mtx);
        mCameraFilter.setMatrix(mtx);
        //第一个处理者
        int id = mCameraFilter.onDrawFrame(mTextures[0]);
        //第二个处理者
        mScreenFilter.onDrawFrame(id);
    }

    @Override
    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
        mView.requestRender();
    }
}
