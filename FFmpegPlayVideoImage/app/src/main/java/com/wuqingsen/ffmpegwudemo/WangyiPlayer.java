package com.wuqingsen.ffmpegwudemo;

import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;

/**
 * wuqingsen on 2021/3/25
 * Mailbox:807926618@qq.com
 * annotation:
 */
public class WangyiPlayer implements SurfaceHolder.Callback{

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("wangyiplayer");
    }

    private SurfaceHolder surfaceHolder;

    public void setSurfaceView(SurfaceView surfaceView){
        if (null != this.surfaceHolder){
            this.surfaceHolder.removeCallback(this);
        }
        this.surfaceHolder = surfaceView.getHolder();
        this.surfaceHolder.addCallback(this);
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder surfaceHolder) {

    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        this.surfaceHolder = surfaceHolder;
    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder surfaceHolder) {

    }

    //开始播放
    public void start(String filePath) {
        native_start(filePath, surfaceHolder.getSurface());
    }

    public native void native_start(String filePath, Surface surface);
}
