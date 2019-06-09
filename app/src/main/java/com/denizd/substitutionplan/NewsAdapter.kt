package com.denizd.substitutionplan

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import java.util.ArrayList
import androidx.recyclerview.widget.RecyclerView

class NewsAdapter(news: ArrayList<News>) : RecyclerView.Adapter<NewsAdapter.CardViewHolder>() {
    private var mNews: List<News>? = null

    class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var title = itemView.findViewById<TextView>(R.id.title)
        var content = itemView.findViewById<TextView>(R.id.content)
    }

    init {
        mNews = news
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.news_card, parent, false)
        return CardViewHolder(v)
    }

    fun getNewsAt(i: Int): News {
        return mNews!![i]
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val currentItem = mNews!![position]

        holder.title.text = currentItem.title
        holder.content.text = currentItem.content
    }

    override fun getItemCount(): Int {
        return mNews!!.size
    }

    fun setNews(news: List<News>) {
        mNews = news
        notifyDataSetChanged()
    }
}
