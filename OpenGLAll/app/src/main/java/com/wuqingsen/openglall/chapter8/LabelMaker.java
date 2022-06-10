package com.wuqingsen.openglall.chapter8;

import java.util.ArrayList;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;
import javax.microedition.khronos.opengles.GL11Ext;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Paint.Style;
import android.graphics.drawable.Drawable;
import android.opengl.GLUtils;

public class LabelMaker {
    private int mStrikeWidth;
    private int mStrikeHeight;
    // 颜色数据选择
    private boolean mFullColor;
    // 被字体绘制的画布
    private Bitmap mBitmap;
    // Canvas和paint
    private Canvas mCanvas;
    private Paint mClearPaint;
    // 纹理贴图
    private int mTextureID;
    // 纹理坐标
    private int mU;
    private int mV;
    private int mLineHeight;

    // 标签列表
    private ArrayList<Label> mLabels = new ArrayList<Label>();

    // 操作状态
    private int mState;
    // 构造状态
    private static final int STATE_NEW = 0;
    // 初始化状体
    private static final int STATE_INITIALIZED = 1;
    // 添加字符串状态
    private static final int STATE_ADDING = 2;
    // 绘制字符串状态
    private static final int STATE_DRAWING = 3;

    // 选择颜色，宽度和高度
    public LabelMaker(boolean fullColor, int strikeWidth, int strikeHeight) {
        mFullColor = fullColor;
        mStrikeWidth = strikeWidth;
        mStrikeHeight = strikeHeight;
        mClearPaint = new Paint();
        mClearPaint.setARGB(0, 0, 0, 0);
        mClearPaint.setStyle(Style.FILL);
        mState = STATE_NEW;
    }


    //  初始化
    public void initialize(GL10 gl) {
        mState = STATE_INITIALIZED;

        //  创建纹理
        int[] textures = new int[1];
        gl.glGenTextures(1, textures, 0);
        mTextureID = textures[0];

        // 绑定纹理
        gl.glBindTexture(GL10.GL_TEXTURE_2D, mTextureID);

        // 使用性能最佳来配置图像.
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_NEAREST);

        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE);

        //设置纹理和物体表面颜色处理方式
        //只用纹理颜色，不关心物体表面颜色
        gl.glTexEnvf(GL10.GL_TEXTURE_ENV, GL10.GL_TEXTURE_ENV_MODE, GL10.GL_REPLACE);

        //和物体表面颜色做与运算。
        //gl.glTexEnvf(GL10.GL_TEXTURE_ENV, GL10.GL_TEXTURE_ENV_MODE, GL10.GL_MODULATE);

        //做融合运算。
        //gl.glTexEnvf(GL10.GL_TEXTURE_ENV, GL10.GL_TEXTURE_ENV_MODE, GL10.GL_BLEND);
    }


    // 卸载-当窗口关闭时，需要卸载
    public void shutdown(GL10 gl) {
        if (gl != null) {
            if (mState > STATE_NEW) {
                int[] textures = new int[1];
                textures[0] = mTextureID;
                gl.glDeleteTextures(1, textures, 0);
                mState = STATE_NEW;
            }
        }
    }


    // 在添加字符串标签之前调用：清楚所有现有的标签
    public void beginAdding(GL10 gl) {
        checkState(STATE_INITIALIZED, STATE_ADDING);
        // 清理所有标签
        mLabels.clear();
        mU = 0;
        mV = 0;
        mLineHeight = 0;
        // 创建一个图像Bitmap
        Bitmap.Config config = mFullColor ? Bitmap.Config.ARGB_4444 : Bitmap.Config.ALPHA_8;
        mBitmap = Bitmap.createBitmap(mStrikeWidth, mStrikeHeight, config);
        // 绑定Canvas
        mCanvas = new Canvas(mBitmap);
        mBitmap.eraseColor(0);
    }


    // 添加标签（gl,字符串，画笔）
    public int add(GL10 gl, String text, Paint textPaint) {
        return add(gl, null, text, textPaint);
    }


    // 添加标签（gl,背景图片,字符串，画笔）
    public int add(GL10 gl, Drawable background, String text, Paint textPaint) {
        return add(gl, background, text, textPaint, 0, 0);
    }

    // 添加一个字符串为空白的标签
    public int add(GL10 gl, Drawable drawable, int minWidth, int minHeight) {
        return add(gl, drawable, null, null, minWidth, minHeight);
    }

    // 添加标签（gl,背景图片,字符串,画笔,最小宽度,最小高度）
    public int add(GL10 gl, Drawable background, String text, Paint textPaint, int minWidth, int minHeight) {
        checkState(STATE_ADDING, STATE_ADDING);
        boolean drawBackground = background != null;
        boolean drawText = (text != null) && (textPaint != null);

        Rect padding = new Rect();
        // 判断背景是否存在，并处理最小宽度和高度
        if (drawBackground) {
            background.getPadding(padding);
            minWidth = Math.max(minWidth, background.getMinimumWidth());
            minHeight = Math.max(minHeight, background.getMinimumHeight());
        }

        int ascent = 0;
        int descent = 0;
        int measuredTextWidth = 0;

        // 如果存在字符串，则取得字符串的宽度
        if (drawText) {
            ascent = (int) Math.ceil(-textPaint.ascent());
            descent = (int) Math.ceil(textPaint.descent());
            measuredTextWidth = (int) Math.ceil(textPaint.measureText(text));
        }
        int textHeight = ascent + descent;
        int textWidth = Math.min(mStrikeWidth, measuredTextWidth);

        int padHeight = padding.top + padding.bottom;
        int padWidth = padding.left + padding.right;
        int height = Math.max(minHeight, textHeight + padHeight);
        int width = Math.max(minWidth, textWidth + padWidth);
        int effectiveTextHeight = height - padHeight;
        int effectiveTextWidth = width - padWidth;

        int centerOffsetHeight = (effectiveTextHeight - textHeight) / 2;
        int centerOffsetWidth = (effectiveTextWidth - textWidth) / 2;


        int u = mU;
        int v = mV;
        int lineHeight = mLineHeight;

        if (width > mStrikeWidth) {
            width = mStrikeWidth;
        }

        // 检查宽度和高度是否能够显示字符串
        if (u + width > mStrikeWidth) {
            // No room, go to the next line:
            u = 0;
            v += lineHeight;
            lineHeight = 0;
        }
        lineHeight = Math.max(lineHeight, height);
        if (v + lineHeight > mStrikeHeight) {
            throw new IllegalArgumentException("Out of texture space.");
        }

        int vBase = v + ascent;

        // 绘制背景
        if (drawBackground) {
            background.setBounds(u, v, u + width, v + height);
            background.draw(mCanvas);
        }

        // 绘制字符串
        if (drawText) {
            mCanvas.drawText(text, u + padding.left + centerOffsetWidth, vBase + padding.top + centerOffsetHeight, textPaint);
        }

        // We know there's enough space, so update the member variables
        mU = u + width;
        mV = v;
        mLineHeight = lineHeight;
        mLabels.add(new Label(width, height, u, v + height, width, -height));

        // 返回该标签的ID
        return mLabels.size() - 1;
    }


    // 添加结束
    public void endAdding(GL10 gl) {
        checkState(STATE_ADDING, STATE_INITIALIZED);
        // 绑定我们绘制的纹理
        gl.glBindTexture(GL10.GL_TEXTURE_2D, mTextureID);

        // 装载纹理
        GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, mBitmap, 0);
        // 释放数据
        mBitmap.recycle();
        mBitmap = null;
        mCanvas = null;
    }


    // 得到宽度
    public float getWidth(int labelID) {
        return mLabels.get(labelID).width;
    }


    // 得到高度
    public float getHeight(int labelID) {
        return mLabels.get(labelID).height;
    }


    // 开始绘制
    public void beginDrawing(GL10 gl, float viewWidth, float viewHeight) {
        checkState(STATE_INITIALIZED, STATE_DRAWING);

        gl.glBindTexture(GL10.GL_TEXTURE_2D, mTextureID);
        gl.glShadeModel(GL10.GL_FLAT);
        gl.glEnable(GL10.GL_BLEND);
        gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
        gl.glColor4x(0x10000, 0x10000, 0x10000, 0x10000);

        gl.glMatrixMode(GL10.GL_PROJECTION);
        gl.glPushMatrix();
        gl.glLoadIdentity();

        // 改变透视为正交
        gl.glOrthof(0.0f, viewWidth, 0.0f, viewHeight, 0.0f, 1.0f);

        gl.glMatrixMode(GL10.GL_MODELVIEW);
        gl.glPushMatrix();
        gl.glLoadIdentity();

        // Magic offsets to promote consistent rasterization.
        gl.glTranslatef(0.375f, 0.375f, 0.0f);

    }


    // 绘制
    public void draw(GL10 gl, float x, float y, int labelID) {
        checkState(STATE_DRAWING, STATE_DRAWING);
        gl.glPushMatrix();
        float snappedX = (float) Math.floor(x);
        float snappedY = (float) Math.floor(y);
        gl.glTranslatef(snappedX, snappedY, 0.0f);
        Label label = mLabels.get(labelID);
        // 允许2D贴图
        gl.glEnable(GL10.GL_TEXTURE_2D);
        // 将纹理按照GL_TEXTURE_CROP_RECT_OES进行映射
        ((GL11) gl).glTexParameteriv(GL10.GL_TEXTURE_2D, GL11Ext.GL_TEXTURE_CROP_RECT_OES, label.mCrop, 0);
        ((GL11Ext) gl).glDrawTexiOES((int) snappedX, (int) snappedY, 0, (int) label.width, (int) label.height);
        gl.glDisable(GL10.GL_TEXTURE_2D);
        gl.glPopMatrix();
    }


    // 绘制结束
    public void endDrawing(GL10 gl) {
        checkState(STATE_DRAWING, STATE_INITIALIZED);
        gl.glDisable(GL10.GL_BLEND);
        gl.glMatrixMode(GL10.GL_PROJECTION);
        gl.glPopMatrix();
        gl.glMatrixMode(GL10.GL_MODELVIEW);
        gl.glPopMatrix();
    }

    // 检查状态是否正确
    private void checkState(int oldState, int newState) {
        if (mState != oldState) {
            throw new IllegalArgumentException("Can't call this method now.");
        }
        mState = newState;
    }

    private static class Label {
        public float width;
        public float height;
        public int[] mCrop;

        public Label(float width, float height, int cropU, int cropV, int cropW, int cropH) {
            this.width = width;
            this.height = height;
            int[] crop = new int[4];
            crop[0] = cropU;
            crop[1] = cropV;
            crop[2] = cropW;
            crop[3] = cropH;
            mCrop = crop;
        }
    }
}
