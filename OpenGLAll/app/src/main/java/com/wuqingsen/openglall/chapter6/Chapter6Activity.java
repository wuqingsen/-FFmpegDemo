package com.wuqingsen.openglall.chapter6;

import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.KeyEvent;

import androidx.appcompat.app.AppCompatActivity;

import com.wuqingsen.openglall.chapter5.GLRender5;

public class Chapter6Activity extends AppCompatActivity {

    private GLSurfaceView mGlSurfaceView;
    private GLRender6 mGLRender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //实例化
        mGlSurfaceView = new GLSurfaceView(this);

        //设置渲染器
        mGLRender = new GLRender6(this);
        mGlSurfaceView.setRenderer(mGLRender);
        setContentView(mGlSurfaceView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGlSurfaceView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mGlSurfaceView.onPause();
    }

//    public boolean onKeyUp(int keyCode, KeyEvent event)
//    {
//        mGLRender.onKeyUp(keyCode, event);
//        return false;
//    }
}