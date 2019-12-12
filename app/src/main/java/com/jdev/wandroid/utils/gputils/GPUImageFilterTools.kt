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

package com.jdev.wandroid.utils.gputils

import android.app.AlertDialog
import android.content.Context
import android.opengl.Matrix
import com.jdev.wandroid.widget.GPUImageUglyFilter
import com.jdev.wandroid.widget.GpuImageBeautyFilter
import com.seu.magicfilter.filter.base.gpuimage.GPUImageFilter
import com.seu.magicfilter.filter.helper.MagicFilterFactory
import com.seu.magicfilter.filter.helper.MagicFilterType
import com.seu.magicfilter.filter.origin.*
import java.util.*

object GPUImageFilterTools {
    /**
     * 测试使用;
     */
    fun showCustomFilterDialog(context: Context,
                               name: String = "",
                               listener: (filter: GPUImageFilter, filterName: String) -> Unit) {

        val filters = FilterList().apply {
            addFilter(MagicFilterType.CUSTOM_丑颜.name, MagicFilterType.CUSTOM_丑颜)
            addFilter(MagicFilterType.CUSTOM_美颜.name, MagicFilterType.CUSTOM_美颜)
        }

        var index = filters.names.indexOf(name)
        AlertDialog.Builder(context)
                .setTitle("Choose a custom filter")
                .setSingleChoiceItems(filters.names.toTypedArray(), index) { dialog, item ->
                    dialog.dismiss()
                    listener(createCustomFilterForType(context, filters.filters[item]), filters.names[item])
                }
                .create().show()
    }

    fun createCustomFilterForType(context: Context, type: MagicFilterType): GPUImageFilter {
        return when (type) {
            MagicFilterType.CUSTOM_丑颜 -> {
                GPUImageUglyFilter(context)
            }
            MagicFilterType.CUSTOM_美颜 -> {
                GpuImageBeautyFilter()
            }

//            com.seu.magicfilter.filter.helper.MagicFilterType.CUSTOM_TEST1 ->{
//
//            }
            else -> {
                GpuImageBeautyFilter()
            }
        }
    }

    fun createCustomAdjusterByFilter(filter: GPUImageFilter?): FilterAdjuster.Adjuster<out GPUImageFilter>? {
        return when (filter) {
            else -> {
                null
            }
        }
    }


    /**
     * ==============================================
     */
    fun showDialog(
            context: Context,
            name: String = "",
            listener: (filter: GPUImageFilter?, filterName: String) -> Unit
    ) {
        val filters = initFilterListObj()
        var index = filters.names.indexOf(name)

        val builder = AlertDialog.Builder(context)
        builder.setTitle("Choose a filter")
                .setSingleChoiceItems(filters.names.toTypedArray(), index) { dialog, item ->
                    dialog.dismiss()
                    listener(MagicFilterFactory.getFilterByType(context,filters.filters[item]), filters.names[item])
                }
                .create().show()
    }

    fun initFilterListObj(): FilterList {
        val filters = FilterList().apply {
            addFilter("Contrast", MagicFilterType.CONTRAST)
            addFilter("Invert", MagicFilterType.INVERT)
            addFilter("Pixelation", MagicFilterType.PIXELATION)
            addFilter("Hue", MagicFilterType.HUE)
            addFilter("Gamma", MagicFilterType.GAMMA)
            addFilter("Brightness", MagicFilterType.BRIGHTNESS)
            addFilter("Sepia", MagicFilterType.SEPIA)
            addFilter("Grayscale", MagicFilterType.GRAYSCALE)
            addFilter("Sharpness", MagicFilterType.SHARPEN)
            addFilter("Sobel Edge Detection", MagicFilterType.SOBEL_EDGE_DETECTION)
            addFilter("Threshold Edge Detection", MagicFilterType.THRESHOLD_EDGE_DETECTION)
            addFilter("3x3 Convolution", MagicFilterType.THREE_X_THREE_CONVOLUTION)
            addFilter("Emboss", MagicFilterType.EMBOSS)
            addFilter("Posterize", MagicFilterType.POSTERIZE)
            addFilter("Grouped filters", MagicFilterType.FILTER_GROUP)
            addFilter("Saturation", MagicFilterType.SATURATION)
            addFilter("Exposure", MagicFilterType.EXPOSURE)
            addFilter("Highlight Shadow", MagicFilterType.HIGHLIGHT_SHADOW)
            addFilter("Monochrome", MagicFilterType.MONOCHROME)
            addFilter("Opacity", MagicFilterType.OPACITY)
            addFilter("RGB", MagicFilterType.RGB)
            addFilter("White Balance", MagicFilterType.WHITE_BALANCE)
            addFilter("Vignette", MagicFilterType.VIGNETTE)
            addFilter("ToneCurve", MagicFilterType.TONE_CURVE)

            addFilter("Luminance", MagicFilterType.LUMINANCE)
            addFilter("Luminance Threshold", MagicFilterType.LUMINANCE_THRESHSOLD)

            addFilter("Blend (Difference)", MagicFilterType.BLEND_DIFFERENCE)
            addFilter("Blend (Source Over)", MagicFilterType.BLEND_SOURCE_OVER)
            addFilter("Blend (Color Burn)", MagicFilterType.BLEND_COLOR_BURN)
            addFilter("Blend (Color Dodge)", MagicFilterType.BLEND_COLOR_DODGE)
            addFilter("Blend (Darken)", MagicFilterType.BLEND_DARKEN)
            addFilter("Blend (Dissolve)", MagicFilterType.BLEND_DISSOLVE)
            addFilter("Blend (Exclusion)", MagicFilterType.BLEND_EXCLUSION)
            addFilter("Blend (Hard Light)", MagicFilterType.BLEND_HARD_LIGHT)
            addFilter("Blend (Lighten)", MagicFilterType.BLEND_LIGHTEN)
            addFilter("Blend (Add)", MagicFilterType.BLEND_ADD)
            addFilter("Blend (Divide)", MagicFilterType.BLEND_DIVIDE)
            addFilter("Blend (Multiply)", MagicFilterType.BLEND_MULTIPLY)
            addFilter("Blend (Overlay)", MagicFilterType.BLEND_OVERLAY)
            addFilter("Blend (Screen)", MagicFilterType.BLEND_SCREEN)
            addFilter("Blend (Alpha)", MagicFilterType.BLEND_ALPHA)
            addFilter("Blend (Color)", MagicFilterType.BLEND_COLOR)
            addFilter("Blend (Hue)", MagicFilterType.BLEND_HUE)
            addFilter("Blend (Saturation)", MagicFilterType.BLEND_SATURATION)
            addFilter("Blend (Luminosity)", MagicFilterType.BLEND_LUMINOSITY)
            addFilter("Blend (Linear Burn)", MagicFilterType.BLEND_LINEAR_BURN)
            addFilter("Blend (Soft Light)", MagicFilterType.BLEND_SOFT_LIGHT)
            addFilter("Blend (Subtract)", MagicFilterType.BLEND_SUBTRACT)
            addFilter("Blend (Chroma Key)", MagicFilterType.BLEND_CHROMA_KEY)
            addFilter("Blend (Normal)", MagicFilterType.BLEND_NORMAL)

            addFilter("Lookup (Amatorka)", MagicFilterType.LOOKUP_AMATORKA)
            addFilter("Gaussian Blur", MagicFilterType.GAUSSIAN_BLUR)
            addFilter("Crosshatch", MagicFilterType.CROSSHATCH)

            addFilter("Box Blur", MagicFilterType.BOX_BLUR)
            addFilter("CGA Color Space", MagicFilterType.CGA_COLORSPACE)
            addFilter("Dilation", MagicFilterType.DILATION)
            addFilter("Kuwahara", MagicFilterType.KUWAHARA)
            addFilter("RGB Dilation", MagicFilterType.RGB_DILATION)
            addFilter("Sketch", MagicFilterType.SKETCH)
            addFilter("Toon", MagicFilterType.TOON)
            addFilter("Smooth Toon", MagicFilterType.SMOOTH_TOON)
            addFilter("Halftone", MagicFilterType.HALFTONE)

            addFilter("Bulge Distortion", MagicFilterType.BULGE_DISTORTION)
            addFilter("Glass Sphere", MagicFilterType.GLASS_SPHERE)
            addFilter("Haze", MagicFilterType.HAZE)
            addFilter("Laplacian", MagicFilterType.LAPLACIAN)
            addFilter("Non Maximum Suppression", MagicFilterType.NON_MAXIMUM_SUPPRESSION)
            addFilter("Sphere Refraction", MagicFilterType.SPHERE_REFRACTION)
            addFilter("Swirl", MagicFilterType.SWIRL)
            addFilter("Weak Pixel Inclusion", MagicFilterType.WEAK_PIXEL_INCLUSION)
            addFilter("False Color", MagicFilterType.FALSE_COLOR)

            addFilter("Color Balance", MagicFilterType.COLOR_BALANCE)

            addFilter("Levels Min (Mid Adjust)", MagicFilterType.LEVELS_FILTER_MIN)

            addFilter("Bilateral Blur", MagicFilterType.BILATERAL_BLUR)

            addFilter("Zoom Blur", MagicFilterType.ZOOM_BLUR)

            addFilter("Transform (2-D)", MagicFilterType.TRANSFORM2D)

            addFilter("Solarize", MagicFilterType.SOLARIZE)

            addFilter("Vibrance", MagicFilterType.VIBRANCE)
        }
        return filters
    }

    class FilterList {
        val names: MutableList<String> = LinkedList()
        val filters: MutableList<MagicFilterType> = LinkedList()

        fun addFilter(name: String, filter: MagicFilterType) {
            names.add(name)
            filters.add(filter)
        }
    }

    class FilterAdjuster(filter: GPUImageFilter?) {
        private val adjuster: Adjuster<out GPUImageFilter>?

        init {
            adjuster = when (filter) {
                is GPUImageSharpenFilter -> SharpnessAdjuster(filter)
                is GPUImageSepiaToneFilter -> SepiaAdjuster(filter)
                is GPUImageContrastFilter -> ContrastAdjuster(filter)
                is GPUImageGammaFilter -> GammaAdjuster(filter)
                is GPUImageBrightnessFilter -> BrightnessAdjuster(filter)
                is GPUImageSobelEdgeDetectionFilter -> SobelAdjuster(filter)
                is GPUImageThresholdEdgeDetectionFilter -> ThresholdAdjuster(filter)
                is GPUImage3x3ConvolutionFilter -> ThreeXThreeConvolutionAjuster(filter)
                is GPUImageEmbossFilter -> EmbossAdjuster(filter)
                is GPUImage3x3TextureSamplingFilter -> GPU3x3TextureAdjuster(filter)
                is GPUImageHueFilter -> HueAdjuster(filter)
                is GPUImagePosterizeFilter -> PosterizeAdjuster(filter)
                is GPUImagePixelationFilter -> PixelationAdjuster(filter)
                is GPUImageSaturationFilter -> SaturationAdjuster(filter)
                is GPUImageExposureFilter -> ExposureAdjuster(filter)
                is GPUImageHighlightShadowFilter -> HighlightShadowAdjuster(filter)
                is GPUImageMonochromeFilter -> MonochromeAdjuster(filter)
                is GPUImageOpacityFilter -> OpacityAdjuster(filter)
                is GPUImageRGBFilter -> RGBAdjuster(filter)
                is GPUImageWhiteBalanceFilter -> WhiteBalanceAdjuster(filter)
                is GPUImageVignetteFilter -> VignetteAdjuster(filter)
                is GPUImageLuminanceThresholdFilter -> LuminanceThresholdAdjuster(filter)
                is GPUImageDissolveBlendFilter -> DissolveBlendAdjuster(filter)
                is GPUImageGaussianBlurFilter -> GaussianBlurAdjuster(filter)
                is GPUImageCrosshatchFilter -> CrosshatchBlurAdjuster(filter)
                is GPUImageBulgeDistortionFilter -> BulgeDistortionAdjuster(filter)
                is GPUImageGlassSphereFilter -> GlassSphereAdjuster(filter)
                is GPUImageHazeFilter -> HazeAdjuster(filter)
                is GPUImageSphereRefractionFilter -> SphereRefractionAdjuster(filter)
                is GPUImageSwirlFilter -> SwirlAdjuster(filter)
                is GPUImageColorBalanceFilter -> ColorBalanceAdjuster(filter)
                is GPUImageLevelsFilter -> LevelsMinMidAdjuster(filter)
                is GPUImageBilateralBlurFilter -> BilateralAdjuster(filter)
                is GPUImageTransformFilter -> RotateAdjuster(filter)
                is GPUImageSolarizeFilter -> SolarizeAdjuster(filter)
                is GPUImageVibranceFilter -> VibranceAdjuster(filter)
                else -> createCustomAdjusterByFilter(filter)
            }
        }

        fun canAdjust(): Boolean {
            return adjuster != null
        }

        fun adjust(percentage: Int) {
            adjuster?.adjust(percentage)
        }

        abstract inner class Adjuster<T : GPUImageFilter>(protected val filter: T) {

            abstract fun adjust(percentage: Int)

            protected fun range(percentage: Int, start: Float, end: Float): Float {
                return (end - start) * percentage / 100.0f + start
            }

            protected fun range(percentage: Int, start: Int, end: Int): Int {
                return (end - start) * percentage / 100 + start
            }
        }

        private inner class SharpnessAdjuster(filter: GPUImageSharpenFilter) :
                Adjuster<GPUImageSharpenFilter>(filter) {
            override fun adjust(percentage: Int) {
                filter.setSharpness(range(percentage, -4.0f, 4.0f))
            }
        }

        private inner class PixelationAdjuster(filter: GPUImagePixelationFilter) :
                Adjuster<GPUImagePixelationFilter>(filter) {
            override fun adjust(percentage: Int) {
                filter.setPixel(range(percentage, 1.0f, 100.0f))
            }
        }

        private inner class HueAdjuster(filter: GPUImageHueFilter) :
                Adjuster<GPUImageHueFilter>(filter) {
            override fun adjust(percentage: Int) {
                filter.setHue(range(percentage, 0.0f, 360.0f))
            }
        }

        private inner class ContrastAdjuster(filter: GPUImageContrastFilter) :
                Adjuster<GPUImageContrastFilter>(filter) {
            override fun adjust(percentage: Int) {
                filter.setContrast(range(percentage, 0.0f, 2.0f))
            }
        }

        private inner class GammaAdjuster(filter: GPUImageGammaFilter) :
                Adjuster<GPUImageGammaFilter>(filter) {
            override fun adjust(percentage: Int) {
                filter.setGamma(range(percentage, 0.0f, 3.0f))
            }
        }

        private inner class BrightnessAdjuster(filter: GPUImageBrightnessFilter) :
                Adjuster<GPUImageBrightnessFilter>(filter) {
            override fun adjust(percentage: Int) {
                filter.setBrightness(range(percentage, -1.0f, 1.0f))
            }
        }

        private inner class SepiaAdjuster(filter: GPUImageSepiaToneFilter) :
                Adjuster<GPUImageSepiaToneFilter>(filter) {
            override fun adjust(percentage: Int) {
                filter.setIntensity(range(percentage, 0.0f, 2.0f))
            }
        }

        private inner class SobelAdjuster(filter: GPUImageSobelEdgeDetectionFilter) :
                Adjuster<GPUImageSobelEdgeDetectionFilter>(filter) {
            override fun adjust(percentage: Int) {
                filter.setLineSize(range(percentage, 0.0f, 5.0f))
            }
        }

        private inner class ThresholdAdjuster(filter: GPUImageThresholdEdgeDetectionFilter) :
                Adjuster<GPUImageThresholdEdgeDetectionFilter>(filter) {
            override fun adjust(percentage: Int) {
                filter.setLineSize(range(percentage, 0.0f, 5.0f))
                filter.setThreshold(0.9f)
            }
        }

        private inner class ThreeXThreeConvolutionAjuster(filter: GPUImage3x3ConvolutionFilter) :
                Adjuster<GPUImage3x3ConvolutionFilter>(filter) {
            override fun adjust(percentage: Int) {
                filter.setConvolutionKernel(
                        floatArrayOf(-1.0f, 0.0f, 1.0f, -2.0f, 0.0f, 2.0f, -1.0f, 0.0f, 1.0f)
                )
            }
        }

        private inner class EmbossAdjuster(filter: GPUImageEmbossFilter) :
                Adjuster<GPUImageEmbossFilter>(filter) {
            override fun adjust(percentage: Int) {
                filter.intensity = range(percentage, 0.0f, 4.0f)
            }
        }

        private inner class PosterizeAdjuster(filter: GPUImagePosterizeFilter) :
                Adjuster<GPUImagePosterizeFilter>(filter) {
            override fun adjust(percentage: Int) {
                // In theorie to 256, but only first 50 are interesting
                filter.setColorLevels(range(percentage, 1, 50))
            }
        }

        private inner class GPU3x3TextureAdjuster(filter: GPUImage3x3TextureSamplingFilter) :
                Adjuster<GPUImage3x3TextureSamplingFilter>(filter) {
            override fun adjust(percentage: Int) {
                filter.setLineSize(range(percentage, 0.0f, 5.0f))
            }
        }

        private inner class SaturationAdjuster(filter: GPUImageSaturationFilter) :
                Adjuster<GPUImageSaturationFilter>(filter) {
            override fun adjust(percentage: Int) {
                filter.setSaturation(range(percentage, 0.0f, 2.0f))
            }
        }

        private inner class ExposureAdjuster(filter: GPUImageExposureFilter) :
                Adjuster<GPUImageExposureFilter>(filter) {
            override fun adjust(percentage: Int) {
                filter.setExposure(range(percentage, -10.0f, 10.0f))
            }
        }

        private inner class HighlightShadowAdjuster(filter: GPUImageHighlightShadowFilter) :
                Adjuster<GPUImageHighlightShadowFilter>(filter) {
            override fun adjust(percentage: Int) {
                filter.setShadows(range(percentage, 0.0f, 1.0f))
                filter.setHighlights(range(percentage, 0.0f, 1.0f))
            }
        }

        private inner class MonochromeAdjuster(filter: GPUImageMonochromeFilter) :
                Adjuster<GPUImageMonochromeFilter>(filter) {
            override fun adjust(percentage: Int) {
                filter.setIntensity(range(percentage, 0.0f, 1.0f))
            }
        }

        private inner class OpacityAdjuster(filter: GPUImageOpacityFilter) :
                Adjuster<GPUImageOpacityFilter>(filter) {
            override fun adjust(percentage: Int) {
                filter.setOpacity(range(percentage, 0.0f, 1.0f))
            }
        }

        private inner class RGBAdjuster(filter: GPUImageRGBFilter) :
                Adjuster<GPUImageRGBFilter>(filter) {
            override fun adjust(percentage: Int) {
                filter.setRed(range(percentage, 0.0f, 1.0f))
            }
        }

        private inner class WhiteBalanceAdjuster(filter: GPUImageWhiteBalanceFilter) :
                Adjuster<GPUImageWhiteBalanceFilter>(filter) {
            override fun adjust(percentage: Int) {
                filter.setTemperature(range(percentage, 2000.0f, 8000.0f))
            }
        }

        private inner class VignetteAdjuster(filter: GPUImageVignetteFilter) :
                Adjuster<GPUImageVignetteFilter>(filter) {
            override fun adjust(percentage: Int) {
                filter.setVignetteStart(range(percentage, 0.0f, 1.0f))
            }
        }

        private inner class LuminanceThresholdAdjuster(filter: GPUImageLuminanceThresholdFilter) :
                Adjuster<GPUImageLuminanceThresholdFilter>(filter) {
            override fun adjust(percentage: Int) {
                filter.setThreshold(range(percentage, 0.0f, 1.0f))
            }
        }

        private inner class DissolveBlendAdjuster(filter: GPUImageDissolveBlendFilter) :
                Adjuster<GPUImageDissolveBlendFilter>(filter) {
            override fun adjust(percentage: Int) {
                filter.setMix(range(percentage, 0.0f, 1.0f))
            }
        }

        private inner class GaussianBlurAdjuster(filter: GPUImageGaussianBlurFilter) :
                Adjuster<GPUImageGaussianBlurFilter>(filter) {
            override fun adjust(percentage: Int) {
                filter.setBlurSize(range(percentage, 0.0f, 1.0f))
            }
        }

        private inner class CrosshatchBlurAdjuster(filter: GPUImageCrosshatchFilter) :
                Adjuster<GPUImageCrosshatchFilter>(filter) {
            override fun adjust(percentage: Int) {
                filter.setCrossHatchSpacing(range(percentage, 0.0f, 0.06f))
                filter.setLineWidth(range(percentage, 0.0f, 0.006f))
            }
        }

        private inner class BulgeDistortionAdjuster(filter: GPUImageBulgeDistortionFilter) :
                Adjuster<GPUImageBulgeDistortionFilter>(filter) {
            override fun adjust(percentage: Int) {
                filter.setRadius(range(percentage, 0.0f, 1.0f))
                filter.setScale(range(percentage, -1.0f, 1.0f))
            }
        }

        private inner class GlassSphereAdjuster(filter: GPUImageGlassSphereFilter) :
                Adjuster<GPUImageGlassSphereFilter>(filter) {
            override fun adjust(percentage: Int) {
                filter.setRadius(range(percentage, 0.0f, 1.0f))
            }
        }

        private inner class HazeAdjuster(filter: GPUImageHazeFilter) :
                Adjuster<GPUImageHazeFilter>(filter) {
            override fun adjust(percentage: Int) {
                filter.setDistance(range(percentage, -0.3f, 0.3f))
                filter.setSlope(range(percentage, -0.3f, 0.3f))
            }
        }

        private inner class SphereRefractionAdjuster(filter: GPUImageSphereRefractionFilter) :
                Adjuster<GPUImageSphereRefractionFilter>(filter) {
            override fun adjust(percentage: Int) {
                filter.setRadius(range(percentage, 0.0f, 1.0f))
            }
        }

        private inner class SwirlAdjuster(filter: GPUImageSwirlFilter) :
                Adjuster<GPUImageSwirlFilter>(filter) {
            override fun adjust(percentage: Int) {
                filter.setAngle(range(percentage, 0.0f, 2.0f))
            }
        }

        private inner class ColorBalanceAdjuster(filter: GPUImageColorBalanceFilter) :
                Adjuster<GPUImageColorBalanceFilter>(filter) {
            override fun adjust(percentage: Int) {
                filter.setMidtones(
                        floatArrayOf(
                                range(percentage, 0.0f, 1.0f),
                                range(percentage / 2, 0.0f, 1.0f),
                                range(percentage / 3, 0.0f, 1.0f)
                        )
                )
            }
        }

        private inner class LevelsMinMidAdjuster(filter: GPUImageLevelsFilter) :
                Adjuster<GPUImageLevelsFilter>(filter) {
            override fun adjust(percentage: Int) {
                filter.setMin(0.0f, range(percentage, 0.0f, 1.0f), 1.0f)
            }
        }

        private inner class BilateralAdjuster(filter: GPUImageBilateralBlurFilter) :
                Adjuster<GPUImageBilateralBlurFilter>(filter) {
            override fun adjust(percentage: Int) {
                filter.setDistanceNormalizationFactor(range(percentage, 0.0f, 15.0f))
            }
        }

        private inner class RotateAdjuster(filter: GPUImageTransformFilter) :
                Adjuster<GPUImageTransformFilter>(filter) {
            override fun adjust(percentage: Int) {
                val transform = FloatArray(16)
                Matrix.setRotateM(transform, 0, (360 * percentage / 100).toFloat(), 0f, 0f, 1.0f)
                filter.transform3D = transform
            }
        }

        private inner class SolarizeAdjuster(filter: GPUImageSolarizeFilter) :
                Adjuster<GPUImageSolarizeFilter>(filter) {
            override fun adjust(percentage: Int) {
                filter.setThreshold(range(percentage, 0.0f, 1.0f))
            }
        }

        private inner class VibranceAdjuster(filter: GPUImageVibranceFilter) :
                Adjuster<GPUImageVibranceFilter>(filter) {
            override fun adjust(percentage: Int) {
                filter.setVibrance(range(percentage, -1.2f, 1.2f))
            }
        }
    }
}
