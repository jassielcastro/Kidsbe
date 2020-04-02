package com.ajcm.kidstube.ui.adapters

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ajcm.domain.Video
import com.ajcm.kidstube.R
import com.ajcm.design.extensions.basicDiffUtil
import com.ajcm.design.extensions.inflate
import com.ajcm.design.extensions.loadUrl
import kotlinx.android.synthetic.main.item_related_video.view.*

class RelatedVideosAdapter(private val listener: (Video) -> Unit) : RecyclerView.Adapter<ViewHolder>() {

    var videos: List<Video> by basicDiffUtil(
        emptyList(),
        areItemsTheSame = { old, new -> old.videoId == new.videoId }
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = parent.inflate(R.layout.item_related_video)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = videos.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val video = videos[position]
        holder.itemView.imgVideo.loadUrl(video.thumbnail, 8)
        holder.itemView.setOnClickListener { listener(video) }
    }

}