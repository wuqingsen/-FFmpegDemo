package com.wuqingsen.openglall.chapter7;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLSurfaceView.Renderer;
import android.opengl.GLU;
import android.opengl.GLUtils;

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
 * annotation:雾气
 */
public class GLRender7 implements Renderer {

    private Bitmap mBitmapTexture = null;

    private int mTexture[];

    // X轴循环变量
    private int xloop;
    // Y轴循环变量
    private int yloop;

    private float xrot, yrot, zrot;
    // 雾的颜色设为白色
    private float fogColor[] = {0.5f, 0.5f, 0.5f, 1.0f};

    // 保存盒子的显示列表
    FloatBuffer boxVertices;
    FloatBuffer boxTexCoords;

    // 保存盒子顶部的显示列表
    FloatBuffer topVertices;
    FloatBuffer topTexCoords ;

    float[][] boxcol = {
            {1.0f, 0.0f, 0.0f},
            {1.0f, 0.5f, 0.0f},
            {1.0f, 1.0f, 0.0f},
            {0.0f, 1.0f, 0.0f},
            {0.0f, 1.0f, 1.0f},
    };

    float[][] topcol = {
            {0.5f, 0.0f, 0.0f},
            {0.5f, 0.25f, 0.0f},
            {0.5f, 0.5f, 0.0f},
            {0.0f, 0.5f, 0.0f},
            {0.0f, 0.5f, 0.5f},
    };

    public GLRender7(Context context) {
        mBitmapTexture = BitmapFactory.decodeResource(context.getResources(), R.drawable.img);
        mTexture = new int[1];
    }

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

        // 绘制列表
        drawList(gl);

        xrot += 0.5f;
        yrot += 0.6f;
        zrot += 0.3f;
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
        gl.glFrustumf(-ratio, ratio, -1, 1, 1, 1000f);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        // TODO Auto-generated method stub

        //告诉系统需要对透视进行修正
        gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);

        //设置清理屏幕的颜色
        gl.glClearColor(0.5f, 0.5f, 0.5f, 1.0f);

        //启用深度缓存
        gl.glEnable(GL10.GL_DEPTH_TEST);

        //剔除背面（这里我们不关心背面）
        gl.glEnable(GL10.GL_CULL_FACE);

        // 启用阴影平滑
        gl.glShadeModel(GL10.GL_SMOOTH);

        // 清理深度缓存
        gl.glClearDepthf(30.0f);

        //深度测试的类型(深度小或相等的时候也渲染)
        gl.glDepthFunc(GL10.GL_LEQUAL);
        //深度小的时候才渲染
        //gl.glDepthFunc(GL10.GL_LESS);

        loadTexture(gl);

        // 设置光效
        setupLight(gl);

        // 使用颜色材质
        gl.glEnable(GL10.GL_COLOR_MATERIAL);

        setupFog(gl);
    }

    /* 构建列表 */
    public void BuildLists(GL10 gl) {

        float[] boxA = new float[]{
                -1.0f, -1.0f, -1.0f, 1.0f, -1.0f, -1.0f, 1.0f, -1.0f, 1.0f, -1.0f, -1.0f, 1.0f,
                -1.0f, -1.0f, 1.0f, 1.0f, -1.0f, 1.0f, 1.0f, 1.0f, 1.0f, -1.0f, 1.0f, 1.0f,
                -1.0f, -1.0f, -1.0f, -1.0f, 1.0f, -1.0f, 1.0f, 1.0f, -1.0f, 1.0f, -1.0f, -1.0f,
                1.0f, -1.0f, -1.0f, 1.0f, 1.0f, -1.0f, 1.0f, 1.0f, 1.0f, 1.0f, -1.0f, 1.0f,
                - 1.0f, -1.0f, -1.0f, -1.0f, -1.0f, 1.0f, -1.0f, 1.0f, 1.0f, -1.0f, 1.0f, -1.0f
        };
        ByteBuffer mbb = ByteBuffer.allocateDirect(boxA.length * 4);
        mbb.order(ByteOrder.nativeOrder());
        boxVertices = mbb.asFloatBuffer();
        boxVertices.put(boxA);
        boxVertices.position(0);


        float[] boxT = new float[]{
        1.0f, 1.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f,
                0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f,
                1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f, 0.0f, 0.0f,
                1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f, 0.0f, 0.0f,
                0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f
        };
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(boxT.length * 4);
        byteBuffer.order(ByteOrder.nativeOrder());
        boxTexCoords = byteBuffer.asFloatBuffer();
        boxTexCoords.put(boxT);
        boxTexCoords.position(0);


        float[] topTCFloat = new float[]{
                0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f
        };
        ByteBuffer topTC = ByteBuffer.allocateDirect(topTCFloat.length * 4);
        topTC.order(ByteOrder.nativeOrder());
        topTexCoords = topTC.asFloatBuffer();
        topTexCoords.put(topTCFloat);
        topTexCoords.position(0);


        float[] topVFloat = new float[]{
                -1.0f, 1.0f, -1.0f, -1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, -1.0f
        };
        ByteBuffer bt = ByteBuffer.allocateDirect(topVFloat.length * 4);
        bt.order(ByteOrder.nativeOrder());
        topVertices = bt.asFloatBuffer();
        topVertices.put(topVFloat);
        topVertices.position(0);
    }

    /* 装载贴图 */
    private void loadTexture(GL10 gl) {
        // 开启2D纹理贴图
        gl.glEnable(GL10.GL_TEXTURE_2D);

        IntBuffer intBuffer = IntBuffer.allocate(1);
        // 创建纹理
        gl.glGenTextures(1, intBuffer);
        mTexture[0] = intBuffer.get();
        // 绑定纹理
        gl.glBindTexture(GL10.GL_TEXTURE_2D, mTexture[0]);

        // 生成纹理
        GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, mBitmapTexture, 0);
        // 配置图像
        gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR_MIPMAP_NEAREST);
        gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
        // 创建显示列表
        BuildLists(gl);
    }


    private void drawList(GL10 gl) {
        // 绑定纹理
        gl.glBindTexture(GL10.GL_TEXTURE_2D, mTexture[0]);

        // 开启设置顶点数组
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);

        // 开启设置纹理贴图坐标数组
        gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);

        for (yloop = 1; yloop < 6; yloop++) {
            for (xloop = 0; xloop < yloop; xloop++) {
                // 重置模型视图矩阵
                gl.glLoadIdentity();

                // 设置盒子的位置（平移）
                gl.glTranslatef(1.4f + ((float) (xloop) * 2.8f) - ((float) (yloop) * 1.4f), ((6.0f - (float) (yloop)) * 2.4f) - 7.0f, -20.0f);

                // 旋转(x轴)
                gl.glRotatef(45.0f - (2.0f * yloop) + xrot, 1.0f, 0.0f, 0.0f);
                // 旋转(y轴)
                gl.glRotatef(45.0f + yrot, 0.0f, 1.0f, 0.0f);
                // 设置颜色
                gl.glColor4f(boxcol[yloop - 1][0], boxcol[yloop - 1][1], boxcol[yloop - 1][2], 1.0f);

                // 设置顶点数组
                gl.glVertexPointer(3, GL10.GL_FLOAT, 0, boxVertices);
                // 设置纹理坐标数组
                gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, boxTexCoords);

                // 绘制
                gl.glDrawArrays(GL10.GL_TRIANGLE_FAN, 0, 4);
                gl.glDrawArrays(GL10.GL_TRIANGLE_FAN, 4, 4);
                gl.glDrawArrays(GL10.GL_TRIANGLE_FAN, 8, 4);
                gl.glDrawArrays(GL10.GL_TRIANGLE_FAN, 12, 4);
                gl.glDrawArrays(GL10.GL_TRIANGLE_FAN, 16, 4);

                // 设置单一颜色
                gl.glColor4f(topcol[yloop - 1][0], topcol[yloop - 1][1], topcol[yloop - 1][2], 1.0f);
                // 设置顶点数组
                gl.glVertexPointer(3, GL10.GL_FLOAT, 0, topVertices);
                // 设置纹理坐标数组
                gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, topTexCoords);
                // 绘制
                gl.glDrawArrays(GL10.GL_TRIANGLE_FAN, 0, 4);
            }
        }

        // 禁止纹理坐标数组设置
        gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
        // 禁止设置顶点数组
        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
    }

    private void setupLight(GL10 gl) {
        //开启光效
        gl.glEnable(GL10.GL_LIGHTING);

        //开启0号光源
        gl.glEnable(GL10.GL_LIGHT0);

        //环境光的颜色
        FloatBuffer light0Ambient = FloatBuffer.wrap(new float[]{0.5f, 0.5f, 0.5f, 1.0f});
        //设置环境光
        gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_AMBIENT, light0Ambient);

        //散射光的颜色
        FloatBuffer light0Diffuse = FloatBuffer.wrap(new float[]{1.0f, 1.0f, 1.0f, 1.0f});
        //设置散射光
        gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_DIFFUSE, light0Diffuse);

        //高光的颜色
        FloatBuffer light0Position = FloatBuffer.wrap(new float[]{0.0f, 0.0f, 2.0f, 1.0f});
        //设置高光
        gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_POSITION, light0Position);
    }

    private void setupFog(GL10 gl) {
        // 设置雾气的模式
//        gl.glFogx(GL10.GL_FOG_MODE, GL10.GL_EXP);

//        gl.glFogx(GL10.GL_FOG_MODE, GL10.GL_EXP2);
        gl.glFogx(GL10.GL_FOG_MODE, GL10.GL_LINEAR);

        // 设置雾气的颜色
        gl.glFogfv(GL10.GL_FOG_COLOR, fogColor, 0);

        // 设置雾气的密度
        gl.glFogf(GL10.GL_FOG_DENSITY, 0.35f);

        // 设置修正(设置雾气的渲染方式)
        gl.glHint(GL10.GL_FOG_HINT, GL10.GL_DONT_CARE);

        // 雾气的开始位置
        gl.glFogf(GL10.GL_FOG_START, 1.0f);

        // 雾气的结束位置
        gl.glFogf(GL10.GL_FOG_END, 35.0f);

        // 开启雾气
        gl.glEnable(GL10.GL_FOG);
    }

}

