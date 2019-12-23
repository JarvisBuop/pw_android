package com.example.libimagefilter.filter.helper

import com.example.libimagefilter.filter.advanced.MagicImageAdjustFilter
import com.example.libimagefilter.filter.base.gpuimage.*

class MagicFilterFactory {

    companion object {

        var currentFilterType = MagicFilterType.NONE

//        fun createBlendFilter(
//                filterClass: Class<out GPUImageTwoInputFilter>,
//                blendBitmap: Bitmap? = null
//        ): GPUImageFilter {
//            var bitmapTemp = blendBitmap
//            if (bitmapTemp == null) {
//                bitmapTemp = BitmapFactory.decodeResource(Utils.getApp().resources, R.drawable.gpuimage_origin)
//            }
//            return try {
//                filterClass.newInstance().apply {
//                    bitmap = bitmapTemp
//                }
//            } catch (e: Exception) {
//                e.printStackTrace()
//                GPUImageFilter()
//            }
//        }

        fun getFilterByType(type: MagicFilterType): GPUImageFilter? {
            currentFilterType = type
            return when (type) {
//                MagicFilterType.WHITECAT -> return MagicWhiteCatFilter()
//                MagicFilterType.BLACKCAT -> return MagicBlackCatFilter()
//                MagicFilterType.SKINWHITEN -> return MagicSkinWhitenFilter()
//                MagicFilterType.ROMANCE -> return MagicRomanceFilter()
//                MagicFilterType.SAKURA -> return MagicSakuraFilter()
//                MagicFilterType.AMARO -> return MagicAmaroFilter()
//                MagicFilterType.WALDEN -> return MagicWaldenFilter()
//                MagicFilterType.ANTIQUE -> return MagicAntiqueFilter()
//                MagicFilterType.CALM -> return MagicCalmFilter()
//                MagicFilterType.BRANNAN -> return MagicBrannanFilter()
//                MagicFilterType.BROOKLYN -> return MagicBrooklynFilter()
//                MagicFilterType.EARLYBIRD -> return MagicEarlyBirdFilter()
//                MagicFilterType.FREUD -> return MagicFreudFilter()
//                MagicFilterType.HEFE -> return MagicHefeFilter()
//                MagicFilterType.HUDSON -> return MagicHudsonFilter()
//                MagicFilterType.INKWELL -> return MagicInkwellFilter()
//                MagicFilterType.KEVIN -> return MagicKevinFilter()
//                MagicFilterType.LOMO -> return MagicLomoFilter()
//                MagicFilterType.N1977 -> return MagicN1977Filter()
//                MagicFilterType.NASHVILLE -> return MagicNashvilleFilter()
//                MagicFilterType.PIXAR -> return MagicPixarFilter()
//                MagicFilterType.RISE -> return MagicRiseFilter()
//                MagicFilterType.SIERRA -> return MagicSierraFilter()
//                MagicFilterType.SUTRO -> return MagicSutroFilter()
//                MagicFilterType.TOASTER2 -> return MagicToasterFilter()
//                MagicFilterType.VALENCIA -> return MagicValenciaFilter()
//                MagicFilterType.XPROII -> return MagicXproIIFilter()
//                MagicFilterType.EVERGREEN -> return MagicEvergreenFilter()
//                MagicFilterType.HEALTHY -> return MagicHealthyFilter()
//                MagicFilterType.COOL -> return MagicCoolFilter()
//                MagicFilterType.EMERALD -> return MagicEmeraldFilter()
//                MagicFilterType.LATTE -> return MagicLatteFilter()
//                MagicFilterType.WARM -> return MagicWarmFilter()
//                MagicFilterType.TENDER -> return MagicTenderFilter()
//                MagicFilterType.SWEETS -> return MagicSweetsFilter()
//                MagicFilterType.NOSTALGIA -> return MagicNostalgiaFilter()
//                MagicFilterType.FAIRYTALE -> return MagicFairytaleFilter()
//                MagicFilterType.SUNRISE -> return MagicSunriseFilter()
//                MagicFilterType.SUNSET -> return MagicSunsetFilter()
//                MagicFilterType.CRAYON -> return MagicCrayonFilter()
//                MagicFilterType.SKETCH -> return MagicSketchFilter()
                //image adjust
                MagicFilterType.BRIGHTNESS -> return GPUImageBrightnessFilter()
                MagicFilterType.CONTRAST -> return GPUImageContrastFilter()
                MagicFilterType.EXPOSURE -> return GPUImageExposureFilter()
                MagicFilterType.HUE -> return GPUImageHueFilter()
                MagicFilterType.SATURATION -> return GPUImageSaturationFilter()
                MagicFilterType.SHARPEN -> return GPUImageSharpenFilter()
                MagicFilterType.IMAGE_ADJUST -> return MagicImageAdjustFilter()

                //origin
//                MagicFilterType.GAMMA -> GPUImageGammaFilter(2.0f)
//                MagicFilterType.INVERT -> GPUImageColorInvertFilter()
//                MagicFilterType.PIXELATION -> GPUImagePixelationFilter()
//                MagicFilterType.GRAYSCALE -> GPUImageGrayscaleFilter()
//                MagicFilterType.SEPIA -> GPUImageSepiaToneFilter()
//                MagicFilterType.SOBEL_EDGE_DETECTION -> GPUImageSobelEdgeDetectionFilter()
//                MagicFilterType.THRESHOLD_EDGE_DETECTION -> GPUImageThresholdEdgeDetectionFilter()
//                MagicFilterType.THREE_X_THREE_CONVOLUTION -> GPUImage3x3ConvolutionFilter()
//                MagicFilterType.EMBOSS -> GPUImageEmbossFilter()
//                MagicFilterType.POSTERIZE -> GPUImagePosterizeFilter()
//                MagicFilterType.FILTER_GROUP -> GPUImageFilterGroup(
//                        listOf(
//                                GPUImageContrastFilter(),
//                                GPUImageDirectionalSobelEdgeDetectionFilter(),
//                                GPUImageGrayscaleFilter()
//                        )
//                )
//                MagicFilterType.HIGHLIGHT_SHADOW -> GPUImageHighlightShadowFilter(
//                        0.0f,
//                        1.0f
//                )
//                MagicFilterType.MONOCHROME -> GPUImageMonochromeFilter(
//                        1.0f, floatArrayOf(0.6f, 0.45f, 0.3f, 1.0f)
//                )
//                MagicFilterType.OPACITY -> GPUImageOpacityFilter(1.0f)
//                MagicFilterType.RGB -> GPUImageRGBFilter(1.0f, 1.0f, 1.0f)
//                MagicFilterType.WHITE_BALANCE -> GPUImageWhiteBalanceFilter(
//                        5000.0f,
//                        0.0f
//                )
//                MagicFilterType.VIGNETTE -> GPUImageVignetteFilter(
//                        PointF(0.5f, 0.5f),
//                        floatArrayOf(0.0f, 0.0f, 0.0f),
//                        0.3f,
//                        0.75f
//                )
//                MagicFilterType.TONE_CURVE -> GPUImageToneCurveFilter().apply {
//                    setFromCurveFileInputStream(Utils.getApp().resources.openRawResource(R.raw.tone_cuver_sample))
//                }
//                MagicFilterType.LUMINANCE -> GPUImageLuminanceFilter()
//                MagicFilterType.LUMINANCE_THRESHSOLD -> GPUImageLuminanceThresholdFilter(0.5f)
//                MagicFilterType.BLEND_DIFFERENCE -> createBlendFilter(
//                        GPUImageDifferenceBlendFilter::class.java
//                )
//                MagicFilterType.BLEND_SOURCE_OVER -> createBlendFilter(
//                        GPUImageSourceOverBlendFilter::class.java
//                )
//                MagicFilterType.BLEND_COLOR_BURN -> createBlendFilter(
//                        GPUImageColorBurnBlendFilter::class.java
//                )
//                MagicFilterType.BLEND_COLOR_DODGE -> createBlendFilter(
//                        GPUImageColorDodgeBlendFilter::class.java
//                )
//                MagicFilterType.BLEND_DARKEN -> createBlendFilter(
//                        GPUImageDarkenBlendFilter::class.java
//                )
//                MagicFilterType.BLEND_DISSOLVE -> createBlendFilter(
//                        GPUImageDissolveBlendFilter::class.java
//                )
//                MagicFilterType.BLEND_EXCLUSION -> createBlendFilter(
//                        GPUImageExclusionBlendFilter::class.java
//                )
//                MagicFilterType.BLEND_HARD_LIGHT -> createBlendFilter(
//                        GPUImageHardLightBlendFilter::class.java
//                )
//                MagicFilterType.BLEND_LIGHTEN -> createBlendFilter(
//                        GPUImageLightenBlendFilter::class.java
//                )
//                MagicFilterType.BLEND_ADD -> createBlendFilter(
//                        GPUImageAddBlendFilter::class.java
//                )
//                MagicFilterType.BLEND_DIVIDE -> createBlendFilter(
//                        GPUImageDivideBlendFilter::class.java
//                )
//                MagicFilterType.BLEND_MULTIPLY -> createBlendFilter(
//                        GPUImageMultiplyBlendFilter::class.java
//                )
//                MagicFilterType.BLEND_OVERLAY -> createBlendFilter(
//                        GPUImageOverlayBlendFilter::class.java
//                )
//                MagicFilterType.BLEND_SCREEN -> createBlendFilter(
//                        GPUImageScreenBlendFilter::class.java
//                )
//                MagicFilterType.BLEND_ALPHA -> createBlendFilter(
//                        GPUImageAlphaBlendFilter::class.java
//                )
//                MagicFilterType.BLEND_COLOR -> createBlendFilter(
//                        GPUImageColorBlendFilter::class.java
//                )
//                MagicFilterType.BLEND_HUE -> createBlendFilter(
//                        GPUImageHueBlendFilter::class.java
//                )
//                MagicFilterType.BLEND_SATURATION -> createBlendFilter(
//                        GPUImageSaturationBlendFilter::class.java
//                )
//                MagicFilterType.BLEND_LUMINOSITY -> createBlendFilter(
//                        GPUImageLuminosityBlendFilter::class.java
//                )
//                MagicFilterType.BLEND_LINEAR_BURN -> createBlendFilter(
//                        GPUImageLinearBurnBlendFilter::class.java
//                )
//                MagicFilterType.BLEND_SOFT_LIGHT -> createBlendFilter(
//                        GPUImageSoftLightBlendFilter::class.java
//                )
//                MagicFilterType.BLEND_SUBTRACT -> createBlendFilter(
//                        GPUImageSubtractBlendFilter::class.java
//                )
//                MagicFilterType.BLEND_CHROMA_KEY -> createBlendFilter(
//                        GPUImageChromaKeyBlendFilter::class.java
//                )
//                MagicFilterType.BLEND_NORMAL -> createBlendFilter(
//                        GPUImageNormalBlendFilter::class.java
//                )
//
//                MagicFilterType.LOOKUP_AMATORKA -> GPUImageLookupFilter().apply {
//                    bitmap = BitmapFactory.decodeResource(Utils.getApp().resources, R.drawable.lookup_amatorka)
//                }
//                MagicFilterType.GAUSSIAN_BLUR -> GPUImageGaussianBlurFilter()
//                MagicFilterType.CROSSHATCH -> GPUImageCrosshatchFilter()
//                MagicFilterType.BOX_BLUR -> GPUImageBoxBlurFilter()
//                MagicFilterType.CGA_COLORSPACE -> GPUImageCGAColorspaceFilter()
//                MagicFilterType.DILATION -> GPUImageDilationFilter()
//                MagicFilterType.KUWAHARA -> GPUImageKuwaharaFilter()
//                MagicFilterType.RGB_DILATION -> GPUImageRGBDilationFilter()
////                MagicFilterType.SKETCH -> GPUImageSketchFilter()
//                MagicFilterType.TOON -> GPUImageToonFilter()
//                MagicFilterType.SMOOTH_TOON -> GPUImageSmoothToonFilter()
//                MagicFilterType.BULGE_DISTORTION -> GPUImageBulgeDistortionFilter()
//                MagicFilterType.GLASS_SPHERE -> GPUImageGlassSphereFilter()
//                MagicFilterType.HAZE -> GPUImageHazeFilter()
//                MagicFilterType.LAPLACIAN -> GPUImageLaplacianFilter()
//                MagicFilterType.NON_MAXIMUM_SUPPRESSION -> GPUImageNonMaximumSuppressionFilter()
//                MagicFilterType.SPHERE_REFRACTION -> GPUImageSphereRefractionFilter()
//                MagicFilterType.SWIRL -> GPUImageSwirlFilter()
//                MagicFilterType.WEAK_PIXEL_INCLUSION -> GPUImageWeakPixelInclusionFilter()
//                MagicFilterType.FALSE_COLOR -> GPUImageFalseColorFilter()
//                MagicFilterType.COLOR_BALANCE -> GPUImageColorBalanceFilter()
//                MagicFilterType.LEVELS_FILTER_MIN -> GPUImageLevelsFilter()
//                MagicFilterType.HALFTONE -> GPUImageHalftoneFilter()
//                MagicFilterType.BILATERAL_BLUR -> GPUImageBilateralBlurFilter()
//                MagicFilterType.ZOOM_BLUR -> GPUImageZoomBlurFilter()
//                MagicFilterType.TRANSFORM2D -> GPUImageTransformFilter()
//                MagicFilterType.SOLARIZE -> GPUImageSolarizeFilter()
//                MagicFilterType.VIBRANCE -> GPUImageVibranceFilter()
                else -> return null
            }
        }
    }
}
