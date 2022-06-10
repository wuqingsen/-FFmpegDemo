package com.wuqingsen.openglall.chapter3;

import android.opengl.GLSurfaceView;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class Chapter3Activity extends AppCompatActivity {

    private GLSurfaceView mGlSurfaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //实例化
        mGlSurfaceView = new GLSurfaceView(this);

        //设置渲染器
        mGlSurfaceView.setRenderer(new GLRender3());
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
}