package com.graduation.teamwork.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.graduation.teamwork.databinding.ItNotiBinding
import com.graduation.teamwork.models.firebase.InfoNoti

class NotiAdapter(
    list: List<InfoNoti>,
    private val onItemClicked: (item: InfoNoti) -> Unit
) : RecyclerView.Adapter<NotiAdapter.NotiViewHolder>() {

    init {
        currentList = list.toMutableList()
    }

    companion object {
        var currentList = mutableListOf<InfoNoti>()
    }

    private fun getItem(position: Int) = currentList[position]

    private val TAG = "__NotiAdapter"
    fun submitList(listItem: List<InfoNoti>) {
        Log.d(TAG, "submitList: ${listItem.size}")
        currentList.clear()

        currentList.addAll(listItem)

        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotiAdapter.NotiViewHolder =
        ItNotiBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        ).let(::NotiViewHolder)

    inner class NotiViewHolder(val binding: ItNotiBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bindData(item: InfoNoti) {
            with(binding) {
                root.setOnClickListener {
                    onItemClicked(item)
                }

                tvInfo.text = item.content
                tvTimeNoti.text = item.timestamp
            }
        }
    }

    override fun onBindViewHolder(holder: NotiAdapter.NotiViewHolder, position: Int) {
        holder.bindData(getItem(position))
    }

    override fun getItemCount(): Int = currentList.size
}