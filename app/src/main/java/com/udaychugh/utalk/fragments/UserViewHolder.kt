package com.udaychugh.utalk.fragments

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.udaychugh.utalk.User
import kotlinx.android.synthetic.main.item_user.view.*

class UserViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
    fun bind(user : User) {
        itemView.titleTv.text = user.name
        Picasso.get().load(user.imageUrl).into(itemView.userImgView)
    }

}