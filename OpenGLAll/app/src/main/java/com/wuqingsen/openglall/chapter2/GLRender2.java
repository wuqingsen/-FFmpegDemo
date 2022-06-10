package com.wuqingsen.openglall.chapter2;

import android.opengl.GLSurfaceView;
import android.opengl.GLU;

import com.wuqingsen.openglall.utils.BufferUtil;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * wuqingsen on 2021/4/29
 * Mailbox:807926618@qq.com
 * annotation:
 */
public class GLRender2 implements GLSurfaceView.Renderer {

    float rot = 0.0f;//旋转角度

    //顶点数组
    private float[] vertices = new float[]{
            0.f, -0.525731f, 0.850651f,
            0.850651f, 0f, 0.525731f,
            0.850651f, 0f, -0.525731f,
            -0.850651f, 0f, -0.525731f,
            -0.850651f, 0f, 0.525731f,
            -0.525731f, 0.850651f, 0f,
            0.525731f, 0.850651f, 0f,
            0.525731f, -0.850651f, 0f,
            -0.525731f, -0.850651f, 0f,
            0.f, -0.525731f, -0.850651f,
            0.f, 0.525731f, -0.850651f,
            0.f, 0.525731f, 0.850651f
    };

    //颜色数组(r,g,b,a)
    private float[] colors = new float[]{
            1.0f, 0.0f, 0.0f, 1.0f,
            1.0f, 0.5f, 0.0f, 1.0f,
            1.0f, 1.0f, 0.0f, 1.0f,
            0.5f, 1.0f, 0.0f, 1.0f,
            0.0f, 1.0f, 0.0f, 1.0f,
            0.0f, 1.0f, 0.5f, 1.0f,
            0.0f, 1.0f, 1.0f, 1.0f,
            0.0f, 0.5f, 1.0f, 1.0f,
            0.0f, 0.0f, 1.0f, 1.0f,
            0.5f, 0.0f, 1.0f, 1.0f,
            1.0f, 0.0f, 1.0f, 1.0f,
            1.0f, 0.0f, 0.5f, 1.0f
    };

    //索引数组(20个三角形)
    private ByteBuffer icosahedronFaces = ByteBuffer.wrap(new byte[]{
            1, 2, 6,
            1, 7, 2,
            3, 4, 5,
            4, 3, 8,
            6, 5, 11,
            5, 6, 10,
            9, 10, 2,
            10, 9, 3,
            7, 8, 9,
            8, 7, 0,
            11, 0, 1,
            0, 11, 4,
            6, 2, 10,
            1, 6, 11,
            3, 5, 10,
            5, 4, 11,
            2, 7, 9,
            7, 1, 0,
            3, 9, 8,
            4, 8, 0});

    @Override
    public void onDrawFrame(GL10 gl) {
        //清理屏幕
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

        //设置模型视图矩阵
        gl.glMatrixMode(GL10.GL_MODELVIEW);

        //重置矩阵
        gl.glLoadIdentity();

        //视点变化
        GLU.gluLookAt(gl, 0, 0, 3, 0, 0, 0, 0, 1, 0);

        //设置平移
        gl.glTranslatef(0.0f, 0.0f, -3.0f);

        //设置旋转（x,y,z轴）
        gl.glRotatef(rot, 1.0f, 1.0f, 1.0f);

        //缩放
        gl.glScalef(3.0f, 3.0f, 3.0f);

        //打开允许设置顶点
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);

        //打开允许设置颜色
        gl.glEnableClientState(GL10.GL_COLOR_ARRAY);

        //设置顶点数据
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, BufferUtil.floatToBuffer(vertices));

        //设置颜色数组
        gl.glColorPointer(4, GL10.GL_FLOAT, 0, BufferUtil.floatToBuffer(colors));

        //绘制,20个三角形(索引数组)，所以是60个顶点，绘制一个
//        gl.glDrawElements(GL10.GL_TRIANGLES, 60, GL10.GL_UNSIGNED_BYTE, icosahedronFaces);

        //绘制30个相同的物体
        for (int i = 1; i <= 30; i++) {
            gl.glLoadIdentity();
            gl.glTranslatef(0.0f, -1.5f, -3.0f * (float) i);
            gl.glRotatef(rot, 1.0f, 1.0f, 1.0f);
            gl.glDrawElements(GL10.GL_TRIANGLES, 60, GL10.GL_UNSIGNED_BYTE, icosahedronFaces);
        }

        //关闭允许设置顶点
        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);

        //关闭允许设置颜色
        gl.glDisableClientState(GL10.GL_COLOR_ARRAY);

        //改变旋转角度
        rot += 0.5f;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig eglConfig) {

        //告诉系统需要对透视进行修正
        gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);

        //设置清理屏幕颜色,黑色
        gl.glClearColor(0, 0, 0, 1);

        //启用深度缓存
        gl.glEnable(GL10.GL_DEPTH_TEST);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {

        float ratio = (float) width / height;

        //设置视口（OpenGL场景大小）
        gl.glViewport(0, 0, width, height);

        //设置投影矩阵为透视投影
        gl.glMatrixMode(GL10.GL_PROJECTION);

        //重置投影矩阵（置为单位矩阵）
        gl.glLoadIdentity();

        //创建一个透视投影矩阵（设置视口大小）
        gl.glFrustumf(-ratio, ratio, -1, 1, 1.0f, 1000.0f);

        //创建一个正交投影矩阵,(不常用)
//        gl.glOrthof(-ratio, ratio, -1, 1, 1.0f, 1000.0f);
    }
}
