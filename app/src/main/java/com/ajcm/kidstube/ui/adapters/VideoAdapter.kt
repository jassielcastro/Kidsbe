package com.ajcm.kidstube.ui.adapters

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ajcm.domain.Video
import com.ajcm.kidstube.R
import com.ajcm.kidstube.extensions.basicDiffUtil
import com.ajcm.kidstube.extensions.inflate
import com.ajcm.kidstube.extensions.loadUrl
import kotlinx.android.synthetic.main.item_video.view.*

class VideoAdapter(private val listener: (Video) -> Unit) : RecyclerView.Adapter<ViewHolder>() {

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
        holder.itemView.setOnClickListener { listener(video) }
    }

    private fun bind(video: Video, holder: ViewHolder) {
        holder.itemView.imgVideo.loadUrl(video.thumbnail)
        holder.itemView.txtTitle.text = video.title
        holder.itemView.imgOption.setOnClickListener {  }
    }

}