package com.ajcm.kidstube.ui.adapters

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ajcm.domain.Avatar
import com.ajcm.kidstube.R
import com.ajcm.kidstube.extensions.getDrawable
import com.ajcm.kidstube.ui.profile.ItemAvatar
import com.ajcm.design.extensions.*
import kotlinx.android.synthetic.main.item_avatar.view.*

class AvatarAdapter(private val listener: (Avatar) -> Unit) : RecyclerView.Adapter<ViewHolder>() {

    var avatarList: List<ItemAvatar> by basicDiffUtil(
        emptyList(),
        areItemsTheSame = { old, new -> old.first == new.first }
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = parent.inflate(R.layout.item_avatar)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = avatarList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val avatar = avatarList[position]
        holder.itemView.imgAvatar.loadRes(avatar.first.getDrawable())

        if (avatar.second) {
            holder.itemView.viewSelected.show()
        } else {
            holder.itemView.viewSelected.hide()
        }

        holder.itemView.setOnClickListener { listener(avatar.first) }
    }

}