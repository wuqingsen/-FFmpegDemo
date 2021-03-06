package com.wuqingsen.openglmeiyan.util;

import android.content.Context;
import android.content.res.Resources;
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
         * ?????????????????????
         */
        int vShader = GLES20.glCreateShader(GLES20.GL_VERTEX_SHADER);
        GLES20.glShaderSource(vShader, vertexShader);//??????
        GLES20.glCompileShader(vShader);

        //????????????????????????
        int[] status = new int[1];
        GLES20.glGetShaderiv(vShader, GLES20.GL_COMPILE_STATUS, status, 0);
        if (status[0] != GLES20.GL_TRUE) {
            //??????
            throw new IllegalStateException("load vertex shader:" + GLES20.glGetShaderInfoLog(vShader));
        }

        /**
         * ???????????????
         */
        int fShader = GLES20.glCreateShader(GLES20.GL_FRAGMENT_SHADER);
        GLES20.glShaderSource(fShader, fragmentShader);//??????
        GLES20.glCompileShader(fShader);

        //????????????????????????
        GLES20.glGetShaderiv(fShader, GLES20.GL_COMPILE_STATUS, status, 0);
        if (status[0] != GLES20.GL_TRUE) {
            //??????
            throw new IllegalStateException("load fragment shader:" + GLES20.glGetShaderInfoLog(fShader));
        }

        //??????????????????????????????????????????????????????????????????
        int mProgram = GLES20.glCreateProgram();
        //??????????????????????????????
        GLES20.glAttachShader(mProgram, vShader);
        GLES20.glAttachShader(mProgram, fShader);

        //????????????????????????
        GLES20.glLinkProgram(mProgram);
        //????????????
        GLES20.glGetProgramiv(mProgram, GLES20.GL_LINK_STATUS, status, 0);
        if (status[0] != GLES20.GL_TRUE) {
            throw new IllegalStateException("link program:" + GLES20.glGetShaderInfoLog(mProgram));
        }

        GLES20.glDeleteShader(vShader);
        GLES20.glDeleteShader(fShader);
        return mProgram;
    }

    //??????????????????GPU????????????
    public static void glGenTextures(int[] textures) {
        //????????????
        GLES20.glGenTextures(textures.length, textures, 0);
        for (int i = 0; i < textures.length; i++) {
            //????????????
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures[i]);

            //??????????????????2D?????????/??????????????????/?????????GL_NEAREST/GL_LINEAR???
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
                    GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);
            //??????
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
                    GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);

            //?????????????????????????????????X???Y,?????????2D?????????x(S)???y(T)??????????????????????????????
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
                    GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
                    GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_REPEAT);

            //????????????
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);

        }
    }
}
