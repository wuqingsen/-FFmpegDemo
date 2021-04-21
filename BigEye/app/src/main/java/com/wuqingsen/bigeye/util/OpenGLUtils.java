package com.wuqingsen.bigeye.util;

import android.content.Context;
import android.opengl.GLES20;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * wuqingsen on 2021/4/12
 * Mailbox:807926618@qq.com
 * annotation:
 */
public class OpenGLUtils {

    public static String readRawFileFile(Context context,
                                         int rawId) {
//        StringBuilder body = new StringBuilder();
//
//        try {
//            InputStream inputStream =
//                    context.getResources().openRawResource(resourceId);
//            InputStreamReader inputStreamReader =
//                    new InputStreamReader(inputStream);
//            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
//            String nextLine;
//            while ((nextLine = bufferedReader.readLine()) != null) {
//                body.append(nextLine);
//                body.append('\n');
//            }
//        } catch (IOException e) {
//            throw new RuntimeException(
//                    "Could not open resource: " + resourceId, e);
//        } catch (Resources.NotFoundException nfe) {
//            throw new RuntimeException("Resource not found: " + resourceId, nfe);
//        }
//        return body.toString();


        InputStream is = context.getResources().openRawResource(rawId);
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String line;
        StringBuilder sb = new StringBuilder();
        try {
            while ((line = br.readLine()) != null) {
                sb.append(line);
                sb.append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            br.close();
        }catch (IOException e){
            e.printStackTrace();
        }
        return sb.toString();
    }

    public static int loadProgram(String vertexShader, String fragmentShader) {

        /**
         * 创建顶点着色器
         */
        int vShader = GLES20.glCreateShader(GLES20.GL_VERTEX_SHADER);
        GLES20.glShaderSource(vShader, vertexShader);//编译
        GLES20.glCompileShader(vShader);

        //查看配置是否成功
        int[] status = new int[1];
        GLES20.glGetShaderiv(vShader, GLES20.GL_COMPILE_STATUS, status, 0);
        if (status[0] != GLES20.GL_TRUE) {
            //失败
            throw new IllegalStateException("load vertex shader:" + GLES20.glGetShaderInfoLog(vShader));
        }

        /**
         * 片段着色器
         */
        int fShader = GLES20.glCreateShader(GLES20.GL_FRAGMENT_SHADER);
        GLES20.glShaderSource(fShader, fragmentShader);//编译
        GLES20.glCompileShader(fShader);

        //查看配置是否成功
        GLES20.glGetShaderiv(fShader, GLES20.GL_COMPILE_STATUS, status, 0);
        if (status[0] != GLES20.GL_TRUE) {
            //失败
            throw new IllegalStateException("load fragment shader:" + GLES20.glGetShaderInfoLog(fShader));
        }

        //将片段着色器和顶点着色器放到同一程序进行管理
        int mProgram = GLES20.glCreateProgram();
        //绑定顶点和片段着色器
        GLES20.glAttachShader(mProgram, vShader);
        GLES20.glAttachShader(mProgram, fShader);

        //连接到着色器程序
        GLES20.glLinkProgram(mProgram);
        //或得状态
        GLES20.glGetProgramiv(mProgram, GLES20.GL_LINK_STATUS, status, 0);
        if (status[0] != GLES20.GL_TRUE) {
            throw new IllegalStateException("link program:" + GLES20.glGetShaderInfoLog(mProgram));
        }

        GLES20.glDeleteShader(vShader);
        GLES20.glDeleteShader(fShader);
        return mProgram;
    }

    //生成纹理，在GPU开辟内存
    public static void glGenTextures(int[] textures) {
        //生成纹理
        GLES20.glGenTextures(textures.length, textures, 0);
        for (int i = 0; i < textures.length; i++) {
            //绑定纹理
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures[i]);

            //远端；参数：2D，最大/最小，马赛克/模糊（GL_NEAREST/GL_LINEAR）
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
                    GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);
            //近端
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
                    GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);

            //纹理环绕方向，就是设置X和Y,参数：2D；设置x(S)和y(T)；显示方式，重复显示
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
                    GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
                    GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_REPEAT);

            //解绑纹理
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);

        }
    }
}
