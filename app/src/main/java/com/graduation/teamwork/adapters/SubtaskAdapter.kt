package com.graduation.teamwork.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.graduation.teamwork.databinding.ItRoomDetailBinding
import com.graduation.teamwork.models.DtSubtask

class SubtaskAdapter(
    listItem: List<DtSubtask>
) : RecyclerView.Adapter<SubtaskAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItRoomDetailBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bindData(data: DtSubtask) {
            with(binding) {
                cbTask.text = data.name
                cbTask.isChecked = data.isCompleted == true
            }
        }
    }

    init {
        currentList = listItem.toMutableList()
    }

    companion object {
        private var currentList = mutableListOf<DtSubtask>()
    }

    private fun getItem(position: Int) = currentList[position]

    fun submitList(listItem: List<DtSubtask>) {
        currentList.clear()
        currentList.addAll(listItem)

        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ItRoomDetailBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        ).let(::ViewHolder)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindData(getItem(position))
    }

    override fun getItemCount(): Int = currentList.size
}