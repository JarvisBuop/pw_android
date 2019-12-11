package com.jdev.wandroid.widget

import android.content.Context
import jp.co.cyberagent.android.gpuimage.filter.*

/**
 * info: create by jd in 2019/12/11
 * @see:
 * @description: 美颜;
 *
 */
class GPUImageUglyFilter(context: Context) : GPUImageFilterGroup() {

    private var bilateralBlurFilter: GPUImageBilateralBlurFilter
    private var sobelEdgeDetectionFilter: GPUImageSobelEdgeDetectionFilter
    private var brightnessFilter: GPUImageBrightnessFilter
    private var saturationFilter: GPUImageSaturationFilter

    init {
        bilateralBlurFilter = GPUImageBilateralBlurFilter()
        sobelEdgeDetectionFilter = GPUImageSobelEdgeDetectionFilter()
        brightnessFilter = GPUImageBrightnessFilter(0.2f)
        saturationFilter = GPUImageSaturationFilter(1.0f)

        addFilter(bilateralBlurFilter)
        addFilter(sobelEdgeDetectionFilter)
        addFilter(brightnessFilter)
        addFilter(saturationFilter)
    }

    override fun onInitialized() {
        super.onInitialized()
        bilateralBlurFilter.setDistanceNormalizationFactor(4f)

    }
}