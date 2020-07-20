package com.jdev.wandroid.ui.frg

import android.media.MediaCodec
import android.media.MediaExtractor
import android.media.MediaMuxer
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import com.jdev.kit.baseui.BaseViewStubFragment

class MediaMuxerTestFrag : BaseViewStubFragment() {
    companion object {
        val mOutputVideoPath: String = "/pwandroid/temp.mp4"
    }

    override fun getViewStubDefault(): View? {
        var linearLayout = LinearLayout(mContext)
        linearLayout.addView(Button(mContext).apply {
            text = "mediamuxer"
            setOnClickListener {
                textMediaMuxer()
            }
        })
        return linearLayout
    }

    private fun textMediaMuxer() {
        var mediaMuxer = MediaMuxer(mOutputVideoPath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4)

//        MediaExtractor()
//        MediaCodec.createByCodecName()
    }

    override fun customOperate(savedInstanceState: Bundle?) {

    }
}