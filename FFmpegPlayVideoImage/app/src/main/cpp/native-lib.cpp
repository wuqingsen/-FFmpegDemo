#include <jni.h>
#include <string>
#include <android/native_window_jni.h>
#include <unistd.h>

extern "C" {
#include "libavcodec/avcodec.h"
#include "libavformat/avformat.h"
#include <libswscale/swscale.h>
#include <libavutil/imgutils.h>
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_wuqingsen_ffmpegwudemo_MainActivity_stringFromJNI(
        JNIEnv *env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(av_version_info());
}

//播放视频流
//大概步骤：1.拿到总上下文。2.遍历流。3.解码器上下文。4.获取到解码器。
// 5.流中读取到packet。6.packet转换为frame。7.转换为可显示的格式。8.输出到相应设备
extern "C"
JNIEXPORT void JNICALL
Java_com_wuqingsen_ffmpegwudemo_WangyiPlayer_native_1start(JNIEnv *env, jobject thiz,
                                                           jstring path_, jobject surface) {
    ANativeWindow *nativeWindow = ANativeWindow_fromSurface(env, surface);
    ANativeWindow_Buffer outBuffer;
    const char *path = env->GetStringUTFChars(path_, 0);
    //ffmpeg视频绘制
    //1.初始化网络
    avformat_network_init();
    //获取总上下文，并分配内存
    AVFormatContext *formatContext = avformat_alloc_context();
    //设置超时时间
    AVDictionary *opts = NULL;
    av_dict_set(&opts, "timeout", "3000000", 0);
    int ret = avformat_open_input(&formatContext, path, NULL, &opts);
    if (ret) {
        return;
    }
    //视频流
    int vidio_stream_idx = -1;
    avformat_find_stream_info(formatContext, NULL);//寻找视频流
    for (int i = 0; i < formatContext->nb_streams; ++i) {
        //类型为视频流
        if (formatContext->streams[i]->codecpar->codec_type == AVMEDIA_TYPE_VIDEO) {
            vidio_stream_idx = i;
        }
    }
    //视频流索引，获取到视频流的详细参数
    AVCodecParameters *codecpar = formatContext->streams[vidio_stream_idx]->codecpar;

    //解码器 h264
    AVCodec *dec = avcodec_find_decoder(codecpar->codec_id);//找到视频流的解码器
    //解码器上下文
    AVCodecContext *codecContext = avcodec_alloc_context3(dec);
    //将加码器参数复制到解码器上下文
    avcodec_parameters_to_context(codecContext, codecpar);

    avcodec_open2(codecContext, dec, NULL);
    //解码yuv数据
    AVPacket *packet = av_packet_alloc();
    //在视频流中读取数据包,>=0是正确的
    //yuv转rgb，前三个输入源，中间三个是输出源，AVPixelFormat是格式，yuv转rgb
    SwsContext *swsContext = sws_getContext(codecContext->width, codecContext->height,
                                            codecContext->pix_fmt,
                                            codecContext->width, codecContext->height,
                                            AV_PIX_FMT_RGBA,
                                            SWS_FAST_BILINEAR, 0, 0, 0);
    //设置window缓冲区大小
    ANativeWindow_setBuffersGeometry(nativeWindow, codecContext->width, codecContext->height,
                                     WINDOW_FORMAT_RGBA_8888);
    while (av_read_frame(formatContext, packet) >= 0) {
        avcodec_send_packet(codecContext, packet);
        AVFrame *frame = av_frame_alloc();
        ret = avcodec_receive_frame(codecContext, frame);//第二个参数frame即一个yuv数据
        if (ret == AVERROR(EAGAIN)) {
            //输出不可用，再重新取一次数据包
            continue;
        } else if (ret < 0) {
            //读取完毕
            break;
        }
        //接收的容器
        uint8_t *dst_data[4];
        //每一行的首地址
        int dst_linesize[4];
        //确定容器的大小
        av_image_alloc(dst_data, dst_linesize, codecContext->width, codecContext->height,
                       AV_PIX_FMT_RGBA, 1);
        //绘制
        sws_scale(swsContext, frame->data, frame->linesize, 0, frame->height,
                  dst_data, dst_linesize);

        //对window缓冲区复制，先锁定缓冲区
        ANativeWindow_lock(nativeWindow, &outBuffer, NULL);
        uint8_t *firstWindown = static_cast<uint8_t *>(outBuffer.bits);
        uint8_t *src_data = dst_data[0];
        int destStride = outBuffer.stride * 4;
        int src_linesize = dst_linesize[0];
        //对缓冲区渲染(对内存进行复制)
        for (int i = 0; i < outBuffer.height; ++i) {
            //通过内存拷贝进行渲染
            memcpy(firstWindown + i * destStride, src_data + i * src_linesize, destStride);
        }
        //解锁缓冲区
        ANativeWindow_unlockAndPost(nativeWindow);
        usleep(1000 * 16);
        av_frame_free(&frame);
    }

    env->ReleaseStringUTFChars(path_, path);
}