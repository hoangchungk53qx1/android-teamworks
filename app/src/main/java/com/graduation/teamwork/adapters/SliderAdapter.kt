package com.graduation.teamwork.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.graduation.teamwork.R
import com.graduation.teamwork.databinding.ItSliderGroupBinding
import com.graduation.teamwork.models.DtGroup
import com.graduation.teamwork.models.data.TaskListItem
import com.graduation.teamwork.models.slider.Slider

/**
 * com.graduation.teamwork.adapters
 * Created on 11/14/20
 */

class SliderAdapter(
    listItem: List<DtGroup>,
    private val onItemClicked: ((item: DtGroup, position: Int) -> Unit)
) : RecyclerView.Adapter<SliderAdapter.SliderViewHolder>() {

    private fun getItem(position: Int): DtGroup {
        return currentList[position]
    }

    fun submitList(listItem: List<DtGroup>) {
        currentList.clear()
        currentList.addAll(listItem)

        notifyDataSetChanged()
    }

    init {
        currentList = listItem.toMutableList()
    }

    companion object{
        var currentList = mutableListOf<DtGroup>()
    }

    inner class SliderViewHolder(private val binding: ItSliderGroupBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindData(data: DtGroup) {

            with(binding) {
                lnSlideGroup.setOnClickListener {
                    onItemClicked(data, adapterPosition)
                }

//                imgIcon.setImageResource(
//                    root.context.resources.getDrawable()
//                )
                tvName.text = data.name
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SliderViewHolder =
        ItSliderGroupBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        ).let(::SliderViewHolder)

    override fun onBindViewHolder(holder: SliderViewHolder, position: Int) {
        holder.bindData(getItem(position))
    }

    override fun getItemCount(): Int = currentList.size
}