package com.wuqingsen.bigeye.filter;

import android.content.Context;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;

import com.wuqingsen.bigeye.R;
import com.wuqingsen.bigeye.util.OpenGLUtils;

/**
 * wuqingsen on 2021/4/12
 * Mailbox:807926618@qq.com
 * annotation:获取创建数据，并且创建FBO，在FBO中添加特效
 */
public class CameraFilter extends AbstractFilter {
    //FBO
    int[] mFrameBuffer;
    //纹理
    int[] mFrameBufferTextures;

    private float[] matrix;
    public CameraFilter(Context context) {
        super(context, R.raw.camera_vertex, R.raw.camera_frag);
    }

    //坐标转化

    @Override
    protected void initCoordinate() {
        mTextureBuffer.clear();
        //摄像头是颠倒并且是镜像的，原始坐标
        float[] TEXTURE = {
                0.0f, 0.0f,
                1.0f, 0.0f,
                0.0f, 1.0f,
                1.0f, 1.0f
        };
        mTextureBuffer.put(TEXTURE);
    }

    public void onReady(int width, int height) {
        super.onReady(width, height);
        mFrameBuffer = new int[1];
        //生成一个FBO,缓冲区，对纹理进行操作；参数：纹理的数量；纹理数组；对第几个进行生成
        GLES20.glGenFramebuffers(1, mFrameBuffer, 0);
        //实例化一个纹理，对纹理和FBO绑定，对纹理操作
        mFrameBufferTextures = new int[1];
        OpenGLUtils.glGenTextures(mFrameBufferTextures);
        //FBO与纹理绑定
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mFrameBufferTextures[0]);

        //绑定片段着色器和纹理
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, mFrameBuffer[0]);

        //设置纹理显示信息，宽高
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA,
                mWidth, mHeight, 0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, null);
        //将纹理与FBO联系
        GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0,
                GLES20.GL_TEXTURE_2D, mFrameBufferTextures[0], 0);

        //FBO与纹理解绑
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
    }

    @Override
    public int onDrawFrame(int textureId) {

        //设置显示窗口
        GLES20.glViewport(0, 0, mWidth, mHeight);
        //不调用会surfaceview中渲染；将它渲染到FBO(缓存区)中
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, mFrameBuffer[0]);
        //渲染
        GLES20.glUseProgram(mProgram);
        mVertexBuffer.position(0);//设置为初始状态
        GLES20.glVertexAttribPointer(vPosition, 2, GLES20.GL_FLOAT, false, 0, mVertexBuffer);
        GLES20.glEnableVertexAttribArray(vPosition);//允许对该内存存储变量进行读写

        mTextureBuffer.position(0);
        GLES20.glVertexAttribPointer(vCoord, 2, GLES20.GL_FLOAT, false, 0, mTextureBuffer);
        GLES20.glEnableVertexAttribArray(vCoord);//允许对该内存存储变量进行读写

        //变换矩阵
        GLES20.glUniformMatrix4fv(vMatrix,1,false,matrix,0);

        //激活采样器
        GLES20.glActiveTexture(GLES20.GL_TEXTURE);
        //GPU与摄像头绑定；GLES11Ext.GL_TEXTURE_EXTERNAL_OES:采样器；
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textureId);

        GLES20.glUniform1i(vTexture, 0);
        //绘制
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);

        //FBO与纹理解绑
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, 0);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
        return mFrameBufferTextures[0];
    }

    //设置矩阵
    public void setMatrix(float[] mtx){
        this.matrix = mtx;
    }
}
