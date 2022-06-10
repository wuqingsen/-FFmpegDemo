package com.wuqingsen.myapplication;

import android.content.Context;
import android.opengl.GLSurfaceView;

/**
 * wuqingsen on 8/10/21
 * Mailbox:807926618@qq.com
 * annotation:
 */
class MyGLSurfaceView extends GLSurfaceView {

    private final MyGLRenderer mRenderer;

    public MyGLSurfaceView(Context context){
        super(context);

        // Create an OpenGL ES 2.0 context
        setEGLContextClientVersion(2);

        mRenderer = new MyGLRenderer();

        // Set the Renderer for drawing on the GLSurfaceView
        setRenderer(mRenderer);
    }
}
