package com.trendyol.bubblescrollbar

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

class SampleAdapter : RecyclerView.Adapter<SampleAdapter.SampleViewHolder>(){

    val data = mutableListOf<String>()

    init {
        for (index in 1..100) {
            data.add(index.toString())
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int) =
        SampleViewHolder(LayoutInflater.from(viewGroup.context).inflate(R.layout.item_sample, viewGroup, false))

    override fun getItemCount() = data.size

    override fun onBindViewHolder(viewHolder: SampleViewHolder, adapterPosition: Int) {
        viewHolder.bind(data[adapterPosition])
    }

    inner class SampleViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val textView: TextView = view.findViewById(R.id.text)

        fun bind(item: String) {
            textView.text = item
        }
    }
}
