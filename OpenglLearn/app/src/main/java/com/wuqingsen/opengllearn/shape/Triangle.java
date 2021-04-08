package com.wuqingsen.opengllearn.shape;

import android.opengl.GLES20;
import android.opengl.Matrix;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

/**
 * wuqingsen on 2021/4/7
 * Mailbox:807926618@qq.com
 * annotation:三角形
 * 创建顶点数组，写顶点着色器和片段着色器，声明顶点数组和颜色数组
 */
public class Triangle {
    //opengl操作
    //初始化

    int mProgram;

    //渲染
    static float triangleCoords[] = {
            0.5f, 0.5f, 0.0f,
            -0.5f, -0.5f, 0.0f,
            0.5f, -0.5f, 0.0f,
    };

    //顶点着色器
    private String vertextShaderCode = "attribute vec4 vPosition;" +
            "uniform mat4 vMatrix;\n" +
            "void main(){" +
            "gl_Position=vMatrix*vPosition;" +
            "}";
    //片段着色器
    private final String fragmentShaderCode = "precision mediump float;\n" +
            "uniform vec4 vColor;\n" +
            "void main(){\n" +
            "    gl_FragColor=vColor;\n" +
            "}";

    private FloatBuffer vertexBuffer;

    private float[] mViewMatrix = new float[16];
    private float[] mProjectMatrix = new float[16];
    private float[] mMVPMatrix = new float[16];

    public Triangle() {
        //GPU声明空间的容量,总长度*字节数(float有4个字节)
        ByteBuffer bb = ByteBuffer.allocateDirect(triangleCoords.length * 4);
        bb.order(ByteOrder.nativeOrder());//内存排列顺序(默认顺序)
        vertexBuffer = bb.asFloatBuffer();//将ByteBuffer转化为管道
        //将gl语言放到管道,将语法推送到GPU
        vertexBuffer.put(triangleCoords);
        vertexBuffer.position(0);

        //创建顶点着色器
        int shader = GLES20.glCreateShader(GLES20.GL_VERTEX_SHADER);
        GLES20.glShaderSource(shader, vertextShaderCode);//编译
        GLES20.glCompileShader(shader);

        //片段着色器
        int fragmentShader = GLES20.glCreateShader(GLES20.GL_FRAGMENT_SHADER);
        GLES20.glShaderSource(fragmentShader, fragmentShaderCode);//编译
        GLES20.glCompileShader(fragmentShader);

        //将片段着色器和顶点着色器放到同一程序进行管理
        mProgram = GLES20.glCreateProgram();
        GLES20.glAttachShader(mProgram, shader);
        GLES20.glAttachShader(mProgram, fragmentShader);

        //连接到着色器程序
        GLES20.glLinkProgram(mProgram);

    }

    float color[] = {1.0f, 1.0f, 1.0f, 1.0f};

    public void onSurfaceChanged(GL10 gl, int width, int height) {
        //计算宽高比
        float ratio = (float) width / height;
        //投影矩阵
        Matrix.frustumM(mProjectMatrix, 0, -ratio, ratio, -1, 1, 3, 120);
        //相机
        Matrix.setLookAtM(mViewMatrix, 0, 0, 0, 7, //摄像机坐标
                0f, 0f, 0f,//目标物的中心坐标
                0f, 1.0f, 0f);//相机方向
        //计算变换矩阵
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectMatrix, 0, mViewMatrix, 0);
    }

    public void onDrawFrame(GL10 gl) {
        //渲染
        GLES20.glUseProgram(mProgram);

        int mMatrixHandler = GLES20.glGetUniformLocation(mProgram, "vMatrix");
        GLES20.glUniformMatrix4fv(mMatrixHandler, 1, false, mMVPMatrix, 0);
        //拿到native指针，gpu某个内存地址;获取vPosition对应的内存地址
        int mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
        //允许对该内存存储变量进行读写
        GLES20.glEnableVertexAttribArray(mPositionHandle);
        GLES20.glVertexAttribPointer(mPositionHandle, 3, GLES20.GL_FLOAT, false, 3 * 4, vertexBuffer);
        int mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");
        GLES20.glUniform4fv(mColorHandle, 1, color, 0);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 3);
        //关闭对该内存存储变量进行读写
        GLES20.glDisableVertexAttribArray(mPositionHandle);
    }
}
