package com.wuqingsen.openglall;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;

import com.wuqingsen.openglall.chapter1.Chapter1Activity;
import com.wuqingsen.openglall.chapter2.Chapter2Activity;
import com.wuqingsen.openglall.chapter3.Chapter3Activity;
import com.wuqingsen.openglall.chapter4.Chapter4Activity;
import com.wuqingsen.openglall.chapter5.Chapter5Activity;
import com.wuqingsen.openglall.chapter6.Chapter6Activity;
import com.wuqingsen.openglall.chapter7.Chapter7Activity;
import com.wuqingsen.openglall.chapter8.Chapter8Activity;
import com.wuqingsen.openglall.chapter9.Chapter9Activity;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private String filePath = Environment.getExternalStorageDirectory() + "/" + "wqs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
//        deleteDirectory(filePath);
    }

    public void btnChapter(View view) {
        if (view.getId() == R.id.btnChapter1) {
            startActivity(new Intent(MainActivity.this, Chapter1Activity.class));
        } else if (view.getId() == R.id.btnChapter2) {
            startActivity(new Intent(MainActivity.this, Chapter2Activity.class));
        } else if (view.getId() == R.id.btnChapter3) {
            startActivity(new Intent(MainActivity.this, Chapter3Activity.class));
        } else if (view.getId() == R.id.btnChapter4) {
            startActivity(new Intent(MainActivity.this, Chapter4Activity.class));
        } else if (view.getId() == R.id.btnChapter5) {
            startActivity(new Intent(MainActivity.this, Chapter5Activity.class));
        } else if (view.getId() == R.id.btnChapter6) {
            startActivity(new Intent(MainActivity.this, Chapter6Activity.class));
        } else if (view.getId() == R.id.btnChapter7) {
            startActivity(new Intent(MainActivity.this, Chapter7Activity.class));
        } else if (view.getId() == R.id.btnChapter8) {
            startActivity(new Intent(MainActivity.this, Chapter8Activity.class));
        } else if (view.getId() == R.id.btnChapter9) {
            startActivity(new Intent(MainActivity.this, Chapter9Activity.class));
        }

    }


    /**
     * 删除文件夹以及目录下的文件
     *
     * @param filePath 被删除目录的文件路径
     * @return 目录删除成功返回true，否则返回false
     */
    public boolean deleteDirectory(String filePath) {
        boolean flag = false;
        //如果filePath不以文件分隔符结尾，自动添加文件分隔符
        if (!filePath.endsWith(File.separator)) {
            filePath = filePath + File.separator;
        }
        File dirFile = new File(filePath);
        if (!dirFile.exists() || !dirFile.isDirectory()) {
            return false;
        }
        flag = true;
        File[] files = dirFile.listFiles();
        //遍历删除文件夹下的所有文件(包括子目录)
        for (int i = 0; i < files.length; i++) {
            if (files[i].isFile()) {
                //删除子文件
                flag = deleteFile(files[i].getAbsolutePath());
                if (!flag) break;
            } else {
                //删除子目录
                flag = deleteDirectory(files[i].getAbsolutePath());
                if (!flag) break;
            }
        }
        if (!flag) return false;
        //删除当前空目录
        return dirFile.delete();
    }

    /**
     * 删除单个文件
     *
     * @param filePath 被删除文件的文件名
     * @return 文件删除成功返回true，否则返回false
     */
    public boolean deleteFile(String filePath) {
        File file = new File(filePath);
        if (file.isFile() && file.exists()) {
            return file.delete();
        }
        return false;
    }
}