#include <jni.h>
#include <string>
#include <android/native_window_jni.h>
#include <unistd.h>
#include <android/log.h>

#define LOGI(FORMAT, ...) __android_log_print(ANDROID_LOG_INFO,"wqs",FORMAT,##__VA_ARGS__);
#define LOGE(FORMAT, ...) __android_log_print(ANDROID_LOG_INFO,"wqs",FORMAT,##__VA_ARGS__);

#define MAX_AUDIO_FRME_SIZE 48000 * 4;

extern "C" {
#include "libavformat/avformat.h"
#include "libavcodec/avcodec.h"
#include <libswscale/swscale.h>
#include <libswresample/swresample.h>
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_wuqingsen_ffmpegwudemo_MainActivity_stringFromJNI(
        JNIEnv *env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(av_version_info());
}

extern "C"
JNIEXPORT void JNICALL
Java_com_wuqingsen_ffmpegwudemo_WangyiPlayer_sound(JNIEnv *env, jobject thiz,
                                                   jstring input_, jstring output_) {
//    const char *input = env->GetStringUTFChars(input_, 0);
//    const char *output = env->GetStringUTFChars(output_, 0);
//
//    avformat_network_init();//初始化网络
//    AVFormatContext *formatContext = avformat_alloc_context();//总上下文
//    //打开音频文件
//    if (avformat_open_input(&formatContext, input, NULL, NULL) != 0) {
//        LOGI("%s", "无法打开文件");
//        return;
//    }
//    //获取文件信息
//    if (avformat_find_stream_info(formatContext, NULL) < 0) {
//        LOGI("%s", "无法获取文件信息");
//        return;
//    }
//    //获取音频流索引
//    int audio_stream_idx = -1;
//    for (int i = 0; i < formatContext->nb_streams; ++i) {
//        if (formatContext->streams[i]->codecpar->codec_type == AVMEDIA_TYPE_AUDIO) {
//            audio_stream_idx = i;
//            break;
//        }
//    }
//    //获取解码器参数
//    AVCodecParameters *codecpar = formatContext->streams[audio_stream_idx]->codecpar;
//    //找到解码器
//    AVCodec *dec = avcodec_find_decoder(codecpar->codec_id);
//    //创建上下文
//    AVCodecContext *codecContext = avcodec_alloc_context3(dec);
//    //将解码参数赋值到上下文中
//    avcodec_parameters_to_context(codecContext, codecpar);
//
//    //转化器上下文
//    SwrContext *swrContext = swr_alloc();
//
//    //输入的参数
//    AVSampleFormat in_sample = codecContext->sample_fmt;
//    int in_sample_rate = codecContext->sample_rate;//采样率
//    uint64_t in_ch_layout = codecContext->channel_layout;
//    //输出参数
//    AVSampleFormat out_sample = AV_SAMPLE_FMT_S16;//采样格式
//    int out_sample_rate = 44100;//采样率
//    uint64_t out_ch_layout = AV_CH_LAYOUT_STEREO;//声道
//
//    //设置输入输出参数
//    swr_alloc_set_opts(swrContext, out_ch_layout, out_sample, out_sample_rate,
//                       in_ch_layout, in_sample, in_sample_rate, 0, NULL);
//    //初始化转化器默认参数
//    swr_init(swrContext);
//
//    //初始化缓冲区
//    uint8_t *out_buffer = static_cast<uint8_t *>(av_malloc(2 * 44100));//缓冲区大小=通道数*采样率
//    FILE *fp_pcm = fopen(output, "wb");
//
//    //读取数据包(数据包是压缩数据)
//    AVPacket *packet = av_packet_alloc();
//    int count = 0;
//    while (av_read_frame(formatContext, packet) >= 0) {
//        avcodec_send_packet(codecContext, packet);
//
//        //解压数据
//        AVFrame *frame = av_frame_alloc();
//        int ret = avcodec_receive_frame(codecContext, frame);
//        if (ret == AVERROR(EAGAIN)) {
//            continue;
//        } else if (ret < 0) {
//            LOGE("解码完成");
//            break;
//        }
//
//        //解码流是音频流就继续解码，不是就跳过此次循环
//        if (packet->stream_index != audio_stream_idx) {
//            continue;
//        }
//        LOGE("正在解码%d", count++);
//
//        //对解压后的音频转化为同一采样率
//        swr_convert(swrContext, &out_buffer, 2 * 44100,
//                    (const uint8_t **) frame->data, frame->nb_samples);
//
//        //计算输出的声道布局
//        int out_channerl_nb = av_get_channel_layout_nb_channels(out_ch_layout);
//
//        //缓冲区对齐
//        int out_buffer_size = av_samples_get_buffer_size(NULL, out_channerl_nb,
//                frame->nb_samples,out_sample, 1);//获取到这一帧的实际大小
//        // 写入文件,1代表的字节
//        fwrite(out_buffer,1,out_buffer_size,fp_pcm);
//    }
//    fclose(fp_pcm);
//    av_free(out_buffer);
//    swr_free(&swrContext);
//    avcodec_close(codecContext);
//    avformat_close_input(&formatContext);
//
//    env->ReleaseStringUTFChars(input_, input);
//    env->ReleaseStringUTFChars(output_, output);


    const char *src = env->GetStringUTFChars(input_, 0);
    const char *out = env->GetStringUTFChars(output_, 0);
    av_register_all();//注册所有容器解码器
    AVFormatContext *fmt_ctx = avformat_alloc_context();

    if (avformat_open_input(&fmt_ctx, src, NULL, NULL) < 0) {//打开文件
        LOGE("open file error");
        return;
    }
    if (avformat_find_stream_info(fmt_ctx, NULL) < 0) {//读取音频格式文件信息
        LOGE("find stream info error");
        return;
    }
    //获取音频索引
    int audio_stream_index = -1;
    for (int i = 0; i < fmt_ctx->nb_streams; i++) {
        if (fmt_ctx->streams[i]->codecpar->codec_type == AVMEDIA_TYPE_AUDIO) {
            audio_stream_index = i;
            LOGI("find audio stream index");
            break;
        }
    }
    //获取解码器
    AVCodecContext *codec_ctx = avcodec_alloc_context3(NULL);
    avcodec_parameters_to_context(codec_ctx, fmt_ctx->streams[audio_stream_index]->codecpar);
    AVCodec *codec = avcodec_find_decoder(codec_ctx->codec_id);
    //打开解码器
    if (avcodec_open2(codec_ctx, codec, NULL) < 0) {
        LOGE("could not open codec");
        return;
    }
    //分配AVPacket和AVFrame内存，用于接收音频数据，解码数据
    AVPacket *packet = av_packet_alloc();
    AVFrame *frame = av_frame_alloc();
    int got_frame;//接收解码结果
    int index = 0;
    //pcm输出文件
    FILE *out_file = fopen(out, "wb");
    while (av_read_frame(fmt_ctx, packet) == 0) {//将音频数据读入packet
        if (packet->stream_index == audio_stream_index) {//取音频索引packet
            if (avcodec_decode_audio4(codec_ctx, frame, &got_frame, packet) <
                0) {//将packet解码成AVFrame
                LOGE("decode error:%d", index);
                break;
            }
            if (got_frame > 0) {
                LOGI("decode frame:%d", index++);
                fwrite(frame->data[0], 1, static_cast<size_t>(frame->linesize[0]),
                       out_file); //想将单个声道pcm数据写入文件

            }
        }
    }
    LOGI("decode finish...");
    //释放资源
    av_packet_unref(packet);
    av_frame_free(&frame);
    avcodec_close(codec_ctx);
    avformat_close_input(&fmt_ctx);
    fclose(out_file);
}