package com.jdev.wandroid.ui.opengl

import android.opengl.GLES20
import android.opengl.GLES20.*
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import android.util.Log
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10


/**
 * info: create by jd in 2019/12/16
 * @see:
 * @description:
 *
 */
class MyG20Render : GLSurfaceView.Renderer {
    private var programId: Int = 0
    val NO_FILTER_VERTEX_SHADER = "" +
            "uniform mat4 u_Matrix;\n" +
            "attribute vec4 a_Position;\n" +
            " \n" +
            "void main()\n" +
            "{\n" +
            "    gl_Position = u_Matrix * a_Position;\n" +
            "}"
    val NO_FILTER_FRAGMENT_SHADER = "" +
            "precision mediump float;\n" +
            " \n" +
            "uniform vec4 u_Color;\n" +
            " \n" +
            "void main()\n" +
            "{\n" +
            "      gl_FragColor = u_Color;\n" +
            "}"


    companion object {
        val TAG = "SimpleOpenglDemoFrag"

        fun validateProgram(programObjectId: Int): Boolean {
            GLES20.glValidateProgram(programObjectId)
            var validateStatus = IntArray(1)
            GLES20.glGetProgramiv(programObjectId, GLES20.GL_VALIDATE_STATUS, validateStatus, 0)

            Log.d(TAG, "Results of validating program: " + validateStatus[0]
                    + "\nLog:" + GLES20.glGetProgramInfoLog(programObjectId))

            return validateStatus[0] != 0
        }
    }

    protected var mVertexArray = floatArrayOf(// OpenGL的坐标是[-1, 1]，这里的Vertex正好定义了一个居中的正方形
            // triangle fan x, y
            0f, 0f,
            -0.5f, -0.5f,
            0.5f, -0.5f,
            0.5f, 0.5f,
            -0.5f, 0.5f,
            -0.5f, -0.5f)

    val triangleData = floatArrayOf(
            0f, 0f,
            -1f, -1f,
            1f, 0f
    )

    protected var mVertexBuffer: FloatBuffer? = null
    protected var mVertexTestBuffer: FloatBuffer? = null
    //投影矩阵;
    protected var mProjectionMatrix = FloatArray(16)
    //view变化矩阵
    protected var mViewMatrix = FloatArray(16)

    protected var mProjectionViewMatrix = FloatArray(16)

    protected var uMatrixLocation: Int = 0
    protected var aPositionLocation: Int = 0
    protected var uColorLocation: Int = 0

    protected val sFovy = 90 // 透视投影的视角，90度
    protected val sZ = 2f

    init {
        mVertexBuffer = MyRender.floatBufferUtil(mVertexArray)
        mVertexTestBuffer = MyRender.floatBufferUtil(triangleData)
    }

    override fun onDrawFrame(gl: GL10) {
        glClear(GL_COLOR_BUFFER_BIT)

        // 启用这个Program
        glUseProgram(programId)


        // 填充数据 正方形;
        glUniform4f(uColorLocation, 1f, 0f, 0f, 1f)
        mVertexBuffer!!.position(0)
        glVertexAttribPointer(aPositionLocation, 2, GL_FLOAT, false, 0, mVertexBuffer)
        glEnableVertexAttribArray(aPositionLocation)
        glDrawArrays(GL_TRIANGLE_FAN, 0, 6)

        //todo 三角形;
        glUniform4f(uColorLocation, 0f, 0f, 1f, 1f)
        mVertexTestBuffer!!.position(0)
        glVertexAttribPointer(aPositionLocation, 2, GL_FLOAT, false, 0, mVertexTestBuffer)
        glEnableVertexAttribArray(aPositionLocation)
        glDrawArrays(GL_TRIANGLE_FAN, 0, 3)

    }

    override fun onSurfaceChanged(gl: GL10, width: Int, height: Int) {
        glViewport(0, 0, width, height)
        // 正交变换，只考虑竖屏的情况
        val rate = height * 1.0f / width
//        Matrix.orthoM(mProjectionMatrix, 0, -1f, 1f, -rate, rate, -1f, 1f) // 正交变换，防止界面拉伸
//        glUniformMatrix4fv(uMatrixLocation, 1, false, mProjectionMatrix, 0)


        // 旋转矩阵;
        val screenAspect = width * 1.0f / height
        Matrix.perspectiveM(mProjectionMatrix, 0, sFovy.toFloat(), screenAspect, 1f, 10f)

        Matrix.setIdentityM(mViewMatrix, 0)
        Matrix.translateM(mViewMatrix,0,0f,0f,-sZ)
        Matrix.rotateM(mViewMatrix, 0, -60f, 1f, 0f, 0f)
        Matrix.multiplyMM(mProjectionViewMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0)

        glUniformMatrix4fv(uMatrixLocation, 1, false, mProjectionViewMatrix, 0)
    }

    override fun onSurfaceCreated(gl: GL10, config: EGLConfig) {
        glClearColor(1.0f, 1.0f, 1.0f, 1.0f)

        val vertexShaderStr = NO_FILTER_VERTEX_SHADER
        val fragmentShaderStr = NO_FILTER_FRAGMENT_SHADER
        // 创建Shader
        val vertexShaderId = glCreateShader(GL_VERTEX_SHADER)
        val fragmentShaderId = glCreateShader(GL_FRAGMENT_SHADER)
        glShaderSource(vertexShaderId, vertexShaderStr)
        glShaderSource(fragmentShaderId, fragmentShaderStr)
        glCompileShader(vertexShaderId)
        glCompileShader(fragmentShaderId)
        val compileStatus = IntArray(1)
        glGetShaderiv(fragmentShaderId, GL_COMPILE_STATUS,
                compileStatus, 0)
        // 创建Program
        programId = glCreateProgram()
        glAttachShader(programId, vertexShaderId)
        glAttachShader(programId, fragmentShaderId)
        glLinkProgram(programId)

        // 启用这个Program
        glUseProgram(programId)
        // 找到需要赋值的变量
        uMatrixLocation = glGetUniformLocation(programId, "u_Matrix")
        aPositionLocation = glGetAttribLocation(programId, "a_Position")
        uColorLocation = glGetUniformLocation(programId, "u_Color")

    }

}