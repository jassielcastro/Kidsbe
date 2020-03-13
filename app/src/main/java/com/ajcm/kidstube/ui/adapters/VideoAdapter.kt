package com.ajcm.kidstube.ui.adapters

import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.ajcm.domain.Video
import com.ajcm.kidstube.R
import com.ajcm.kidstube.common.VideoAction
import com.payclip.design.extensions.*
import kotlinx.android.synthetic.main.item_video.view.*
import kotlinx.android.synthetic.main.item_video_options.view.*

class VideoAdapter(private val listener: (VideoAction) -> Unit) : RecyclerView.Adapter<ViewHolder>() {

    var videos: List<Video> by basicDiffUtil(
        emptyList(),
        areItemsTheSame = { old, new -> old.videoId == new.videoId }
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = parent.inflate(R.layout.item_video)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = videos.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val video = videos[position]
        bind(video, holder)
        holder.itemView.setOnClickListener { listener(VideoAction.Play(video)) }
    }

    private fun bind(video: Video, holder: ViewHolder) {
        holder.itemView.imgVideo.loadUrl(video.thumbnail)
        holder.itemView.txtTitle.text = video.title
        holder.itemView.imgOption.setOnClickListener {
            if (holder.itemView.videoOptions.isVisible) {
                holder.itemView.videoOptions.hide()
            } else {
                holder.itemView.videoOptions.show()
            }
        }

        holder.itemView.videoOptions.hide()

        holder.itemView.videoOptions.btnVideoBlock.setOnClickListener {
            listener(VideoAction.Block(video))
        }
        holder.itemView.videoOptions.btnVideoRelated.setOnClickListener {
            listener(VideoAction.RelatedTo(video))
        }
    }

}