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

object GPUImageFilterTools {
    /**
     * 测试使用;
     */
    fun showCustomFilterDialog(context: Context,
                               name: String = "",
                               listener: (filter: GPUImageFilter, filterName: String) -> Unit) {

        val filters = arrayListOf<MagicFilterType>(
                MagicFilterType.CUSTOM_丑颜,
                MagicFilterType.CUSTOM_美颜
        )
        val names = arrayListOf<String>()
        for(i in filters){
            names.add(i.toString())
        }

        var index = names.indexOf(name)
        AlertDialog.Builder(context)
                .setTitle("Choose a custom filter")
                .setSingleChoiceItems(names.toTypedArray(), index) { dialog, item ->
                    dialog.dismiss()
                    listener(createCustomFilterForType(context, filters[item]), names[item])
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

        val names = arrayListOf<String>()
        for(i in filters){
            names.add(i.toString())
        }

        var index = names.indexOf(name)
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Choose a filter")
                .setSingleChoiceItems(names.toTypedArray(), index) { dialog, item ->
                    dialog.dismiss()
                    listener(MagicFilterFactory.getFilterByType(context, filters[item]), names[item])
                }
                .create().show()
    }

    fun initFilterListObj(): ArrayList<MagicFilterType> {
        return arrayListOf<MagicFilterType>(
                MagicFilterType.NONE,
                MagicFilterType.FAIRYTALE,
                MagicFilterType.SUNRISE,
                MagicFilterType.SUNSET,
                MagicFilterType.WHITECAT,
                MagicFilterType.BLACKCAT,
                MagicFilterType.SKINWHITEN,
                MagicFilterType.HEALTHY,
                MagicFilterType.SWEETS,
                MagicFilterType.ROMANCE,
                MagicFilterType.SAKURA,
                MagicFilterType.WARM,
                MagicFilterType.ANTIQUE,
                MagicFilterType.NOSTALGIA,
                MagicFilterType.CALM,
                MagicFilterType.LATTE,
                MagicFilterType.TENDER,
                MagicFilterType.COOL,
                MagicFilterType.EMERALD,
                MagicFilterType.EVERGREEN,
                MagicFilterType.CRAYON,
                MagicFilterType.SKETCH,
                MagicFilterType.AMARO,
                MagicFilterType.BRANNAN,
                MagicFilterType.BROOKLYN,
                MagicFilterType.EARLYBIRD,
                MagicFilterType.FREUD,
                MagicFilterType.HEFE,
                MagicFilterType.HUDSON,
                MagicFilterType.INKWELL,
                MagicFilterType.KEVIN,
                MagicFilterType.LOMO,
                MagicFilterType.N1977,
                MagicFilterType.NASHVILLE,
                MagicFilterType.PIXAR,
                MagicFilterType.RISE,
                MagicFilterType.SIERRA,
                MagicFilterType.SUTRO,
                MagicFilterType.TOASTER2,
                MagicFilterType.VALENCIA,
                MagicFilterType.WALDEN,
                MagicFilterType.XPROII,

                //origin
                MagicFilterType.CONTRAST,
                MagicFilterType.BRIGHTNESS,
                MagicFilterType.EXPOSURE,
                MagicFilterType.HUE,
                MagicFilterType.SATURATION,
                MagicFilterType.SHARPEN,
                MagicFilterType.IMAGE_ADJUST,
                MagicFilterType.GRAYSCALE,
                MagicFilterType.SEPIA,
                MagicFilterType.SOBEL_EDGE_DETECTION,
                MagicFilterType.THRESHOLD_EDGE_DETECTION,
                MagicFilterType.THREE_X_THREE_CONVOLUTION,
                MagicFilterType.FILTER_GROUP,
                MagicFilterType.EMBOSS,
                MagicFilterType.POSTERIZE,
                MagicFilterType.GAMMA,
                MagicFilterType.INVERT,
                MagicFilterType.PIXELATION,
                MagicFilterType.HIGHLIGHT_SHADOW,
                MagicFilterType.MONOCHROME,
                MagicFilterType.OPACITY,
                MagicFilterType.RGB,
                MagicFilterType.WHITE_BALANCE,
                MagicFilterType.VIGNETTE,
                MagicFilterType.TONE_CURVE,
                MagicFilterType.LUMINANCE,
                MagicFilterType.LUMINANCE_THRESHSOLD,
                MagicFilterType.BLEND_COLOR_BURN,
                MagicFilterType.BLEND_COLOR_DODGE,
                MagicFilterType.BLEND_DARKEN,
                MagicFilterType.BLEND_DIFFERENCE,
                MagicFilterType.BLEND_DISSOLVE,
                MagicFilterType.BLEND_EXCLUSION,
                MagicFilterType.BLEND_SOURCE_OVER,
                MagicFilterType.BLEND_HARD_LIGHT,
                MagicFilterType.BLEND_LIGHTEN,
                MagicFilterType.BLEND_ADD,
                MagicFilterType.BLEND_DIVIDE,
                MagicFilterType.BLEND_MULTIPLY,
                MagicFilterType.BLEND_OVERLAY,
                MagicFilterType.BLEND_SCREEN,
                MagicFilterType.BLEND_ALPHA,
                MagicFilterType.BLEND_COLOR,
                MagicFilterType.BLEND_HUE,
                MagicFilterType.BLEND_SATURATION,
                MagicFilterType.BLEND_LUMINOSITY,
                MagicFilterType.BLEND_LINEAR_BURN,
                MagicFilterType.BLEND_SOFT_LIGHT,
                MagicFilterType.BLEND_SUBTRACT,
                MagicFilterType.BLEND_CHROMA_KEY,
                MagicFilterType.BLEND_NORMAL,
                MagicFilterType.LOOKUP_AMATORKA,
                MagicFilterType.GAUSSIAN_BLUR,
                MagicFilterType.CROSSHATCH,
                MagicFilterType.BOX_BLUR,
                MagicFilterType.CGA_COLORSPACE,
                MagicFilterType.DILATION,
                MagicFilterType.KUWAHARA,
                MagicFilterType.RGB_DILATION,
                MagicFilterType.TOON,
                MagicFilterType.SMOOTH_TOON,
                MagicFilterType.BULGE_DISTORTION,
                MagicFilterType.GLASS_SPHERE,
                MagicFilterType.HAZE,
                MagicFilterType.LAPLACIAN,
                MagicFilterType.NON_MAXIMUM_SUPPRESSION,
                MagicFilterType.SPHERE_REFRACTION,
                MagicFilterType.SWIRL,
                MagicFilterType.WEAK_PIXEL_INCLUSION,
                MagicFilterType.FALSE_COLOR,
                MagicFilterType.COLOR_BALANCE,
                MagicFilterType.LEVELS_FILTER_MIN,
                MagicFilterType.BILATERAL_BLUR,
                MagicFilterType.ZOOM_BLUR,
                MagicFilterType.HALFTONE,
                MagicFilterType.TRANSFORM2D,
                MagicFilterType.SOLARIZE,
                MagicFilterType.VIBRANCE,
                MagicFilterType.CUSTOM_丑颜,
                MagicFilterType.CUSTOM_美颜
        )
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
