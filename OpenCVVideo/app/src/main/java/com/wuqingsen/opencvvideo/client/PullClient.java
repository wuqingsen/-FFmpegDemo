package com.wuqingsen.opencvvideo.client;

import android.graphics.Bitmap;
import android.os.Environment;

import org.bytedeco.javacv.*;

import javax.swing.*;

import java.io.File;
import java.nio.Buffer;

public class PullClient {

    public static FFmpegFrameRecorder recorder;

    /**
     * 按帧录制视频
     *
     * @param inputFile-该地址可以是网络直播/录播地址，也可以是远程/本地文件路径
     * @param outputFile                              -该地址只能是文件地址，如果使用该方法推送流媒体服务器会报错，原因是没有设置编码格式
     * @throws FrameGrabber.Exception
     * @throws FrameRecorder.Exception
     */
    public static void frameRecord(String inputFile, String outputFile, int audioChannel)
            throws FrameGrabber.Exception, FrameRecorder.Exception {

        boolean isStart = true;//该变量建议设置为全局控制变量，用于控制录制结束
        // 获取视频源
        FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(inputFile);
        // 流媒体输出地址，分辨率（长，高），是否录制音频（0:不录制/1:录制）
        // FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(outputFile, 1280, 720, audioChannel);
        // 开始取视频源
        recordByFrame(grabber, /*recorder, */ isStart);
    }

    private static void recordByFrame(FFmpegFrameGrabber grabber, /* FFmpegFrameRecorder recorder,  */ Boolean status)
            throws FrameGrabber.Exception, FrameRecorder.Exception {
        String filePath = Environment.getExternalStorageDirectory() + "/" + "cameraWuDemo.mp4";
        //初始化
        FrameGrabber videoGrabber = new FFmpegFrameGrabber(filePath);
        videoGrabber.start();
//获取视频源的参数
        double frameRate = videoGrabber.getFrameRate();
        int sampleRate = videoGrabber.getSampleRate();

        Frame frame;
        int count = 0;
        String ffmpeg_link = Environment.getExternalStorageDirectory()
                .getAbsolutePath() + "/" + "obama.avi";
        File result = new File(ffmpeg_link);
        if (result.exists()) result.delete();
        if (recorder == null) {
            //初始化recorder
            recorder = new FFmpegFrameRecorder(result,
                    videoGrabber.getImageWidth(), videoGrabber.getImageHeight(), videoGrabber.getAudioChannels());
            recorder.setFormat("avi");
            recorder.setSampleRate(sampleRate);
            // Set in the surface changed method
            recorder.setFrameRate(frameRate);
            recorder.start();
        }

        while (true) {
            //获取下一帧数据
            frame = videoGrabber.grabFrame();
            if (frame == null) break;
            if (frame.image == null) continue;

            //转码  Frame ->Bitmap  操作Bitmap完成后Bitmap->Frame
            AndroidFrameConverter bitmapConverter = new AndroidFrameConverter();
            Bitmap currentImage = bitmapConverter.convert(frame);
            currentImage = initScence(currentImage);//操作Bitmap，
            Frame frame1 = bitmapConverter.convert(currentImage);
            //写入recorder
            recorder.record(frame1);
        }


        //windows
//        CanvasFrame frame = new CanvasFrame("show stream", CanvasFrame.getDefaultGamma() / grabber.getGamma());
//        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        frame.setAlwaysOnTop(true);


//        try {//建议在线程中使用该方法
//            grabber.start();
//            // recorder.start();
//            Frame rotatedFrame = null;
//            while (status&& (rotatedFrame = grabber.grabFrame()) != null) {
//                // recorder.record(rotatedFrame);
////                frame.showImage(rotatedFrame);
//
//                Buffer[] image = rotatedFrame.image;
//
//
//
//            }
//            // recorder.stop();
//            grabber.stop();
//        } finally {
//            if (grabber != null) {
//                grabber.stop();
//            }
//        }
    }


    public static void main(String[] args)
            throws Exception {

        // String inputFile = "rtsp://admin:admin@192.168.2.236:37779/cam/realmonitor?channel=1&subtype=0";
        String inputFile = "rtmp://192.168.241.19/live/record1";
        // Decodes-encodes
        String outputFile = "recorde.mp4";
        frameRecord(inputFile, outputFile, 0);
    }


}
