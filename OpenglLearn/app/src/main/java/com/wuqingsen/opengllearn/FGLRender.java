package com.wuqingsen.opengllearn;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.view.View;

import com.wuqingsen.opengllearn.shape.Triangle;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * wuqingsen on 2021/4/7
 * Mailbox:807926618@qq.com
 * annotation:
 */
public class FGLRender implements GLSurfaceView.Renderer {
    protected View mView;
    Triangle triangle;

    public FGLRender(FGLView fglView) {
        this.mView = fglView;
    }

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        GLES20.glClearColor(0, 0, 0, 0);//清空画布
        triangle = new Triangle();
    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int i, int i1) {

    }

    //不断被调用
    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);//清空
        triangle.onDrawFrame(gl);
    }
}
