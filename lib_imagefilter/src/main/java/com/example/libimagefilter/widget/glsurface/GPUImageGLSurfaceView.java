package com.example.libimagefilter.widget.glsurface;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.SurfaceHolder;

import com.example.libimagefilter.camera.CameraEngine;
import com.example.libimagefilter.widget.JdGPUDisplayView;

/**
 * info: create by jd in 2020/1/16
 * @see:
 * @description: glSurface
 *
 */
public class GPUImageGLSurfaceView extends GLSurfaceView {
    public JdGPUDisplayView.Size forceSize = null;

    public GPUImageGLSurfaceView(Context context) {
        super(context);
    }

    public GPUImageGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        super.surfaceDestroyed(holder);
        CameraEngine.releaseCamera();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (forceSize != null) {
            super.onMeasure(MeasureSpec.makeMeasureSpec(forceSize.width, MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(forceSize.height, MeasureSpec.EXACTLY));
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }
}