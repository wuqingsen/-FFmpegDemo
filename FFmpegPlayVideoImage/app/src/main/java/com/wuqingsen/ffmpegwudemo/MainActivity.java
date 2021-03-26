package com.wuqingsen.ffmpegwudemo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Environment;
import android.view.SurfaceView;
import android.view.View;
import android.widget.TextView;

import java.io.File;

public class MainActivity extends AppCompatActivity {


    SurfaceView surfaceView;
    WangyiPlayer wangyiPlayer;
    private String filePath = Environment.getExternalStorageDirectory() + "/" + "wqs.mp4";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        surfaceView = findViewById(R.id.surfaceView);
        wangyiPlayer = new WangyiPlayer();

        wangyiPlayer.setSurfaceView(surfaceView);

    }

    public void open(View view) {
        wangyiPlayer.start(filePath);
    }

}