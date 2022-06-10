package com.wuqingsen.openglall.chapter9;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;

public class Chapter9Activity extends Activity
{
	private GLSurfaceView mGLSurfaceView;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		// 实例化GLSurfaceView
		mGLSurfaceView = new GLSurfaceView(this);
		
		// 设置渲染器
		mGLSurfaceView.setRenderer(new GLRender9(this));
		
		setContentView(mGLSurfaceView);
	}
	
    @Override
    protected void onResume()
	{
		super.onResume();
		mGLSurfaceView.onResume();
	}


	@Override
	protected void onPause()
	{
		super.onPause();
		mGLSurfaceView.onPause();
	}
}