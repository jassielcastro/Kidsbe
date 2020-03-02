package com.payclip.design.youtubuplayer.player.views

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.payclip.design.youtubuplayer.player.listeners.AbstractYouTubePlayerListener
import com.payclip.design.youtubuplayer.player.listeners.YouTubePlayerCallback
import com.payclip.design.youtubuplayer.player.listeners.YouTubePlayerListener
import com.payclip.design.youtubuplayer.player.options.IFramePlayerOptions
import com.payclip.design.youtubuplayer.player.options.PanelState
import com.payclip.design.youtubuplayer.ui.PlayerUiController

class YouTubePlayerView(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    ResponsiveFrameLayout(context, attrs, defStyleAttr),
    LifecycleObserver {

    constructor(context: Context): this(context, null, 0)
    constructor(context: Context, attrs: AttributeSet? = null): this(context, attrs, 0)

    private val legacyTubePlayerView: LegacyYouTubePlayerView = LegacyYouTubePlayerView(context)

    private var enableAutomaticInitialization: Boolean

    init {
        addView(legacyTubePlayerView, LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))

        enableAutomaticInitialization = true
        val handleNetworkEvents = true

        val youTubePlayerListener = object : AbstractYouTubePlayerListener() {}

        legacyTubePlayerView.initialize(youTubePlayerListener, handleNetworkEvents)

        legacyTubePlayerView.getPlayerUiController()
            .enableLiveVideoUi(false)
            .showYouTubeButton(false)
            .showFullscreenButton(false)
            .showMenuButton(false)
            .showCurrentTime(true)
            .showDuration(true)
            .showSeekBar(true)
    }

    /**
     * Initialize the player. You must call this method before using the player.
     * @param youTubePlayerListener listener for player events
     * @param handleNetworkEvents if set to true a broadcast receiver will be registered and network events will be handled automatically.
     * If set to false, you should handle network events with your own broadcast receiver.
     * @param playerOptions customizable options for the embedded video player, can be null.
     */
    fun initialize(youTubePlayerListener: YouTubePlayerListener, handleNetworkEvents: Boolean, playerOptions: IFramePlayerOptions?) {
        if (enableAutomaticInitialization) throw IllegalStateException("YouTubePlayerView: If you want to initialize this view manually, you need to set 'enableAutomaticInitialization' to false")
        else legacyTubePlayerView.initialize(youTubePlayerListener, handleNetworkEvents, playerOptions)
    }

    /**
     * Initialize the player.
     * @param handleNetworkEvents if set to true a broadcast receiver will be registered and network events will be handled automatically.
     * If set to false, you should handle network events with your own broadcast receiver.
     *
     * @see YouTubePlayerView.initialize
     */
    fun initialize(youTubePlayerListener: YouTubePlayerListener, handleNetworkEvents: Boolean) {
        if(enableAutomaticInitialization) throw IllegalStateException("YouTubePlayerView: If you want to initialize this view manually, you need to set 'enableAutomaticInitialization' to false")
        else legacyTubePlayerView.initialize(youTubePlayerListener, handleNetworkEvents, null)
    }

    /**
     * Initialize the player. Network events are automatically handled by the player.
     * @param youTubePlayerListener listener for player events
     *
     * @see YouTubePlayerView.initialize
     */
    fun initialize(youTubePlayerListener: YouTubePlayerListener) {
        if (enableAutomaticInitialization) throw IllegalStateException("YouTubePlayerView: If you want to initialize this view manually, you need to set 'enableAutomaticInitialization' to false")
        else legacyTubePlayerView.initialize(youTubePlayerListener, true)
    }

    /**
     * Initialize a player using the web-base Ui instead pf the native Ui.
     * The default PlayerUiController will be removed and [YouTubePlayerView.getPlayerUiController] will throw exception.
     *
     * @see YouTubePlayerView.initialize
     */
    fun initializeWithWebUi(youTubePlayerListener: YouTubePlayerListener, handleNetworkEvents: Boolean) {
        if(enableAutomaticInitialization) throw IllegalStateException("YouTubePlayerView: If you want to initialize this view manually, you need to set 'enableAutomaticInitialization' to false")
        else legacyTubePlayerView.initializeWithWebUi(youTubePlayerListener, handleNetworkEvents)
    }

    /**
     * @param youTubePlayerCallback A callback that will be called when the YouTubePlayer is ready.
     * If the player is ready when the function is called, the callback is called immediately.
     * This function is called only once.
     */
    fun getYouTubePlayerWhenReady(youTubePlayerCallback: YouTubePlayerCallback) =
        legacyTubePlayerView.getYouTubePlayerWhenReady(youTubePlayerCallback)

    /**
     * Use this method to replace the default Ui of the player with a custom Ui.
     *
     * You will be responsible to manage the custom Ui from your application,
     * the default controller obtained through [YouTubePlayerView.getPlayerUiController] won't be available anymore.
     * @param layoutId the ID of the layout defining the custom Ui.
     * @return The inflated View
     */
    fun inflateCustomPlayerUi(@LayoutRes layoutId: Int): View = legacyTubePlayerView.inflateCustomPlayerUi(layoutId)

    fun getPlayerUiController(): PlayerUiController = legacyTubePlayerView.getPlayerUiController()

    /**
     * Don't use this method if you want to publish your app on the PlayStore. Background playback is against YouTube terms of service.
     */
    fun enableBackgroundPlayback(enable: Boolean) = legacyTubePlayerView.enableBackgroundPlayback(enable)

    /**
     * Call this method before destroying the host Fragment/Activity, or register this View as an observer of its host lifecycle
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun release() = legacyTubePlayerView.release()

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onResume() = legacyTubePlayerView.onResume()

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onStop() = legacyTubePlayerView.onStop()

    fun addYouTubePlayerListener(youTubePlayerListener: YouTubePlayerListener) =
        legacyTubePlayerView.youTubePlayer.addListener(youTubePlayerListener)

    fun removeYouTubePlayerListener(youTubePlayerListener: YouTubePlayerListener) =
        legacyTubePlayerView.youTubePlayer.removeListener(youTubePlayerListener)

    fun setOnPanelListener(listener: (PanelState) -> Unit) {
        legacyTubePlayerView.setOnPanelListener(listener)
    }

    fun setOnVideoFinishListener(finishListener: () -> Unit) {
        legacyTubePlayerView.setOnVideoFinishListener(finishListener)
    }
}