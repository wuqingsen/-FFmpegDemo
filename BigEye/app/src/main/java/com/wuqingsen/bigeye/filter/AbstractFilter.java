package com.wuqingsen.bigeye.filter;

import android.content.Context;
import android.opengl.GLES20;

import com.wuqingsen.bigeye.util.OpenGLUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * wuqingsen on 2021/4/12
 * Mailbox:807926618@qq.com
 * annotation:顶点和片段着色器创建
 * FBO截流做一些操作，再绘制到屏幕上
 */
public abstract class AbstractFilter {
    //顶点着色器
    protected int mVertexShaderId;
    //片段着色器
    protected int mFragmentShaderId;
    protected FloatBuffer mTextureBuffer;
    protected FloatBuffer mVertexBuffer;

    protected int vTexture;//纹理id
    protected int vMatrix;//矩阵id
    protected int vCoord;//片段着色器接收的矩阵id
    protected int vPosition;
    protected int mProgram;
    protected int mWidth;
    protected int mHeight;

    public void onReady(int width,int height){
        this.mWidth = width;
        this.mHeight = height;
    }
    public AbstractFilter(Context context, int vertexShaderId, int fragmentShaderId) {
        this.mVertexShaderId = vertexShaderId;
        this.mFragmentShaderId = fragmentShaderId;
        //摄像头是2d  三维

        //顶点着色器,按照OpenGL坐标
        mVertexBuffer = ByteBuffer.allocateDirect(4 * 2 * 4)//4个顶点*每个顶点是二维*4个字节
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        mVertexBuffer.clear();
        float[] VERTEX = {
                -1.0f, -1.0f,
                1.0f, -1.0f,
                -1.0f, 1.0f,
                1.0f, 1.0f
        };
        mVertexBuffer.put(VERTEX);

        //片段着色器,按照手机坐标
        mTextureBuffer = ByteBuffer.allocateDirect(4 * 2 * 4)//4个顶点*每个顶点是二维*4个字节
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        mTextureBuffer.clear();
        float[] TEXTURE = {
                0.0f, 1.0f,
                1.0f, 1.0f,
                0.0f, 0.0f,
                1.0f, 0.0f
        };
        mTextureBuffer.put(TEXTURE);

        initilize(context);
        initCoordinate();
    }

    protected abstract void initCoordinate();

    private void initilize(Context context) {
        String vertexShader = OpenGLUtils.readRawFileFile(context, mVertexShaderId);
        String fragmentShader = OpenGLUtils.readRawFileFile(context, mFragmentShaderId);

        mProgram = OpenGLUtils.loadProgram(vertexShader, fragmentShader);
        //获取id，glGetAttribLocation顶点着色器,glGetUniformLocation片段着色器
        vPosition = GLES20.glGetAttribLocation(mProgram, "vPosition");
        vCoord = GLES20.glGetAttribLocation(mProgram, "vCoord");
        vMatrix = GLES20.glGetUniformLocation(mProgram, "vMatrix");
        vTexture = GLES20.glGetUniformLocation(mProgram, "vTexture");
    }

    //渲染
    public int onDrawFrame(int textureId) {
        //设置显示窗口
        GLES20.glViewport(0, 0, mWidth, mHeight);

        //渲染
        GLES20.glUseProgram(mProgram);

        mVertexBuffer.position(0);

        GLES20.glVertexAttribPointer(vPosition, 2, GLES20.GL_FLOAT, false, 0, mVertexBuffer);
        GLES20.glEnableVertexAttribArray(vPosition);//允许对该内存存储变量进行读写

        mTextureBuffer.position(0);
        GLES20.glVertexAttribPointer(vCoord, 2, GLES20.GL_FLOAT, false, 0, mTextureBuffer);
        GLES20.glEnableVertexAttribArray(vCoord);//允许对该内存存储变量进行读写

        //激活采样器
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        //GPU与摄像头绑定；GLES20.GL_TEXTURE_2D:采样器；
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);

        GLES20.glUniform1i(vTexture, 0);
        //绘制
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
        return textureId;
    }

}
