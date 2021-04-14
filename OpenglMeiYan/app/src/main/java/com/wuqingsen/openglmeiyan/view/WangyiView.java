package com.wuqingsen.openglmeiyan.view;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

/**
 * wuqingsen on 2021/4/12
 * Mailbox:807926618@qq.com
 * annotation:
 */
public class WangyiView extends GLSurfaceView {
    public WangyiView(Context context){
        super(context);
    }
    public WangyiView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setEGLContextClientVersion(2);
        setRenderer(new WangyiRender(this));
        //设置渲染模式,自动调用draw
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }
}
