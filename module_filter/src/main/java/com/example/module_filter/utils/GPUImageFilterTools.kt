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

package com.example.module_filter.utils

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import com.blankj.utilcode.util.LogUtils
import com.example.libimagefilter.filter.base.gpuimage.GPUImageFilter
import com.example.libimagefilter.filter.helper.MagicFilterType
import com.jarvisdong.kit.utils.ResourceIdUtils
import com.example.libimagefilter.filter.helper.FilterAdjuster
import com.example.libimagefilter.filter.helper.MagicFilterFactory
import com.example.libimagefilter.filter.origin.GPUImageFilterGroup
import com.example.libimagefilter.filter.other.GpuImageBeautyFilter
import com.example.module_filter.R

object GPUImageFilterTools {

    var array = booleanArrayOf()
    var nameArrays = arrayOf<String>()

    /**
     * 测试使用;
     */
    fun showCustomFilterDialog(context: Context,
                               name: String = "",
                               listener: (filter: GPUImageFilter?, filterName: String) -> Unit) {

        val filters = arrayListOf<MagicFilterType>(
                MagicFilterType.CUSTOM_MIXED,
                MagicFilterType.CUSTOM_美颜
        )
        val names = arrayListOf<String>()
        for (i in filters) {
            names.add(i.toString())
        }

        var index = names.indexOf(name)
        AlertDialog.Builder(context)
                .setTitle("Choose a custom filter")
                .setSingleChoiceItems(names.toTypedArray(), index) { dialog, item ->
                    dialog.dismiss()
                    if (MagicFilterType.CUSTOM_MIXED == filters[item]) {
                        doMultiChoiceItem(context, listener)
                    } else {
                        listener(createCustomFilterForType(context, filters[item]), names[item])
                    }
                }
                .create().show()
    }

    private fun doMultiChoiceItem(context: Context,
                                  listener: (filter: GPUImageFilter?, filterName: String) -> Unit) {
        val (filters, names) = getFilterTypes()

        if (nameArrays.isEmpty() || array.isEmpty()) {
            nameArrays = names.toTypedArray()
            array = BooleanArray(nameArrays.size)
        }
        AlertDialog.Builder(context)
                .setTitle("Choose multi filter")
                .setMultiChoiceItems(nameArrays, array) { dialog, which, isChecked ->
                    array[which] = isChecked
                }
                .setOnDismissListener {
                    var gpuFilters = arrayListOf<GPUImageFilter>()
                    for (i in array.indices) {
                        if (array[i]) {
                            var filterByType = MagicFilterFactory.getFilterByType(filters[i])
                            if (filterByType != null) {
                                gpuFilters.add(filterByType)
                            }
                        }
                    }
                    LogUtils.e("group contents: ${gpuFilters.toString()}")
                    listener(GPUImageFilterGroup(gpuFilters), MagicFilterType.CUSTOM_MIXED.toString())
                }
                .create().show()
    }

    fun createCustomFilterForType(context: Context, type: MagicFilterType): GPUImageFilter? {
        return when (type) {
            MagicFilterType.CUSTOM_美颜 -> {
                GpuImageBeautyFilter()
            }
            else -> {
                GpuImageBeautyFilter()
            }
        }
        return null
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
        val (filters, names) = getFilterTypes()

        var index = names.indexOf(name)
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Choose a filter")
                .setSingleChoiceItems(names.toTypedArray(), index) { dialog, item ->
                    dialog.dismiss()
                    listener(MagicFilterFactory.getFilterByType(filters[item]), names[item])
                }
                .create().show()
    }

    private fun getFilterTypes(): Pair<ArrayList<MagicFilterType>, ArrayList<String>> {
        val filters = initFilterListObj()

        val names = arrayListOf<String>()
        for (i in filters) {
            names.add(i.toString().plus(" ${FilterType2Name(i)}"))
        }
        return Pair(filters, names)
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


    fun FilterType2Name(filterType: MagicFilterType?): String {
        when (filterType) {
            MagicFilterType.NONE -> return ResourceIdUtils.getStringById(R.string.filter_none)
            MagicFilterType.WHITECAT -> return ResourceIdUtils.getStringById(R.string.filter_whitecat)
            MagicFilterType.BLACKCAT -> return ResourceIdUtils.getStringById(R.string.filter_blackcat)
            MagicFilterType.ROMANCE -> return ResourceIdUtils.getStringById(R.string.filter_romance)
            MagicFilterType.SAKURA -> return ResourceIdUtils.getStringById(R.string.filter_sakura)
            MagicFilterType.AMARO -> return ResourceIdUtils.getStringById(R.string.filter_amaro)
            MagicFilterType.BRANNAN -> return ResourceIdUtils.getStringById(R.string.filter_brannan)
            MagicFilterType.BROOKLYN -> return ResourceIdUtils.getStringById(R.string.filter_brooklyn)
            MagicFilterType.EARLYBIRD -> return ResourceIdUtils.getStringById(R.string.filter_Earlybird)
            MagicFilterType.FREUD -> return ResourceIdUtils.getStringById(R.string.filter_freud)
            MagicFilterType.HEFE -> return ResourceIdUtils.getStringById(R.string.filter_hefe)
            MagicFilterType.HUDSON -> return ResourceIdUtils.getStringById(R.string.filter_hudson)
            MagicFilterType.INKWELL -> return ResourceIdUtils.getStringById(R.string.filter_inkwell)
            MagicFilterType.KEVIN -> return ResourceIdUtils.getStringById(R.string.filter_kevin)
            MagicFilterType.LOMO -> return ResourceIdUtils.getStringById(R.string.filter_lomo)
            MagicFilterType.N1977 -> return ResourceIdUtils.getStringById(R.string.filter_n1977)
            MagicFilterType.NASHVILLE -> return ResourceIdUtils.getStringById(R.string.filter_nashville)
            MagicFilterType.PIXAR -> return ResourceIdUtils.getStringById(R.string.filter_pixar)
            MagicFilterType.RISE -> return ResourceIdUtils.getStringById(R.string.filter_rise)
            MagicFilterType.SIERRA -> return ResourceIdUtils.getStringById(R.string.filter_sierra)
            MagicFilterType.SUTRO -> return ResourceIdUtils.getStringById(R.string.filter_sutro)
            MagicFilterType.TOASTER2 -> return ResourceIdUtils.getStringById(R.string.filter_toastero)
            MagicFilterType.VALENCIA -> return ResourceIdUtils.getStringById(R.string.filter_valencia)
            MagicFilterType.WALDEN -> return ResourceIdUtils.getStringById(R.string.filter_walden)
            MagicFilterType.XPROII -> return ResourceIdUtils.getStringById(R.string.filter_xproii)
            MagicFilterType.ANTIQUE -> return ResourceIdUtils.getStringById(R.string.filter_antique)
            MagicFilterType.CALM -> return ResourceIdUtils.getStringById(R.string.filter_calm)
            MagicFilterType.COOL -> return ResourceIdUtils.getStringById(R.string.filter_cool)
            MagicFilterType.EMERALD -> return ResourceIdUtils.getStringById(R.string.filter_emerald)
            MagicFilterType.EVERGREEN -> return ResourceIdUtils.getStringById(R.string.filter_evergreen)
            MagicFilterType.FAIRYTALE -> return ResourceIdUtils.getStringById(R.string.filter_fairytale)
            MagicFilterType.HEALTHY -> return ResourceIdUtils.getStringById(R.string.filter_healthy)
            MagicFilterType.NOSTALGIA -> return ResourceIdUtils.getStringById(R.string.filter_nostalgia)
            MagicFilterType.TENDER -> return ResourceIdUtils.getStringById(R.string.filter_tender)
            MagicFilterType.SWEETS -> return ResourceIdUtils.getStringById(R.string.filter_sweets)
            MagicFilterType.LATTE -> return ResourceIdUtils.getStringById(R.string.filter_latte)
            MagicFilterType.WARM -> return ResourceIdUtils.getStringById(R.string.filter_warm)
            MagicFilterType.SUNRISE -> return ResourceIdUtils.getStringById(R.string.filter_sunrise)
            MagicFilterType.SUNSET -> return ResourceIdUtils.getStringById(R.string.filter_sunset)
            MagicFilterType.SKINWHITEN -> return ResourceIdUtils.getStringById(R.string.filter_skinwhiten)
            MagicFilterType.CRAYON -> return ResourceIdUtils.getStringById(R.string.filter_crayon)
            MagicFilterType.SKETCH -> return ResourceIdUtils.getStringById(R.string.filter_sketch)
            //base
            MagicFilterType.CONTRAST -> return ResourceIdUtils.getStringById(R.string.edit_contrast)
            MagicFilterType.BRIGHTNESS -> return ResourceIdUtils.getStringById(R.string.edit_brightness)
            MagicFilterType.EXPOSURE -> return ResourceIdUtils.getStringById(R.string.edit_exposure)
            MagicFilterType.HUE -> return ResourceIdUtils.getStringById(R.string.edit_hue)
            MagicFilterType.SATURATION -> return ResourceIdUtils.getStringById(R.string.edit_saturation)
            MagicFilterType.SHARPEN -> return ResourceIdUtils.getStringById(R.string.edit_sharpness)
            MagicFilterType.VIBRANCE -> return ResourceIdUtils.getStringById(R.string.edit_vibrance)
            MagicFilterType.IMAGE_ADJUST -> return "基础滤镜混合"
            else -> return ""
        }
    }
}
