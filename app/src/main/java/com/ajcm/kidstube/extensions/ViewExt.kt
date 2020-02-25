package com.ajcm.kidstube.extensions

import android.animation.Animator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.annotation.LayoutRes
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlin.properties.Delegates

fun View.show() {
    if (!this.isVisible) {
        apply {
            alpha = 0f
            visibility = View.VISIBLE

            animate()
                .alpha(1f)
                .setDuration(320L)
                .setListener(null)
        }
    }
}

fun View.hide() {
    if (this.isVisible) {
        apply {
            alpha = 1f
            animate()
                .alpha(0f)
                .setDuration(200L)
                .setListener(object : Animator.AnimatorListener {
                    override fun onAnimationRepeat(p0: Animator?) {}

                    override fun onAnimationEnd(p0: Animator?) {
                        this@apply.visibility = View.GONE
                    }

                    override fun onAnimationCancel(p0: Animator?) {
                        this@apply.visibility = View.GONE
                    }

                    override fun onAnimationStart(p0: Animator?) {}

                })
        }
    }
}

fun ViewGroup.inflate(@LayoutRes layoutRes: Int, attachToRoot: Boolean = false): View =
    LayoutInflater.from(context).inflate(layoutRes, this, attachToRoot)

fun ImageView.loadUrl(url: String) {
    Glide.with(context).load(url).into(this)
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