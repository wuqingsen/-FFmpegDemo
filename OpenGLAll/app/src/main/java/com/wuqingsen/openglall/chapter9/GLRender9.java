package com.wuqingsen.openglall.chapter9;

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
 * wuqingsen on 6/2/21
 * Mailbox:807926618@qq.com
 * annotation:
 */
public class GLRender9 implements Renderer {


    private Bitmap mBitmapTexture = null;

    int mTexture[];

    // Points网格顶点数组(45,45,3)
    float vertex[][][] = new float[45][45][3];
    // 指定旗形波浪的运动速度
    int wiggle_count = 0;
    // 临时变量
    float hold;
    // 旋转变量
    float xrot, yrot, zrot;
    private float[] texCoord = new float[8];
    private float[] points = new float[12];

    public GLRender9(Context context) {
        mBitmapTexture = BitmapFactory.decodeResource(context.getResources(), R.drawable.img9);
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

        // 绘制
        draw(gl);
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

        // 创建一个透视投影矩阵（设置视口大小）
        gl.glFrustumf(-ratio, ratio, -1, 1, 1, 30);
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

        // 设置深度测试
        gl.glClearDepthf(10.0f);

        // 深度测试的类型（小于或者等于时我们都渲染）
        gl.glDepthFunc(GL10.GL_LEQUAL);

        // 装载纹理
        loadTexture(gl);

        // 初始化数据
        initData();
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

        mBitmapTexture.recycle();
        mBitmapTexture = null;
    }

    private void initData() {
        // 沿X平面循环
        for (int x = 0; x < 45; x++) {
            // 沿Y平面循环
            for (int y = 0; y < 45; y++) {
                // 向表面添加波浪效果
                vertex[x][y][0] = ((float) x / 5.0f) - 4.5f;
                vertex[x][y][1] = (((float) y / 5.0f) - 4.5f);
                vertex[x][y][2] = (float) (Math.sin(((((float) x / 5.0f) * 40.0f) / 360.0f) * 3.141592654 * 2.0f));
            }
        }
    }

    private void draw(GL10 gl) {
        // 循环变量
        int x, y;
        // 用来将旗形的波浪分割成很小的四边形
        float float_x, float_y, float_xb, float_yb;

        // 平移操作
        gl.glTranslatef(0.0f, 0.0f, -12.0f);

        // 旋转操作
        gl.glRotatef(xrot, 1.0f, 0.0f, 0.0f);
        gl.glRotatef(yrot, 0.0f, 1.0f, 0.0f);
        gl.glRotatef(zrot, 0.0f, 0.0f, 1.0f);

        // 允许设置顶点数组和纹理坐标数组
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);

        // 设置顶点数组、纹理坐标数组
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, BufferUtil.floatToBuffer(points));
        gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, BufferUtil.floatToBuffer(texCoord));

        // 绑定纹理
        gl.glBindTexture(GL10.GL_TEXTURE_2D, mTexture[0]);

        for (x = 0; x < 44; x++) {
            for (y = 0; y < 44; y++) {
                float_x = (float) (x) / 44.0f; // 生成X浮点值
                float_y = (float) (y) / 44.0f; // 生成Y浮点值
                float_xb = (float) (x + 1) / 44.0f; // X浮点值+0.0227f
                float_yb = (float) (y + 1) / 44.0f; // Y浮点值+0.0227f

                // 保存纹理坐标数组
                texCoord[0] = (float_x);
                texCoord[1] = (float_y);
                texCoord[2] = (float_x);
                texCoord[3] = (float_yb);
                texCoord[4] = (float_xb);
                texCoord[5] = (float_yb);
                texCoord[6] = (float_xb);
                texCoord[7] = (float_y);

                // 保存顶点数组
                points[0] = (vertex[x][y][0]);
                points[1] = (vertex[x][y][1]);
                points[2] = (vertex[x][y][2]);

                points[3] = (vertex[x][y + 1][0]);
                points[4] = (vertex[x][y + 1][1]);
                points[5] = (vertex[x][y + 1][2]);

                points[6] = (vertex[x + 1][y + 1][0]);
                points[7] = (vertex[x + 1][y + 1][1]);
                points[8] = (vertex[x + 1][y + 1][2]);

                points[9] = (vertex[x + 1][y][0]);
                points[10] = (vertex[x + 1][y][1]);
                points[11] = (vertex[x + 1][y][2]);

                // 绘制
                gl.glDrawArrays(GL10.GL_TRIANGLE_FAN, 0, 4);
            }
        }

        // 禁止设置顶点数组、纹理坐标数组
        gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);

        // 用来降低波浪速度(每隔2帧一次)
        if (wiggle_count == 2) {
            // 沿Y平面循环
            for (y = 0; y < 45; y++) {
                // 存储当前左侧波浪值
                hold = vertex[0][y][2];
                // 沿X平面循环
                for (x = 0; x < 44; x++) {
                    // 当前波浪值等于其右侧的波浪值
                    vertex[x][y][2] = vertex[x + 1][y][2];
                }
                // 刚才的值成为最左侧的波浪值
                vertex[44][y][2] = hold;
            }
            // 计数器清零
            wiggle_count = 0;
        }
        wiggle_count++;

        //改变旋转的角度
        xrot += 0.3f;
        yrot += 0.2f;
        zrot += 0.4f;
    }

}
