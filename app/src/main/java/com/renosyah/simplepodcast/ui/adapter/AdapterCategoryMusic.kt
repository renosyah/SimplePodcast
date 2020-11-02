package com.renosyah.simplepodcast.ui.adapter

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.renosyah.simplepodcast.R
import com.renosyah.simplepodcast.model.category.Category

class AdapterCategoryMusic : RecyclerView.Adapter<AdapterCategoryMusic.Holder> {

    lateinit var context: Context
    lateinit var categories : ArrayList<Category>
    lateinit var onSeeAllClick : (Category,Int) -> Unit
    lateinit var onMusicClick : AdapterMusic.onMusicClickListener

    constructor(context: Context, categories: ArrayList<Category>, onSeeAllClick: (Category, Int) -> Unit, onMusicClick: AdapterMusic.onMusicClickListener) : super() {
        this.context = context
        this.categories = categories
        this.onSeeAllClick = onSeeAllClick
        this.onMusicClick = onMusicClick
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder((context as Activity).layoutInflater.inflate(R.layout.adapter_category_music, parent, false))
    }

    override fun getItemCount(): Int {
        return categories.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val item = categories[position]

        holder.name.text = item.name
        holder.seeAll.setOnClickListener {
            onSeeAllClick.invoke(item,position)
        }

        val adapterMusic = AdapterMusic(context, item.musics,onMusicClick)
        holder.musics.adapter = adapterMusic
        holder.musics.layoutManager = LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false)
        adapterMusic.notifyDataSetChanged()
    }

    class Holder : RecyclerView.ViewHolder {

        lateinit var name : TextView
        lateinit var musics : RecyclerView
        lateinit var seeAll : TextView

        constructor(itemView: View) : super(itemView){
            this.name  = itemView.findViewById(R.id.category_name_text_view)
            this.musics = itemView.findViewById(R.id.music_recycle_view)
            this.seeAll = itemView.findViewById(R.id.category_see_all_text_view)
        }
    }
}