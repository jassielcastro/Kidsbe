package com.payclip.design.extensions

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.annotation.LayoutRes
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.payclip.design.R
import kotlin.properties.Delegates

fun Activity.fullScreen() {
    window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
    val decorView = window.decorView
    val uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
    decorView.systemUiVisibility = uiOptions
}

fun Activity.accelerateViews() {
    window.setFlags(
        WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
        WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED)

    this.window.decorView.accelerateCanvas()
}

fun View.accelerateCanvas() {
    setLayerType(View.LAYER_TYPE_HARDWARE, null)
}

fun View.show() {
    apply {
        alpha = 0f
        visibility = View.VISIBLE

        animate()
            .alpha(1f)
            .setDuration(320L)
            .setListener(null)
    }
}

fun View.hide() {
    apply {
        visibility = View.GONE
    }
}

fun ViewGroup.inflate(@LayoutRes layoutRes: Int, attachToRoot: Boolean = false): View =
    LayoutInflater.from(context).inflate(layoutRes, this, attachToRoot)

fun ImageView.loadUrl(url: String, round: Int = 0) {
    Glide.with(context)
        .load(url)
        .placeholder(R.drawable.place_holder_image)
        .transform(CenterCrop(), RoundedCorners(round))
        .into(this)
}

fun ImageView.loadUrlWithAspectRatio(url: String) {
    this.post {
        Glide.with(context)
            .load(url)
            .centerCrop()
            .override(Target.SIZE_ORIGINAL, this.height)
            .into(this)
    }
}

fun ImageView.loadRes(@DrawableRes drawable: Int, circleCrop: Boolean = true) {
    if (circleCrop) {
        Glide.with(context).load(drawable).circleCrop().into(this)
    } else {
        Glide.with(context).load(drawable).into(this)
    }
}

inline fun <VH : RecyclerView.ViewHolder, T> RecyclerView.Adapter<VH>.basicDiffUtil(
    initialValue: List<T>,
    crossinline areItemsTheSame: (T, T) -> Boolean = { old, new -> old == new },
    crossinline areContentsTheSame: (T, T) -> Boolean = { old, new -> old == new }
) =
    Delegates.observable(initialValue) { _, old, new ->
        DiffUtil.calculateDiff(object : DiffUtil.Callback() {
            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
                areItemsTheSame(old[oldItemPosition], new[newItemPosition])

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
                areContentsTheSame(old[oldItemPosition], new[newItemPosition])

            override fun getOldListSize(): Int = old.size

            override fun getNewListSize(): Int = new.size
        }).dispatchUpdatesTo(this@basicDiffUtil)
    }

fun RecyclerView.setUpLayoutManager(orientation: Int = RecyclerView.HORIZONTAL, reversed: Boolean = false) {
    this.layoutManager = LinearLayoutManager(this.context, orientation, reversed)
}

fun RecyclerView.setSmoothScroll(position: Int) {
    val smoothScroller = object : LinearSmoothScroller(this.context) {
        override fun getHorizontalSnapPreference(): Int {
            return SNAP_TO_START
        }
    }

    smoothScroller.targetPosition = position

    this.layoutManager?.startSmoothScroll(smoothScroller)
}

fun Fragment.waitForTransition(targetView: View) {
    postponeEnterTransition()
    targetView.doOnPreDraw { startPostponedEnterTransition() }
}

fun View.hideKeyboard() {
    val inputMethodManager: InputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(this.windowToken, 0)
}