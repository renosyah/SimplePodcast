package com.renosyah.simplepodcast.ui.adapter

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.renosyah.simplepodcast.R
import com.renosyah.simplepodcast.model.category.Category
import com.renosyah.simplepodcast.model.music.Music
import com.squareup.picasso.Picasso


class AdapterMusicWithHeader : RecyclerView.Adapter<RecyclerView.ViewHolder>{

    companion object {
        val TYPE_HEADER = 0
        val TYPE_ITEM = 1
    }

    lateinit var context: Context
    lateinit var stores : ArrayList<Music>
    lateinit var category: Category
    lateinit var onClick : AdapterMusic.onMusicClickListener

    constructor(context: Context, stores: ArrayList<Music>,category: Category, onClick : AdapterMusic.onMusicClickListener) : super() {
        this.context = context
        this.stores = stores
        this.category = category
        this.onClick = onClick
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == TYPE_ITEM) {
            return ItemHolder((context as Activity).layoutInflater.inflate(R.layout.adapter_music,parent,false))
        }
        return HeaderHolder((context as Activity).layoutInflater.inflate(R.layout.category_header,parent,false))
    }

    override fun getItemCount(): Int {
        return stores.size
    }

    override fun getItemViewType(position: Int): Int {
        if (position == 0) {
            return TYPE_HEADER
        }
        return TYPE_ITEM
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        if (holder is ItemHolder){
            val item = stores[position]

            Picasso.get()
                .load(item.imageCoverUrl)
                .into(holder.image)

            holder.name.text = item.title
            holder.name.setOnClickListener {
                onClick.onTitleClick(item,position)
            }
            holder.image.setOnClickListener {
                onClick.onImageClick(item,position)
            }

        } else if (holder is HeaderHolder){

            Picasso.get()
                .load(category.imageUrl)
                .into(holder.imageHeader)

            holder.titleHeader.text = category.name
        }
    }

    class HeaderHolder : RecyclerView.ViewHolder {

        lateinit var imageHeader :ImageView
        lateinit var titleHeader :TextView

        constructor(itemView: View) : super(itemView){
            this.imageHeader = itemView.findViewById(R.id.category_image_view)
            this.titleHeader= itemView.findViewById(R.id.title_category_text_view)
        }
    }

    class ItemHolder : RecyclerView.ViewHolder {

        lateinit var image :ImageView
        lateinit var name : TextView
        lateinit var layout : LinearLayout

        constructor(itemView: View) : super(itemView){
            this.image = itemView.findViewById(R.id.music_image_imageview)
            this.name  = itemView.findViewById(R.id.music_title_textview)
            this.layout = itemView.findViewById(R.id.adapter_layout)
        }
    }


}