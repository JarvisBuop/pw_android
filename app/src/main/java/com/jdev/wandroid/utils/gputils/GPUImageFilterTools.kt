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
import com.jdev.wandroid.widget.GPUImageUglyFilter
import com.jdev.wandroid.widget.GpuImageBeautyFilter
import com.seu.magicfilter.filter.base.gpuimage.GPUImageFilter
import com.seu.magicfilter.filter.helper.FilterAdjuster
import com.seu.magicfilter.filter.helper.MagicFilterFactory
import com.seu.magicfilter.filter.helper.MagicFilterType

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
                MagicFilterType.VIBRANCE
        )
    }


}
