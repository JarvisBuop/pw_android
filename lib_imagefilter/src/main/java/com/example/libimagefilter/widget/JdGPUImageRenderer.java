/*
 * Copyright (C) 2018 CyberAgent, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.libimagefilter.widget;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;


import com.example.libimagefilter.filter.base.gpuimage.GPUImageFilter;
import com.example.libimagefilter.utils.OpenGlUtils;
import com.example.libimagefilter.utils.Rotation;
import com.example.libimagefilter.utils.TextureRotationUtil;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.LinkedList;
import java.util.Queue;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;


public class JdGPUImageRenderer implements GLSurfaceView.Renderer/*, GLTextureView.Renderer*/ {
    //滤镜;
    protected GPUImageFilter filter;

    //处理输入源时的等待锁;
    final Object surfaceChangedWaiter = new Object();

    //纹理id;
    protected int glTextureId = OpenGlUtils.NO_TEXTURE;
    //顶点及纹理数据;
    protected final FloatBuffer glCubeBuffer;
    protected final FloatBuffer glTextureBuffer;

    //surfaceview 大小 | bitmap大小;
    protected int outputWidth;
    protected int outputHeight;
    protected int imageWidth;
    protected int imageHeight;
    protected int addedPadding;

    //执行队列;
    protected final Queue<Runnable> runOnDraw;
    protected final Queue<Runnable> runOnDrawEnd;

    //调节参数
    protected Rotation rotation;
    protected boolean flipHorizontal;
    protected boolean flipVertical;
    protected JdGPUImage.ScaleType scaleType = JdGPUImage.ScaleType.CENTER_CROP;

    private float backgroundRed = 0;
    private float backgroundGreen = 0;
    private float backgroundBlue = 0;

    public JdGPUImageRenderer(GPUImageFilter filter) {
        this.filter = filter;
        runOnDraw = new LinkedList<>();
        runOnDrawEnd = new LinkedList<>();

        glCubeBuffer = ByteBuffer.allocateDirect(TextureRotationUtil.CUBE.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        glCubeBuffer.put(TextureRotationUtil.CUBE).position(0);

        glTextureBuffer = ByteBuffer.allocateDirect(TextureRotationUtil.TEXTURE_NO_ROTATION.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        setRotation(Rotation.NORMAL, false, false);
    }

    @Override
    public void onSurfaceCreated(final GL10 unused, final EGLConfig config) {
        GLES20.glClearColor(backgroundRed, backgroundGreen, backgroundBlue, 1);
        GLES20.glDisable(GLES20.GL_DEPTH_TEST);
        onSurfaceCreatedExcute(unused, config);
    }

    protected void onSurfaceCreatedExcute(GL10 unused, EGLConfig config) {
        if (filter != null) {
            filter.ifNeedInit();
        }
    }

    @Override
    public void onSurfaceChanged(final GL10 gl, final int width, final int height) {
        GLES20.glViewport(0, 0, width, height);
        outputWidth = width;
        outputHeight = height;
        GLES20.glUseProgram(filter.getProgram());
        onFilterChanged();
        adjustSize();
        synchronized (surfaceChangedWaiter) {
            surfaceChangedWaiter.notifyAll();
        }
    }

    @Override
    public void onDrawFrame(final GL10 gl) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        runAll(runOnDraw);
        onDrawFrameExcute(gl);
        runAll(runOnDrawEnd);
    }

    protected void onDrawFrameExcute(GL10 gl) {
        if (filter != null) {
            filter.onDrawFrame(glTextureId, glCubeBuffer, glTextureBuffer);
        }
    }

    protected void onFilterChanged() {
        if (filter != null) {
            filter.onDisplaySizeChanged(outputWidth, outputHeight);
            filter.onInputSizeChanged(imageWidth, imageHeight);
        }
    }

    /**
     * Sets the background color
     *
     * @param red   red color value
     * @param green green color value
     * @param blue  red color value
     */
    public void setBackgroundColor(float red, float green, float blue) {
        backgroundRed = red;
        backgroundGreen = green;
        backgroundBlue = blue;
    }

    private void runAll(Queue<Runnable> queue) {
        synchronized (queue) {
            while (!queue.isEmpty()) {
                queue.poll().run();
            }
        }
    }


    public void setFilter(final GPUImageFilter filter) {
        runOnDraw(new Runnable() {

            @Override
            public void run() {
                final GPUImageFilter oldFilter = JdGPUImageRenderer.this.filter;
                if (oldFilter != null) {
                    oldFilter.destroy();
                }
                JdGPUImageRenderer.this.filter = filter;
                if (JdGPUImageRenderer.this.filter == null) {
                    JdGPUImageRenderer.this.filter = new GPUImageFilter();
                }
                JdGPUImageRenderer.this.filter.ifNeedInit();
                GLES20.glUseProgram(JdGPUImageRenderer.this.filter.getProgram());
                onFilterChanged();
            }
        });
    }

    public void deleteImage() {
        runOnDraw(new Runnable() {

            @Override
            public void run() {
                GLES20.glDeleteTextures(1, new int[]{
                        glTextureId
                }, 0);
                glTextureId = OpenGlUtils.NO_TEXTURE;
            }
        });
    }

    public void setImageBitmap(final Bitmap bitmap) {
        setImageBitmap(bitmap, true);
    }

    public void setImageBitmap(final Bitmap bitmap, final boolean recycle) {
        if (bitmap == null) {
            return;
        }

        runOnDraw(new Runnable() {

            @Override
            public void run() {
                Bitmap resizedBitmap = null;
                if (bitmap.getWidth() % 2 == 1) {
                    resizedBitmap = Bitmap.createBitmap(bitmap.getWidth() + 1, bitmap.getHeight(),
                            Bitmap.Config.ARGB_8888);
                    Canvas can = new Canvas(resizedBitmap);
                    can.drawARGB(0x00, 0x00, 0x00, 0x00);
                    can.drawBitmap(bitmap, 0, 0, null);
                    addedPadding = 1;
                } else {
                    addedPadding = 0;
                }

                glTextureId = OpenGlUtils.loadTexture(
                        resizedBitmap != null ? resizedBitmap : bitmap, glTextureId, recycle);
                if (resizedBitmap != null) {
                    resizedBitmap.recycle();
                }
                imageWidth = bitmap.getWidth();
                imageHeight = bitmap.getHeight();
                adjustSize();
            }
        });
    }

    public void setScaleType(JdGPUImage.ScaleType scaleType) {
        this.scaleType = scaleType;
    }

    protected int getFrameWidth() {
        return outputWidth;
    }

    protected int getFrameHeight() {
        return outputHeight;
    }

    protected void adjustSize() {
        adjustSize(rotation, flipHorizontal, flipVertical);
    }

    protected void adjustSize(int rotation, boolean flipHorizontal, boolean flipVertical) {
        adjustSize(Rotation.fromInt(rotation), flipHorizontal, flipVertical);
    }

    protected void adjustSize(Rotation rotation, boolean flipHorizontal, boolean flipVertical) {
        this.flipHorizontal = flipHorizontal;
        this.flipVertical = flipVertical;
        this.rotation = rotation;

        float outputWidth = this.outputWidth;
        float outputHeight = this.outputHeight;
        if (rotation == Rotation.ROTATION_270 || rotation == Rotation.ROTATION_90) {
            outputWidth = this.outputHeight;
            outputHeight = this.outputWidth;
        }

        float[] textureCords = TextureRotationUtil.getRotation(rotation, flipHorizontal, flipVertical);
        float[] cube = TextureRotationUtil.CUBE;
        float ratio1 = (float) outputWidth / imageWidth;
        float ratio2 = (float) outputHeight / imageHeight;
        float ratioMax = Math.max(ratio1, ratio2);
        int imageWidthNew = Math.round(imageWidth * ratioMax);
        int imageHeightNew = Math.round(imageHeight * ratioMax);

        float ratioWidth = imageWidthNew / (float) outputWidth;
        float ratioHeight = imageHeightNew / (float) outputHeight;

        if (scaleType == JdGPUImage.ScaleType.CENTER_INSIDE) {
            cube = new float[]{
                    TextureRotationUtil.CUBE[0] / ratioHeight, TextureRotationUtil.CUBE[1] / ratioWidth,
                    TextureRotationUtil.CUBE[2] / ratioHeight, TextureRotationUtil.CUBE[3] / ratioWidth,
                    TextureRotationUtil.CUBE[4] / ratioHeight, TextureRotationUtil.CUBE[5] / ratioWidth,
                    TextureRotationUtil.CUBE[6] / ratioHeight, TextureRotationUtil.CUBE[7] / ratioWidth,
            };
        } else if (scaleType == JdGPUImage.ScaleType.FIT_XY) {

        } else if (scaleType == JdGPUImage.ScaleType.CENTER_CROP) {
            float distHorizontal = (1 - 1 / ratioWidth) / 2;
            float distVertical = (1 - 1 / ratioHeight) / 2;
            textureCords = new float[]{
                    addDistance(textureCords[0], distVertical), addDistance(textureCords[1], distHorizontal),
                    addDistance(textureCords[2], distVertical), addDistance(textureCords[3], distHorizontal),
                    addDistance(textureCords[4], distVertical), addDistance(textureCords[5], distHorizontal),
                    addDistance(textureCords[6], distVertical), addDistance(textureCords[7], distHorizontal),
            };
        }

        glCubeBuffer.clear();
        glCubeBuffer.put(cube).position(0);
        glTextureBuffer.clear();
        glTextureBuffer.put(textureCords).position(0);
    }

    private float addDistance(float coordinate, float distance) {
        return coordinate == 0.0f ? distance : 1 - distance;
    }

    protected void setRotation(int rotation) {
        adjustSize(Rotation.fromInt(rotation), flipHorizontal, flipVertical);
    }

    public void setRotation(final Rotation rotation) {
        adjustSize(rotation, flipHorizontal, flipVertical);
    }

    public void setRotation(final Rotation rotation,
                            final boolean flipHorizontal, final boolean flipVertical) {
        adjustSize(rotation, flipHorizontal, flipVertical);
    }

    public Rotation getRotation() {
        return rotation;
    }

    public boolean isFlippedHorizontally() {
        return flipHorizontal;
    }

    public boolean isFlippedVertically() {
        return flipVertical;
    }

    public void runOnDraw(final Runnable runnable) {
        synchronized (runOnDraw) {
            runOnDraw.add(runnable);
        }
    }

    public void runOnDrawEnd(final Runnable runnable) {
        synchronized (runOnDrawEnd) {
            runOnDrawEnd.add(runnable);
        }
    }
}
