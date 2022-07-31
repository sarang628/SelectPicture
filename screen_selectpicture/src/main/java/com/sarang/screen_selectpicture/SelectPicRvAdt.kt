package com.sarang.screen_selectpicture

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.torang_core.data.model.MyImage
import com.sarang.torangimageloader.ImageLoadBindingAdapter.loadImage
import java.util.*

abstract class SelectPicRvAdt : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    abstract fun clickPicture(myImage: MyImage?)
    private var myImageList: ArrayList<MyImage>? = null
    var selectedImgList = ArrayList<MyImage>()
        private set
    var isCheckIn = false
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return SelectPicHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_picture_select, parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val selectPicHolder: SelectPicHolder =
            holder as SelectPicHolder
        loadImage(selectPicHolder.img, myImageList!![position].data)
        /*MyGlide.with(holder.itemView.getContext())
                .load(myImageList.get(position).getData())
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(selectPicHolder.img);*/if (selectedImgList.contains(myImageList!![position])) {
            selectPicHolder.dim.setVisibility(View.VISIBLE)
            selectPicHolder.tag.setVisibility(View.VISIBLE)
            selectPicHolder.tag.setText("" + selectedImgList.indexOf(myImageList!![position]))
        } else {
            selectPicHolder.dim.setVisibility(View.GONE)
            selectPicHolder.tag.setVisibility(View.GONE)
            selectPicHolder.tag.setText("")
        }
        (holder as SelectPicHolder).img.setOnClickListener(View.OnClickListener { view: View? ->
            clickPicture(myImageList!![position])
            val isContained = selectedImgList.contains(myImageList!![position])
            // 이미지를 선택했을때 선택 여부에 따라 딤과 카운트 보여지는 여부
            selectPicHolder.dim.setVisibility(if (isContained) View.GONE else View.VISIBLE)
            selectPicHolder.tag.setVisibility(if (isContained) View.GONE else View.VISIBLE)
            if (isContained) {
                selectedImgList.remove(myImageList!![position])
                notifyDataSetChanged()
            } else {
                if (isCheckIn) {
                    selectedImgList.removeAll(selectedImgList)
                    notifyDataSetChanged()
                }
                selectedImgList.add(myImageList!![position])
                selectPicHolder.tag.setText("" + (selectedImgList.size - 1))
            }
        })
    }

    override fun getItemCount(): Int {
        var count = 0
        if (myImageList != null) count = myImageList!!.size
        return count
    }

    fun setMyImageList(myImageList: ArrayList<MyImage>?) {
        this.myImageList = myImageList
        notifyDataSetChanged()
    }

    fun setSelectedImageList(selectedImgList: ArrayList<MyImage>) {
        this.selectedImgList = selectedImgList
    }

    fun setIsCheckIn(isCheckIn: Boolean) {
        this.isCheckIn = isCheckIn
    }
}