package com.graduation.teamwork.ui.base

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

/**
 * Created by TranTien on 3/3/21.
 */

abstract class BaseAdapter<Item, B : ViewBinding>(
    data: List<Item>
) : RecyclerView.Adapter<BaseAdapter.ItemVH<B>>() {

    protected var currentList: MutableList<Item> = mutableListOf()

    init {
        currentList = data.toMutableList()
    }

    abstract fun onCreateVH(parent: ViewGroup, viewType: Int): ItemVH<B>
    abstract fun onBindVH(holder: ItemVH<B>, position: Int)
    open fun getItemSize() : Int = currentList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemVH<B> =
        onCreateVH(parent, viewType)

    override fun onBindViewHolder(holder: ItemVH<B>, position: Int) = onBindVH(holder, position)

    override fun getItemCount(): Int = getItemSize()
    protected open fun getItem(position: Int) = currentList[position]

    open fun submitList(list: List<Item>) {
        currentList.clear()
        currentList.addAll(list)

        notifyDataSetChanged()
    }

    fun appendList(list: List<Item>) {
        currentList.addAll(list)
        notifyItemRangeInserted(currentList.size - list.size, list.size)
    }

    fun resetData() {
        currentList.clear()
        notifyDataSetChanged()
    }

    class ItemVH<B : ViewBinding>(val binding: B) : RecyclerView.ViewHolder(binding.root)
}
