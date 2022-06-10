package com.wuqingsen.openglall.chapter6;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLSurfaceView.Renderer;
import android.opengl.GLU;
import android.opengl.GLUtils;
import android.view.KeyEvent;

import com.wuqingsen.openglall.R;
import com.wuqingsen.openglall.utils.BufferUtil;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * wuqingsen on 2021/4/29
 * Mailbox:807926618@qq.com
 * annotation:纹理
 * 纹理创建的流程：
 * 开启纹理(混色)->创建纹理(glGenTextures)->绑定纹理(glBindTexture)->
 * 生成纹理(装载贴图，纹理限制，texImage2D)->纹理坐标(正方形，三角形)->图像配置(线性插值，重复，限制拉伸)
 */
public class GLRender6 implements Renderer {
    private Bitmap mBitmapTexture = null;

    int mTexture[];

    private Tunnel3D tunnel;
    private boolean created;

    private float centerX = 0.0f;
    private float centerY = 0.0f;

    public GLRender6(Context context) {
        mBitmapTexture = BitmapFactory.decodeResource(context.getResources(), R.drawable.img);
        mTexture = new int[1];

        tunnel = new Tunnel3D(10, 20);
        created = false;
    }


    @Override
    public void onDrawFrame(GL10 gl) {
        // TODO Auto-generated method stub

        // 检查是否创建egl
        boolean c = false;
        synchronized (this) {
            c = created;
        }
        if (!c)
            return;

        // 首先清理屏幕
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

        // 设置模型视图矩阵
        gl.glMatrixMode(GL10.GL_MODELVIEW);

        // 重置矩阵
        gl.glLoadIdentity();

        // 视点变换
        GLU.gluLookAt(gl, 0, 0, 1, centerX, centerY, 0, 0, 1, 0);

        //设置渲染模式
        gl.glShadeModel(GL10.GL_SMOOTH);

        // 允许设置顶点数组
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        // 允许设置颜色数组
        gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
        // 允许设置纹理坐标数组
        gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);

        // 渲染隧道
        tunnel.render(gl, -0.6f);

        // 隧道动画
        tunnel.nextFrame();

        // 禁止设置顶点数组
        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
        // 禁止设置颜色数组
        gl.glDisableClientState(GL10.GL_COLOR_ARRAY);
        // 禁止设置纹理坐标数组
        gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);

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

        // 创建一个对称的透视投影矩阵
        GLU.gluPerspective(gl, 45.0f, ratio, 1f, 100f);
    }


    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        // TODO Auto-generated method stub

        // 告诉系统需要对透视进行修正
        gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);

        // 设置清理屏幕的颜色
        gl.glClearColor(0, 0, 0, 1);

        // 启用深度缓存
        gl.glEnable(GL10.GL_DEPTH_TEST);

        initApp(gl);

        setupLight(gl);
    }


    private void initApp(GL10 gl) {
        created = true;

        // 启动2D纹理贴图
        gl.glEnable(GL10.GL_TEXTURE_2D);

        loadTexture(gl, mBitmapTexture);
    }


    private void loadTexture(GL10 gl, Bitmap bmp) {
        ByteBuffer bb = ByteBuffer.allocateDirect(bmp.getHeight() * bmp.getWidth() * 4);
        bb.order(ByteOrder.nativeOrder());
        IntBuffer ib = bb.asIntBuffer();

        for (int y = 0; y < bmp.getHeight(); y++) {
            for (int x = 0; x < bmp.getWidth(); x++) {
                ib.put(bmp.getPixel(x, y));
            }
        }
        ib.position(0);
        bb.position(0);
        //创建纹理
        gl.glGenTextures(1, mTexture, 0);

        //绑定纹理
        gl.glBindTexture(GL10.GL_TEXTURE_2D, mTexture[0]);

        //加载纹理
        gl.glTexImage2D(GL10.GL_TEXTURE_2D, 0, GL10.GL_RGBA, bmp.getWidth(), bmp.getHeight(), 0, GL10.GL_RGBA, GL10.GL_UNSIGNED_BYTE, bb);
        /**
         * int target,  //此纹理是一个2D纹理
         * int level,    //代表图像的详细程度, 默认为0即可
         * int internalformat, //颜色成分R(红色分量)、G(绿色分量)、B(蓝色分量)三部分，若为4则是R(红色分量)、G(绿色分量)、B(蓝色分量)、Alpha
         * int width, //纹理的宽度
         * int height, //纹理的高度
         * int border, //边框的值
         * int format, //告诉OpenGL图像数据由红、绿、蓝、A三色数据组成
         * int type,   //组成图像的数据是无符号字节类型
         * Buffer pixels //告诉OpenGL纹理数据的来源
         */
        //设置线性插值算法
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
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

    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
            centerX -= 0.1f;
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
            centerX += 0.1f;
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
            centerY += 0.1f;
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
            centerY -= 0.1f;
        }
        return false;
    }

}

