package com.wuqingsen.openglall.chapter5;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLU;
import android.opengl.GLUtils;
import android.opengl.GLSurfaceView.Renderer;

import com.wuqingsen.openglall.R;
import com.wuqingsen.openglall.utils.BufferUtil;

/**
 * wuqingsen on 2021/4/29
 * Mailbox:807926618@qq.com
 * annotation:纹理
 * 纹理创建的流程：
 * 开启纹理(混色)->创建纹理(glGenTextures)->绑定纹理(glBindTexture)->
 * 生成纹理(装载贴图，纹理限制，texImage2D)->纹理坐标(正方形，三角形)->图像配置(线性插值，重复，限制拉伸)
 *
 */
public class GLRender5 implements Renderer {
    private Bitmap mBitmapTexture = null;

    int mTexture[];

    private float rot = 0.0f;

    public GLRender5(Context context) {
        mBitmapTexture = BitmapFactory.decodeResource(context.getResources(), R.drawable.texture);
        mTexture = new int[1];
    }

    //正方形的定点数组
    private float[] verticesSquare = new float[]{
            -1.0f, 1.0f, -0.0f,
            1.0f, 1.0f, -0.0f,
            -1.0f, -1.0f, -0.0f,
            1.0f, -1.0f, -0.0f};
    //正方形的法线数组
    private float[] normalsSquare = new float[]{
            0.0f, 0.0f, 1.0f,
            0.0f, 0.0f, 1.0f,
            0.0f, 0.0f, 1.0f,
            0.0f, 0.0f, 1.0f};

    //正方形的贴图数组
//    private float[] texCoordsSquare = new float[]{
//            0.0f, 0.5f,
//            0.5f, 0.5f,
//            0.0f, 0.0f,
//            0.5f, 0.0f};

// private float[] texCoordsSquare = new float[]{
//	        0.25f, 0.75f,
//	        0.75f, 0.75f,
//	        0.25f, 0.25f,
//	        0.75f, 0.25f};

//    private float[] texCoordsSquare = new float[]{
//	        0.0f, -1.0f,
//	        -1.0f, -1.0f,
//	        0.0f, 0.0f,
//	        -1.0f, 0.0f};

    private float[] texCoordsSquare = new float[]{
            0.0f, 2.0f,
            2.0f, 2.0f,
            0.0f, 0.0f,
            2.0f, 0.0f};


    //三角形的定点数组
    private float[] verticesTriangle = new float[]{
            -1.0f, 1.0f, -0.0f,
            1.0f, 1.0f, -0.0f,
            0.0f, -1.0f, -0.0f};

    //三角形的法线数组
    private float[] normalsTriangle = new float[]{
            0.0f, 0.0f, 1.0f,
            0.0f, 0.0f, 1.0f,
            0.0f, 0.0f, 1.0f};
    //三角形的贴图数组
    private float[] texCoordsTriangle = new float[]{
            0.0f, 1.0f,
            1.0f, 0.0f,
            0.0f, 0.0f};

    @Override
    public void onDrawFrame(GL10 gl) {
        // TODO Auto-generated method stub

        // 首先清理屏幕
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

        // 设置模型视图矩阵
        gl.glMatrixMode(GL10.GL_MODELVIEW);

        //重置矩阵
        gl.glLoadIdentity();

        // 视点变换
        GLU.gluLookAt(gl, 0, 0, 3, 0, 0, 0, 0, 1, 0);


        //正方形
        drawSquare(gl);

        //三角形
//        drawTriangle(gl);

        //rot+=0.5f;
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        // TODO Auto-generated method stub

        float ratio = (float) width / height;

        // 设置视口(OpenGL场景的大小)
        gl.glViewport(0, 0, width, height);

        // 设置投影矩阵为透视投影
        gl.glMatrixMode(GL10.GL_PROJECTION);

        // 重置投影矩阵（置为单位矩阵）
        gl.glLoadIdentity();

        //创建一个透视投影矩阵（设置视口大小）
        gl.glFrustumf(-ratio, ratio, -1, 1, 1, 1000);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        // TODO Auto-generated method stub

        //告诉系统需要对透视进行修正
        gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);

        //设置清理屏幕的颜色
        gl.glClearColor(0, 0, 0, 1);

        //启用深度缓存
        gl.glEnable(GL10.GL_DEPTH_TEST);

        setupLight(gl);

        setupTexture(gl);
    }


    private void setupLight(GL10 gl) {
        //开启光效
        gl.glEnable(GL10.GL_LIGHTING);

        //开启0号光源
        gl.glEnable(GL10.GL_LIGHT0);

        //环境光的颜色
        FloatBuffer light0Ambient = FloatBuffer.wrap(new float[]{0.4f, 0.4f, 0.4f, 1.0f});
        //设置环境光
        gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_AMBIENT, light0Ambient);

        //漫射光的颜色
        FloatBuffer light0Diffuse = FloatBuffer.wrap(new float[]{0.8f, 0.8f, 0.8f, 1.0f});
        //设置漫射光
        gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_DIFFUSE, light0Diffuse);

        //高光的颜色
        FloatBuffer light0Position = FloatBuffer.wrap(new float[]{10.0f, 10.0f, 10.0f});
        //设置高光
        gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_POSITION, light0Position);
    }


    private void setupTexture(GL10 gl) {
        //打开2D贴图
        gl.glEnable(GL10.GL_TEXTURE_2D);

        //打开混色功能
        gl.glEnable(GL10.GL_BLEND);

        //指定混色方法
        gl.glBlendFunc(GL10.GL_ONE, GL10.GL_SRC_COLOR);

        IntBuffer intBuffer = IntBuffer.allocate(1);
        // 创建纹理
        gl.glGenTextures(1, intBuffer);
        mTexture[0] = intBuffer.get();

        //绑定纹理
        gl.glBindTexture(GL10.GL_TEXTURE_2D, mTexture[0]);

        //当纹理需要被放大和缩小时都使用线性插值方法调整图像
        gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
        gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);

//		//重复效果
//		gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_REPEAT);
//		gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_REPEAT);

        //限制拉伸
        gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);
        gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE);

        //生成纹理（加载图像）
        GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, mBitmapTexture, 0);
    }

    private void drawSquare(GL10 gl) {
        //平移操作
        gl.glTranslatef(0.0f, 0.0f, -3.0f);

        //旋转操作
        gl.glRotatef(rot, 1.0f, 1.0f, 1.0f);

        //缩放操作
        gl.glScalef(3.0f, 3.0f, 3.0f);

        //允许设置顶点数组
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);

        //允许设置法线数组
        gl.glEnableClientState(GL10.GL_NORMAL_ARRAY);

        //允许设置纹理坐标数组
        gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);

        //绑定纹理
        gl.glBindTexture(GL10.GL_TEXTURE_2D, mTexture[0]);

        //设置顶点数组
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, BufferUtil.floatToBuffer(verticesSquare));

        //设置法线数组
        gl.glNormalPointer(GL10.GL_FLOAT, 0, BufferUtil.floatToBuffer(normalsSquare));

        //设置纹理坐标数组
        gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, BufferUtil.floatToBuffer(texCoordsSquare));

        //绘制正方形
        gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);

        //关闭顶点数组、法线数组、纹理坐标数组
        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glDisableClientState(GL10.GL_NORMAL_ARRAY);
        gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
    }

    private void drawTriangle(GL10 gl) {
        //平移操作
        gl.glTranslatef(0.0f, 0.0f, -3.0f);

        //旋转操作
        gl.glRotatef(rot, 1.0f, 1.0f, 1.0f);

        //缩放操作
        gl.glScalef(3.0f, 3.0f, 3.0f);

        //允许设置顶点数组
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);

        //允许设置法线数组
        gl.glEnableClientState(GL10.GL_NORMAL_ARRAY);

        //允许设置纹理坐标数组
        gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);

        //绑定纹理
        gl.glBindTexture(GL10.GL_TEXTURE_2D, mTexture[0]);

        //设置顶点数组
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, BufferUtil.floatToBuffer(verticesTriangle));

        //设置法线数组
        gl.glNormalPointer(GL10.GL_FLOAT, 0, BufferUtil.floatToBuffer(normalsTriangle));

        //设置纹理坐标数组
        gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, BufferUtil.floatToBuffer(texCoordsTriangle));

        //绘制三角形
        gl.glDrawArrays(GL10.GL_TRIANGLES, 0, 3);

        //关闭顶点数组、法线数组、纹理坐标数组
        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glDisableClientState(GL10.GL_NORMAL_ARRAY);
        gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
    }


}

