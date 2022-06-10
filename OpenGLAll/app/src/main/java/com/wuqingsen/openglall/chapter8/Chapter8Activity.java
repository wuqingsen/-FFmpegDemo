package com.wuqingsen.openglall.chapter8;

import android.opengl.GLSurfaceView;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.wuqingsen.openglall.chapter7.GLRender7;

public class Chapter8Activity extends AppCompatActivity {

    private GLSurfaceView mGlSurfaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //实例化
        mGlSurfaceView = new GLSurfaceView(this);

        //设置渲染器
        mGlSurfaceView.setRenderer(new GLRender8(this));
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