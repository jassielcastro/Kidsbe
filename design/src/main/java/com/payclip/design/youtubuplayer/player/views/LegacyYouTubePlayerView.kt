package com.payclip.design.youtubuplayer.player.views

import android.content.Context
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.payclip.design.R
import com.payclip.design.youtubuplayer.player.PlayerConstants
import com.payclip.design.youtubuplayer.player.YouTubePlayer
import com.payclip.design.youtubuplayer.player.listeners.AbstractYouTubePlayerListener
import com.payclip.design.youtubuplayer.player.listeners.YouTubePlayerCallback
import com.payclip.design.youtubuplayer.player.listeners.YouTubePlayerListener
import com.payclip.design.youtubuplayer.player.options.IFramePlayerOptions
import com.payclip.design.youtubuplayer.player.options.PanelState
import com.payclip.design.youtubuplayer.player.utils.NetworkListener
import com.payclip.design.youtubuplayer.player.utils.PlaybackResumer
import com.payclip.design.youtubuplayer.ui.DefaultPlayerUiController
import com.payclip.design.youtubuplayer.ui.PlayerUiController

class LegacyYouTubePlayerView(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0):
    ResponsiveFrameLayout(context, attrs, defStyleAttr), LifecycleObserver {

    constructor(context: Context): this(context, null, 0)
    constructor(context: Context, attrs: AttributeSet? = null): this(context, attrs, 0)

    val youTubePlayer: WebViewYouTubePlayer = WebViewYouTubePlayer(context)
    private val defaultPlayerUiController: DefaultPlayerUiController

    private val networkListener = NetworkListener()
    private val playbackResumer = PlaybackResumer()

    internal var isYouTubePlayerReady = false
    private var initialize = { }
    private val youTubePlayerCallbacks = HashSet<YouTubePlayerCallback>()

    private var canPlay = true

    private var isUsingCustomUi = false

    init {

        addView(youTubePlayer, LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))
        defaultPlayerUiController = DefaultPlayerUiController(this, youTubePlayer)

        defaultPlayerUiController.addSeekBarListener {
            //youTubePlayer.seekTo(it.toFloat())
        }

        youTubePlayer.addListener(defaultPlayerUiController)
        youTubePlayer.addListener(playbackResumer)

        // stop playing if the user loads a video but then leaves the app before the video starts playing.
        youTubePlayer.addListener(object : AbstractYouTubePlayerListener() {
            override fun onStateChange(youTubePlayer: YouTubePlayer, state: PlayerConstants.PlayerState) {
                if(state == PlayerConstants.PlayerState.PLAYING && !isEligibleForPlayback())
                    youTubePlayer.pause()
            }
        })

        youTubePlayer.addListener(object : AbstractYouTubePlayerListener() {
            override fun onReady(youTubePlayer: YouTubePlayer) {
                isYouTubePlayerReady = true

                youTubePlayerCallbacks.forEach { it.onYouTubePlayer(youTubePlayer) }
                youTubePlayerCallbacks.clear()

                youTubePlayer.removeListener(this)
            }
        })

        networkListener.onNetworkAvailable = {
            if (!isYouTubePlayerReady)
                initialize()
            else
                playbackResumer.resume(youTubePlayer)
        }
    }

    /**
     * Initialize the player. You must call this method before using the player.
     * @param youTubePlayerListener listener for player events
     * @param handleNetworkEvents if set to true a broadcast receiver will be registered and network events will be handled automatically.
     * If set to false, you should handle network events with your own broadcast receiver.
     * @param playerOptions customizable options for the embedded video player, can be null.
     */
    fun initialize(youTubePlayerListener: YouTubePlayerListener, handleNetworkEvents: Boolean, playerOptions: IFramePlayerOptions?) {
        if(isYouTubePlayerReady)
            throw IllegalStateException("This YouTubePlayerView has already been initialized.")

        if (handleNetworkEvents)
            context.registerReceiver(networkListener, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))

        initialize = {
            youTubePlayer.initialize({it.addListener(youTubePlayerListener)}, playerOptions)
        }

        if(!handleNetworkEvents)
            initialize()
    }

    /**
     * Initialize the player.
     * @param handleNetworkEvents if set to true a broadcast receiver will be registered and network events will be handled automatically.
     * If set to false, you should handle network events with your own broadcast receiver.
     *
     * @see LegacyYouTubePlayerView.initialize
     */
    fun initialize(youTubePlayerListener: YouTubePlayerListener, handleNetworkEvents: Boolean) =
        initialize(youTubePlayerListener, handleNetworkEvents, null)

    /**
     * Initialize the player. Network events are automatically handled by the player.
     * @param youTubePlayerListener listener for player events
     *
     * @see LegacyYouTubePlayerView.initialize
     */
    fun initialize(youTubePlayerListener: YouTubePlayerListener) =
        initialize(youTubePlayerListener, true)

    /**
     * Initialize a player using the web-base Ui instead pf the native Ui.
     * The default PlayerUiController will be removed and [LegacyYouTubePlayerView.getPlayerUiController] will throw exception.
     *
     * @see LegacyYouTubePlayerView.initialize
     */
    fun initializeWithWebUi(youTubePlayerListener: YouTubePlayerListener, handleNetworkEvents: Boolean) {
        val iFramePlayerOptions = IFramePlayerOptions.Builder().controls(1).build()
        inflateCustomPlayerUi(R.layout.ayp_empty_layout)
        initialize(youTubePlayerListener, handleNetworkEvents, iFramePlayerOptions)
    }

    /**
     * @param youTubePlayerCallback A callback that will be called when the YouTubePlayer is ready.
     * If the player is ready when the function is called, the callback is called immediately.
     * This function is called only once.
     */
    fun getYouTubePlayerWhenReady(youTubePlayerCallback: YouTubePlayerCallback) {
        if(isYouTubePlayerReady)
            youTubePlayerCallback.onYouTubePlayer(youTubePlayer)
        else
            youTubePlayerCallbacks.add(youTubePlayerCallback)
    }

    /**
     * Use this method to replace the default Ui of the player with a custom Ui.
     *
     * You will be responsible to manage the custom Ui from your application,
     * the default controller obtained through [LegacyYouTubePlayerView.getPlayerUiController] won't be available anymore.
     * @param layoutId the ID of the layout defining the custom Ui.
     * @return The inflated View
     */
    fun inflateCustomPlayerUi(@LayoutRes layoutId: Int): View {
        removeViews(1, childCount - 1)

        if (!isUsingCustomUi) {
            youTubePlayer.removeListener(defaultPlayerUiController)
        }

        isUsingCustomUi = true

        return View.inflate(context, layoutId, this)
    }

    /**
     * Call this method before destroying the host Fragment/Activity, or register this View as an observer of its host lifecycle
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun release() {
        removeView(youTubePlayer)
        youTubePlayer.removeAllViews()
        youTubePlayer.destroy()
        try {
            context.unregisterReceiver(networkListener)
        } catch (ignore: Exception) {
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    internal fun onResume() {
        playbackResumer.onLifecycleResume()
        canPlay = true
        defaultPlayerUiController.onResume()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    internal fun onStop() {
        youTubePlayer.pause()
        playbackResumer.onLifecycleStop()
        canPlay = false
    }

    fun loadThumbnailImage(thumbnail: String) {
        defaultPlayerUiController.loadThumbnailImage(thumbnail)
    }

    /**
     * Checks whether the player is in an eligible state for playback in
     * respect of the {@link WebViewYouTubePlayer#isBackgroundPlaybackEnabled}
     * property.
     */
    internal fun isEligibleForPlayback(): Boolean {
        return canPlay || youTubePlayer.isBackgroundPlaybackEnabled
    }

    /**
     * Don't use this method if you want to publish your app on the PlayStore. Background playback is against YouTube terms of service.
     */
    fun enableBackgroundPlayback(enable: Boolean) {
        youTubePlayer.isBackgroundPlaybackEnabled = enable
    }

    fun getPlayerUiController(): PlayerUiController {
        if (isUsingCustomUi)
            throw RuntimeException("You have inflated a custom player Ui. You must manage it with your own controller.")

        return defaultPlayerUiController
    }

    fun setOnPanelListener(listener: (PanelState) -> Unit) {
        defaultPlayerUiController.setOnPanelListener(listener)
    }

    fun setOnVideoFinishListener(finishListener: () -> Unit) {
        defaultPlayerUiController.setOnVideoFinishListener(finishListener)
    }

    fun canHideContent(hideContent: Boolean) {
        defaultPlayerUiController.canHideContent(hideContent)
    }
}