package com.jdev.wandroid.ui.opengl

import android.opengl.GLSurfaceView
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.IntBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * info: create by jd in 2019/12/16
 * @see:
 * @description:
 *
 */
class MyRender : GLSurfaceView.Renderer {
    //三角形顶点;
    val triangleData = floatArrayOf(
            0f, 0f, 0.0f,
            1f, 0.0f, 0.0f,
            0f, -1f, 0.0f
    )

    //顶点颜色;
    val triangleColor = intArrayOf(
            65535, 0, 0, 0
//            0, 65535, 0, 0,
//            0, 0, 65535, 0
    )

    private var triangleDataBuffer: FloatBuffer
    private var triangleColorBuffer: IntBuffer

    init {
        triangleDataBuffer = floatBufferUtil(triangleData)
        triangleColorBuffer = intBufferUtil(triangleColor)


    }

    //将int数组转换成opengl es所需的IntBuffer;
    fun intBufferUtil(array: IntArray): IntBuffer {
        var intBuffer: IntBuffer
        //初始化bytebuffer,长度为arr数组的长度*4,因为一个int占4字节;
        var qbb = ByteBuffer.allocateDirect(array.size * 4)
        //数组排列用nativeOrder
        qbb.order(ByteOrder.nativeOrder())
        intBuffer = qbb.asIntBuffer()
        intBuffer.put(array)
        intBuffer.position(0)
        return intBuffer
    }

    //将float[]转换为opengl es所需的floatBuffer;
    fun floatBufferUtil(array: FloatArray): FloatBuffer {
        var floatBuffer: FloatBuffer
        //初始化ByteBuffer,长度为arr数组的长度*4,因为一个int占4字节;
        var qbb = ByteBuffer.allocateDirect(array.size * 4)
        qbb.order(ByteOrder.nativeOrder())
        floatBuffer = qbb.asFloatBuffer()
        floatBuffer.put(array)
        floatBuffer.position(0)
        return floatBuffer
    }

    override fun onDrawFrame(gl: GL10) {
        //清除屏幕缓存和深度缓存
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT or GL10.GL_DEPTH_BUFFER_BIT)
        //启动顶点坐标数据;
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY)
        //启用顶点颜色数据;
        gl.glEnableClientState(GL10.GL_COLOR_ARRAY)
        //当前堆栈为模型堆栈;
        gl.glMatrixMode(GL10.GL_MODELVIEW)
        //------------------
        gl.glLoadIdentity()
        //移动绘图中心
        gl.glTranslatef(0f, 0f, 0f)
        //设置顶点位置数据;
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, triangleDataBuffer)
        //设置顶点颜色数据;
        gl.glColorPointer(4, GL10.GL_FIXED, 0, triangleColorBuffer)
        //根据顶点数据绘制平面图形;
        gl.glDrawArrays(GL10.GL_TRIANGLES, 0, 3)

        //绘制结束;
        gl.glFinish()
        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY)
    }

    override fun onSurfaceChanged(gl: GL10, width: Int, height: Int) {
        //设置3d视窗的大小及位置;
        gl.glViewport(0, 0, width, height)
        //将当前矩阵模式设为投影矩阵(在glFrustumf中设置zFar有效;)
//        gl.glMatrixMode(GL10.GL_PROJECTION)
        //初始化
        gl.glLoadIdentity()
        var radio = width.toFloat() / height
//        设置透视视窗的空间大小;
        gl.glFrustumf(-radio, radio, -1f, 1f, 1f, 0f)
    }

    override fun onSurfaceCreated(gl: GL10, config: EGLConfig) {
        //关闭抗抖动
        gl.glDisable(GL10.GL_DITHER)
        //系统对透视进行修正
//        gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_FASTEST)
        gl.glClearColor(0f, 255f, 0f, 0f)
        //阴影平滑模式;
        gl.glShadeModel(GL10.GL_SMOOTH)
        //启动深度测试
        gl.glEnable(GL10.GL_DEPTH_TEST)
        //深度测试类型;
        gl.glDepthFunc(GL10.GL_LEQUAL)
    }

}