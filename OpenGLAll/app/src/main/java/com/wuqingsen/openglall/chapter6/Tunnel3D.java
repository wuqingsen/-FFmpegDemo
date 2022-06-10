package com.wuqingsen.openglall.chapter6;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.opengles.GL10;

public class Tunnel3D
{
	private float		vertices[];
	private short		faces[];
	private byte		colors[];
	private float		texture[];
	private ByteBuffer	vertices_direct;
	private ByteBuffer	texture_direct;
	private FloatBuffer	vertices_buffer;
	private ByteBuffer	faces_direct;
	private ShortBuffer	faces_buffer;
	private ByteBuffer	colors_buffer;
	private FloatBuffer	texture_buffer;
	private int			nx, ny, nv;
	private double		start_a;
	private float		start_v;
	private float		px, py;


	public Tunnel3D(int revolution, int depth)
	{
		start_a = 0;
		start_v = 0;

		// 计算顶点的数量
		nx = revolution;
		ny = depth;
		nv = nx * ny;

		// 分配数组
		colors = new byte[nv * 3];
		vertices = new float[nv * 3];
		faces = new short[((nx + 1) * (ny - 1)) << 1];
		texture = new float[nv * 2];

		// 生成数据
		genVertex();
		genFaces();
		genColors();
		genTexture();

		// 建立缓冲区对象
		buildBuffers();

		// 填充数据
		fillVertex();
		fillFaces();
		fillColors();
		fillTexture();
	}


	/* 渲染隧道 */
	public void render(GL10 gl, float depth)
	{
		// 平移
		gl.glTranslatef(-px, -py, depth);
		// 设置顶点数组
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertices_buffer);
		// 设置纹理坐标数组
		gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, texture_buffer);
		// 设置颜色数组
		gl.glColorPointer(3, GL10.GL_UNSIGNED_BYTE, 0, colors_buffer);

		int dy = 0;
		int nf = (nx + 1) << 1;
		faces_buffer.position(0);
		for (int y = 0; y < (ny - 1); y++)
		{
			gl.glDrawElements(GL10.GL_TRIANGLE_STRIP, nf, GL10.GL_UNSIGNED_SHORT, faces_buffer);
			dy += nf;
			faces_buffer.position(dy);
		}
	}


	public void nextFrame()
	{
		int i = 0;
		double delta_x = 360.0 / (double) nx;
		double delta_y = 1.0;

		double delta_z = 220.0 / (double) ny;

		for (int y = 0; y < ny; y++)
		{
			double sa = start_a + ((double) (ny - y) * delta_z);
			float sx = (float) Math.cos(Math.toRadians(sa));
			float sy = (float) Math.sin(Math.toRadians(sa));
			if (y == 0)
			{
				px = sx;
				py = sy;
			}

			for (int x = 0; x < nx; x++)
			{
				vertices[i + 0] = sx + (float) Math.sin(Math.toRadians((double) x * delta_x));
				vertices[i + 1] = sy + (float) Math.cos(Math.toRadians((double) x * delta_x));
				vertices[i + 2] = (float) -(y * delta_y);
				i += 3;
			}
		}

		start_a += 2.0;
		fillVertex();

		i = 0;
		delta_x = 1.0f / (float) nx;
		delta_y = 1.0f / (float) ny;
		for (int y = 0; y < ny; y++)
			for (int x = 0; x < nx; x++)
			{
				texture[i + 0] = (float) x * (float) delta_x;
				texture[i + 1] = start_v + ((float) y * (float) delta_y);
				i += 2;
			}
		start_v += 0.05f;
		fillTexture();
	}


	//生成隧道的顶点数据
	private void genVertex()
	{
		int i = 0;
		double delta_x = 360.0 / (double) nx;
		double delta_y = 1.0;

		for (int y = 0; y < ny; y++)
			for (int x = 0; x < nx; x++)
			{
				vertices[i + 0] = (float) Math.sin(Math.toRadians((double) x * delta_x));
				vertices[i + 1] = (float) Math.cos(Math.toRadians((double) x * delta_x));
				vertices[i + 2] = (float) -(y * delta_y);
				i += 3;
			}
	}


	// 生成索引数组
	private void genFaces()
	{
		int i = 0;
		int dy = 0;
		for (int y = 0; y < (ny - 1); y++)
		{
			for (int x = 0; x < nx; x++)
			{
				faces[i + 0] = (short) (x + dy);
				faces[i + 1] = (short) (x + dy + nx);
				i += 2;
			}
			faces[i + 0] = (short) dy;
			faces[i + 1] = (short) (dy + nx);
			i += 2;
			dy += nx;
		}
	}


	// 生成颜色数组
	private void genColors()
	{
		int i = 0;
		float sy = 1.0f;
		float dy = 1.0f / (float) ny;
		for (int y = 0; y < ny; y++)
		{
			for (int x = 0; x < nx; x++)
			{
				int r_ci = (int) (sy * 255.0f);
				byte r_cb = (byte) ((r_ci > 128) ? (r_ci - 256) : r_ci);

				int g_ci = (int) (sy * 255.0f);
				byte g_cb = (byte) ((g_ci > 128) ? (g_ci - 256) : g_ci);

				int b_ci = (int) (sy * 255.0f);
				byte b_cb = (byte) ((b_ci > 128) ? (b_ci - 256) : b_ci);

				colors[i + 0] = r_cb;
				colors[i + 1] = g_cb;
				colors[i + 2] = b_cb;
				i += 3;
			}
			sy -= dy;
		}
	}


	// 生成纹理坐标数组
	private void genTexture()
	{
		int i = 0;
		float delta_x = 1.0f / (float) nx;
		float delta_y = 1.0f / (float) ny;

		for (int y = 0; y < ny; y++)
			for (int x = 0; x < nx; x++)
			{
				texture[i + 0] = (float) x * delta_x;
				texture[i + 1] = (float) y * delta_y;
				i += 2;
			}
	}


	// 创建缓冲区对象
	private void buildBuffers()
	{
		vertices_direct = ByteBuffer.allocateDirect(vertices.length * (Float.SIZE >> 3));
		vertices_direct.order(ByteOrder.nativeOrder());
		vertices_buffer = vertices_direct.asFloatBuffer();

		faces_direct = ByteBuffer.allocateDirect(faces.length * (Short.SIZE >> 3));
		faces_direct.order(ByteOrder.nativeOrder());
		faces_buffer = faces_direct.asShortBuffer();

		colors_buffer = ByteBuffer.allocateDirect(colors.length);

		texture_direct = ByteBuffer.allocateDirect(texture.length * (Float.SIZE >> 3));
		texture_direct.order(ByteOrder.nativeOrder());
		texture_buffer = texture_direct.asFloatBuffer();
	}


	/** 设置缓冲对象数据 */
	private void fillVertex()
	{
		vertices_buffer.clear();
		vertices_buffer.put(vertices);
		vertices_buffer.position(0);
	}

	private void fillFaces()
	{
		faces_buffer.clear();
		faces_buffer.put(faces);
		faces_buffer.position(0);
	}

	private void fillColors()
	{
		colors_buffer.clear();
		colors_buffer.put(colors);
		colors_buffer.position(0);
	}

	private void fillTexture()
	{
		texture_buffer.clear();
		texture_buffer.put(texture);
		texture_buffer.position(0);
	}
}
