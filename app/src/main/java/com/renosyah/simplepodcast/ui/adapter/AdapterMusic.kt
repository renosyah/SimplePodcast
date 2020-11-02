package com.renosyah.simplepodcast.ui.adapter

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.renosyah.simplepodcast.R
import com.renosyah.simplepodcast.model.music.Music
import com.squareup.picasso.Picasso

class AdapterMusic : RecyclerView.Adapter<AdapterMusic.Holder>{

    lateinit var context: Context
    lateinit var stores : ArrayList<Music>
    lateinit var onClick : onMusicClickListener

    constructor(context: Context, stores: ArrayList<Music>, onClick : onMusicClickListener) : super() {
        this.context = context
        this.stores = stores
        this.onClick = onClick
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder((context as Activity).layoutInflater.inflate(R.layout.adapter_music,parent,false))
    }

    override fun getItemCount(): Int {
        return stores.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
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
    }

    class Holder : RecyclerView.ViewHolder {

        lateinit var image :ImageView
        lateinit var name : TextView
        lateinit var layout : LinearLayout

        constructor(itemView: View) : super(itemView){
            this.image = itemView.findViewById(R.id.music_image_imageview)
            this.name  = itemView.findViewById(R.id.music_title_textview)
            this.layout = itemView.findViewById(R.id.adapter_layout)
        }
    }

    interface onMusicClickListener {
        fun onImageClick(m : Music,pos : Int)
        fun onTitleClick(m : Music,pos : Int)
    }
}