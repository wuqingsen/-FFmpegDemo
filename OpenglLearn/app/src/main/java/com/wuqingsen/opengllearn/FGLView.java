package com.wuqingsen.opengllearn;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

/**
 * wuqingsen on 2021/4/7
 * Mailbox:807926618@qq.com
 * annotation:
 */
public class FGLView extends GLSurfaceView {
    public FGLView(Context context) {
        super(context);
    }

    //三角形，正方形，立方体
    public FGLView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setEGLContextClientVersion(2);
        setRenderer(new FGLRender(this));
        //设置渲染模式
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }
}
