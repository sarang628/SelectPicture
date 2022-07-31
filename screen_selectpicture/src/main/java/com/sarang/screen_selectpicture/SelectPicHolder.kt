package com.sarang.screen_selectpicture

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class SelectPicHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var img: ImageView
    var dim: ImageView
    var tag: TextView

    init {
        img = itemView.findViewById(R.id.img)
        dim = itemView.findViewById(R.id.dim)
        tag = itemView.findViewById(R.id.imgtag)
    }
}