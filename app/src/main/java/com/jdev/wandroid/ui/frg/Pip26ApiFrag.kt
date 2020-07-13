package com.jdev.wandroid.ui.frg

import android.app.PendingIntent
import android.app.PictureInPictureParams
import android.app.RemoteAction
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.drawable.Icon
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.annotation.DrawableRes
import android.support.annotation.RequiresApi
import android.support.v4.app.FragmentActivity
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Rational
import android.view.View
import android.widget.MediaController
import android.widget.TextView
import android.widget.VideoView
import com.blankj.utilcode.util.LogUtils
import com.jdev.kit.baseui.BaseViewStubFragment
import com.jdev.wandroid.R
import kotlinx.android.synthetic.main.app_frag_pip.*
import java.util.*

/**
 * info: create by jd in 2019/12/27
 * @see:
 * @description: pip mode
 *
 */

class Pip26ApiFrag : BaseViewStubFragment() {
    var btn_pip:TextView
        get() {
            return findView(R.id.btn_pip)
        }
        set(value) {}

    var btn_play:TextView
        get() {
            return findView(R.id.btn_play)
        }
        set(value) {}

    var mVideoView:VideoView
        get() {
            return findView(R.id.mVideoView)
        }
        set(value) {}

    var btn_pause:TextView
        get() {
            return findView(R.id.btn_pause)
        }
        set(value) {}

    var btn_ms:TextView
        get() {
            return findView(R.id.btn_ms)
        }
        set(value) {}

    @RequiresApi(Build.VERSION_CODES.O)
    private val mPipParamsBuilder: PictureInPictureParams.Builder = PictureInPictureParams.Builder()

    override fun getViewStubId(): Int {
        return R.layout.app_frag_pip
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun customOperate(savedInstanceState: Bundle?) {
        initVideoView()
        updatePictureInPictureActions(R.drawable.jz_pause_normal, "暂停", CONTROL_TYPE_PAUSE, REQUEST_PAUSE)


        btn_pip.setOnClickListener {
            enterPip(activity!!)
        }
        btn_play.setOnClickListener {
            mVideoView.start()
            updatePictureInPictureActions(R.drawable.jz_pause_normal, "暂停", CONTROL_TYPE_PAUSE, REQUEST_PAUSE)
        }
        btn_pause.setOnClickListener {
            mVideoView.pause()
            updatePictureInPictureActions(R.drawable.jz_play_normal, "播放", CONTROL_TYPE_PLAY, REQUEST_PLAY)
        }

        btn_ms.setOnClickListener {
            //support media session pip
            initializeMediaSession()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun initVideoView() {
        //        var openRawResourceFd = mContext!!.resources.openRawResourceFd(R.raw.vid_bigbuckbunny)
        //        Uri.parse("android.resource://com.example.work/"+R.raw.
        mVideoView.setMediaController(MediaController(mContext))
        mVideoView.setVideoURI(Uri.parse("android.resource://com.jdev.wandroid/" + R.raw.vid_bigbuckbunny))
        mVideoView.setOnPreparedListener {
            mVideoView.start()
        }
    }

    /**
     * 进入画中画模式;
     */
    private fun enterPip(activity: FragmentActivity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Calculate the aspect ratio of the PiP screen.
            var rational = Rational(mVideoView.width, mVideoView.height)
            mPipParamsBuilder.setAspectRatio(rational)
            activity.enterPictureInPictureMode(mPipParamsBuilder.build())
        }
    }

    /** Intent action for media controls from Picture-in-Picture mode.  */
    private val ACTION_MEDIA_CONTROL = "media_control"

    /** Intent extra for media controls from Picture-in-Picture mode.  */
    private val EXTRA_CONTROL_TYPE = "control_type"

    /** The intent extra value for play action.  */
    private val CONTROL_TYPE_PLAY = 1

    /** The intent extra value for pause action.  */
    private val CONTROL_TYPE_PAUSE = 2

    /** A [BroadcastReceiver] to receive action item events from Picture-in-Picture mode.  */
    private var mReceiver: BroadcastReceiver? = null

    /** The request code for play action PendingIntent.  */
    private val REQUEST_PLAY = 1

    /** The request code for pause action PendingIntent.  */
    private val REQUEST_PAUSE = 2

    /** The request code for info action PendingIntent.  */
    private val REQUEST_INFO = 3

    /**
     * pip 是否处于此模式;
     */
    override fun onPictureInPictureModeChanged(isInPictureInPictureMode: Boolean) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode)
        LogUtils.eTag(TAG, "onPictureInPictureModeChanged  $isInPictureInPictureMode")

        if (isInPictureInPictureMode) {
            // Starts receiving events from action items in PiP mode.
            mReceiver = object : BroadcastReceiver() {
                @RequiresApi(Build.VERSION_CODES.O)
                override fun onReceive(context: Context, intent: Intent?) {
                    if (intent == null || ACTION_MEDIA_CONTROL != intent.action) {
                        return
                    }

                    // This is where we are called back from Picture-in-Picture action
                    // items.
                    val controlType = intent.getIntExtra(EXTRA_CONTROL_TYPE, 0)
                    when (controlType) {
                        CONTROL_TYPE_PLAY -> {
                            mVideoView.start()
                            updatePictureInPictureActions(R.drawable.jz_pause_normal, "暂停", CONTROL_TYPE_PAUSE, REQUEST_PAUSE)
                        }
                        CONTROL_TYPE_PAUSE -> {
                            mVideoView.pause()
                            updatePictureInPictureActions(R.drawable.jz_play_normal, "播放", CONTROL_TYPE_PLAY, REQUEST_PLAY)
                        }
                    }
                }
            }
            activity!!.registerReceiver(mReceiver, IntentFilter(ACTION_MEDIA_CONTROL))
        } else {
            // We are out of PiP mode. We can stop receiving events from it.
            activity!!.unregisterReceiver(mReceiver)
            mReceiver = null
//            // Show the video controls if the video is not playing
        }

    }

    /**
     *
     *  播放或者暂停发送消息给pip,设置pip属性;
     * Update the state of pause/resume action item in Picture-in-Picture mode.
     *
     * @param iconId The icon to be used.
     * @param title The title text.
     * @param controlType The type of the action. either [.CONTROL_TYPE_PLAY] or [     ][.CONTROL_TYPE_PAUSE].
     * @param requestCode The request code for the [PendingIntent].
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun updatePictureInPictureActions(
            @DrawableRes iconId: Int, title: String, controlType: Int, requestCode: Int) {
        val actions = ArrayList<RemoteAction>()

        // This is the PendingIntent that is invoked when a user clicks on the action item.
        // You need to use distinct request codes for play and pause, or the PendingIntent won't
        // be properly updated.
        val intent = PendingIntent.getBroadcast(
                activity,
                requestCode,
                Intent(ACTION_MEDIA_CONTROL).putExtra(EXTRA_CONTROL_TYPE, controlType),
                0)
        val icon = Icon.createWithResource(activity, iconId)
        actions.add(RemoteAction(icon, title, title, intent))

        // Another action item. This is a fixed action.
        actions.add(
                RemoteAction(
                        Icon.createWithResource(activity, R.drawable.jz_back_normal),
                        "info",
                        "info_desc",
                        PendingIntent.getActivity(
                                activity,
                                REQUEST_INFO,
                                Intent(
                                        Intent.ACTION_VIEW,
                                        Uri.parse("https://www.baidu.com")),
                                0)))

        mPipParamsBuilder.setActions(actions)

        // This is how you can update action items (or aspect ratio) for Picture-in-Picture mode.
        // Note this call can happen even when the app is not in PiP mode. In that case, the
        // arguments will be used for at the next call of #enterPictureInPictureMode.
        activity!!.setPictureInPictureParams(mPipParamsBuilder.build())
    }

    //----------------------------------------------------------------------------------------------
    //-----------------media session----------------------------------------------------------------
    //----------------------------------------------------------------------------------------------

    private var mSession: MediaSessionCompat? = null

    /**
     * 支持的命令;
     */
    val MEDIA_ACTIONS_PLAY_PAUSE = (
            PlaybackStateCompat.ACTION_PLAY
                    or PlaybackStateCompat.ACTION_PAUSE
                    or PlaybackStateCompat.ACTION_PLAY_PAUSE)

    val MEDIA_ACTIONS_ALL = (
            MEDIA_ACTIONS_PLAY_PAUSE
                    or PlaybackStateCompat.ACTION_SKIP_TO_NEXT
                    or PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS)

    private fun initializeMediaSession() {
        mSession = MediaSessionCompat(mContext, TAG)
        mSession!!.setFlags(
                MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS or MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS)
        mSession!!.setActive(true)
        MediaControllerCompat.setMediaController(activity!!, mSession!!.getController())

        val metadata = MediaMetadataCompat.Builder()
                .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE, "defaultTitle")
                .build()
        mSession!!.setMetadata(metadata)

        val mMediaSessionCallback = MediaSessionCallback(mVideoView)
        mSession!!.setCallback(mMediaSessionCallback)

        val state = if (mVideoView.isPlaying)
            PlaybackStateCompat.STATE_PLAYING
        else
            PlaybackStateCompat.STATE_PAUSED

        updatePlaybackState(
                state,
                MEDIA_ACTIONS_ALL,
                mVideoView.getCurrentPosition(),
                R.raw.vid_bigbuckbunny)
    }

    override fun onStop() {
        super.onStop()
        // On entering Picture-in-Picture mode, onPause is called, but not onStop.
        // For this reason, this is the place where we should pause the video playback.
        mVideoView.pause()
        mSession?.release()
        mSession = null
    }

    /**
     * Overloaded method that persists previously set media actions.
     *
     * @param state The state of the video, e.g. playing, paused, etc.
     * @param position The position of playback in the video.
     * @param mediaId The media id related to the video in the media session.
     */
    private fun updatePlaybackState(
            @PlaybackStateCompat.State state: Int, position: Int, mediaId: Int) {
        val actions = mSession!!.getController().playbackState.actions
        updatePlaybackState(state, actions, position, mediaId)
    }

    private fun updatePlaybackState(
            @PlaybackStateCompat.State state: Int, playbackActions: Long, position: Int, mediaId: Int) {
        val builder = PlaybackStateCompat.Builder()
                .setActions(playbackActions)
                .setActiveQueueItemId(mediaId.toLong())
                .setState(state, position.toLong(), 1.0f)
        mSession!!.setPlaybackState(builder.build())
    }

    /**
     * Updates the [MovieView] based on the callback actions. <br></br>
     * Simulates a playlist that will disable actions when you cannot skip through the playlist in a
     * certairection.
     */
    private inner class MediaSessionCallback(private val movieView: VideoView) : MediaSessionCompat.Callback() {
        private val PLAYLIST_SIZE = 2
        private var indexInPlaylist: Int = 0

        init {
            indexInPlaylist = 1
        }

        override fun onPlay() {
            super.onPlay()
            movieView.start()
            updatePlaybackState(
                    PlaybackStateCompat.STATE_PLAYING,
                    movieView.getCurrentPosition(),
                    R.raw.vid_bigbuckbunny)
        }

        override fun onPause() {
            super.onPause()
            movieView.pause()
            updatePlaybackState(
                    PlaybackStateCompat.STATE_PAUSED,
                    movieView.getCurrentPosition(),
                    R.raw.vid_bigbuckbunny)
        }

        /**
         * 下一个item;
         * 到底了,会显示比较暗的图片,表示已经没有下一个了;
         */
        override fun onSkipToNext() {
            super.onSkipToNext()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                initVideoView()
            }
            if (indexInPlaylist < PLAYLIST_SIZE) {
                indexInPlaylist++
                if (indexInPlaylist >= PLAYLIST_SIZE) {
                    updatePlaybackState(
                            PlaybackStateCompat.STATE_PLAYING,
                            MEDIA_ACTIONS_PLAY_PAUSE or PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS,
                            movieView.currentPosition,
                            R.raw.vid_bigbuckbunny)
                } else {
                    updatePlaybackState(
                            PlaybackStateCompat.STATE_PLAYING,
                            MEDIA_ACTIONS_ALL,
                            movieView.currentPosition,
                            R.raw.vid_bigbuckbunny)
                }
            }
        }

        override fun onSkipToPrevious() {
            super.onSkipToPrevious()
            initVideoView()
            if (indexInPlaylist > 0) {
                indexInPlaylist--
                if (indexInPlaylist <= 0) {
                    updatePlaybackState(
                            PlaybackStateCompat.STATE_PLAYING,
                            MEDIA_ACTIONS_PLAY_PAUSE or PlaybackStateCompat.ACTION_SKIP_TO_NEXT,
                            movieView.getCurrentPosition(),
                            R.raw.vid_bigbuckbunny)
                } else {
                    updatePlaybackState(
                            PlaybackStateCompat.STATE_PLAYING,
                            MEDIA_ACTIONS_ALL,
                            movieView.getCurrentPosition(),
                            R.raw.vid_bigbuckbunny)
                }
            }
        }
    }

}