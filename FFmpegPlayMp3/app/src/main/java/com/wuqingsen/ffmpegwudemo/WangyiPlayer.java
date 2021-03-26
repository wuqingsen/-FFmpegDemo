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
public class WangyiPlayer{

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("wangyiplayer");
    }

    public native void sound(String input,String output);
}
