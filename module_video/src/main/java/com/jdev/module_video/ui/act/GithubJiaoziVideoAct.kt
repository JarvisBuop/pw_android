package com.jdev.module_video.ui.act

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.MediaController
import android.widget.VideoView
import cn.jzvd.Jzvd
import cn.jzvd.JzvdStd
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import com.jarvisdong.kotlindemo.ui.BaseActivity
import com.jdev.module_video.R

/**
 * info: create by jd in 2019/8/5
 * @see:
 * @description:
 *
 */
class GithubJiaoziVideoAct : BaseActivity() {

    lateinit var jzPlayer: JzvdStd
    lateinit var vv: VideoView
    lateinit var exoplayview: PlayerView

    var videoUrl =
            "http://imagetest.youshikoudai.com/36aff518-537f-4334-b4b3-fcf74b20f5fd.mp4"
//                "http://jzvd.nathen.cn/c6e3dc12a1154626b3476d9bf3bd7266/6b56c5f0dc31428083757a45764763b0-5287d2089db37e62345123a1be272f8b.mp4"
//                "http://jzvd.nathen.cn/342a5f7ef6124a4a8faf00e738b8bee4/cf6d9db0bd4d41f59d09ea0a81e918fd-5287d2089db37e62345123a1be272f8b.mp4"

    override fun getViewStubId(): Int {
        return R.layout.mv_layout_github_jiaozi
//        return 0
    }

    override fun initIntentData(): Boolean {
        return true
    }

    override fun customOperate(savedInstanceState: Bundle?) {
        jzPlayer = findViewById<JzvdStd>(R.id.jzplayer)
        vv = findViewById<VideoView>(R.id.video_view_test)
        exoplayview = findViewById<PlayerView>(R.id.exo_playview)
        findViewById<Button>(R.id.btn_replay).setOnClickListener {
            initVideoPlayer()
//            initexoPlayer()
        }

        jzPlayer.setUp(videoUrl, "饺子眼睛", JzvdStd.SCREEN_NORMAL)

        jzPlayer.thumbImageView.setImageResource(R.drawable.ic_launcher_background)
    }

    private fun initexoPlayer() {
        var player = ExoPlayerFactory.newSimpleInstance(mContext)
        exoplayview.player = player

        val dataSourceFactory = DefaultDataSourceFactory(mContext, Util.getUserAgent(mContext, "com.example.module_video"))
        val videoSource = ExtractorMediaSource.Factory(dataSourceFactory)
                .createMediaSource(Uri.parse(videoUrl))
        player.prepare(videoSource)
    }

    fun initVideoPlayer() {
        vv.visibility = View.VISIBLE
        Jzvd.releaseAllVideos()
        jzPlayer.reset()
        jzPlayer.setUp(videoUrl, "饺子眼睛", JzvdStd.SCREEN_NORMAL)
        jzPlayer.thumbImageView.setImageResource(R.drawable.ic_launcher_background)

        vv.setMediaController(MediaController(this))
        vv.setVideoPath(videoUrl)

        vv.start()
        vv.requestFocus()
    }

    override fun onBackPressed() {
        if (JzvdStd.backPress()) {
            return
        }
        super.onBackPressed()
    }

    override fun onPause() {
        super.onPause()
        Jzvd.releaseAllVideos()
    }
}