package com.qxtao.viewanalysis.ui.widget.layoutinfoview.infopage

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.qxtao.viewanalysis.databinding.ItemViewInfoBinding

class InfoAdapter(val context: Context) : RecyclerView.Adapter<InfoAdapter.ViewHolder>() {
    private val list = arrayListOf<ItemInfo>()

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val binding = ItemViewInfoBinding.inflate(LayoutInflater.from(context), p0, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setItems(list: ArrayList<ItemInfo>) {
        this.list.clear()
        if (list.isNotEmpty()) {
            this.list.addAll(list)
        }
        notifyDataSetChanged()
    }

    fun addItem(index: Int, item: ItemInfo) {
        if (this.list.size < index) {
            return
        }
        list.add(index, item)
        notifyItemInserted(index)
    }

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        val itemInfo = list[p1]
        p0.bind(itemInfo)
    }

    fun notifyItemChanged(enforceItem: ItemInfo) {
        val index = list.indexOf(enforceItem)
        if (index >= 0) {
            notifyItemChanged(index)
        }
    }

    inner class ViewHolder(private val binding: ItemViewInfoBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(itemInfo: ItemInfo) {
            binding.titleTv.text = itemInfo.title
            binding.contentTv.text = itemInfo.content.toString()
            binding.contentTv.setTextIsSelectable(itemInfo.clickListener == null)
            binding.contentTv.setOnClickListener {
                itemInfo.clickListener?.run {
                    onClick(it)
                }
            }
        }
    }
}