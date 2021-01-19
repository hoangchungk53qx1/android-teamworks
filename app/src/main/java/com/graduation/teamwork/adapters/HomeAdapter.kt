package com.graduation.teamwork.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.graduation.teamwork.R
import com.graduation.teamwork.databinding.ItHeaderTaskBinding
import com.graduation.teamwork.databinding.ItTaskBinding
import com.graduation.teamwork.extensions.normalizeString
import com.graduation.teamwork.models.data.TaskListItem

class HomeAdapter(
    listItem: List<TaskListItem>,
    private val onItemClicked: (item: TaskListItem) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>(), Filterable {

    private val TAG = "__HomeAdapter"

    init {
        currentList = listItem.toMutableList()
        filteredList = listItem.toMutableList()
    }

    companion object {
        var currentList = mutableListOf<TaskListItem>()
        var filteredList = mutableListOf<TaskListItem>()
        const val TYPE_HEADER = 1
        const val TYPE_DATA = 2
    }

    private fun getItem(position: Int): TaskListItem = filteredList[position]

    fun submitList(listItem: List<TaskListItem>) {
        currentList.clear()
        currentList.addAll(listItem)

        filteredList.clear()
        filteredList.addAll(listItem)

        notifyDataSetChanged()
        notifyItemRangeInserted(0, filteredList.size-1)

    }

    override fun getItemViewType(position: Int): Int =
        when (getItem(position)) {
            is TaskListItem.Header -> TYPE_HEADER
            else -> TYPE_DATA
        }

    inner class HomeViewHolder(private val binding: ItTaskBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bindData(dtTask: TaskListItem.DtTask) {
            binding.run {
                root.setOnClickListener {
                    onItemClicked(dtTask)
                }
                tvNameTask.text = dtTask.name
            }
        }
    }

    inner class HeaderViewHolder(private val binding: ItHeaderTaskBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindData(header: TaskListItem.Header) {
            binding.run {
                root.setOnClickListener {
                    onItemClicked(header)
                }
                tvHeader.text = header.name

                Glide.with(root.context)
                    .load(header.image)
                    .placeholder(R.drawable.bg_demo)
                    .into(imgHeader)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        when (viewType) {
            TYPE_HEADER -> ItHeaderTaskBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ).let(::HeaderViewHolder)

            else -> ItTaskBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ).let(::HomeViewHolder)
        }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        when (val item = getItem(position)) {
            is TaskListItem.Header -> (holder as HeaderViewHolder).bindData(item)

            else -> (holder as HomeViewHolder).bindData(item as TaskListItem.DtTask)
        }
    }

    override fun getItemCount(): Int  {
        Log.d(TAG, "getItemCount: ${filteredList.size}")
        return filteredList.size
    }

    override fun getFilter(): Filter {
        return object: Filter(){
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val searchString = if (constraint?.toString() != null) constraint.toString() else ""

                if (searchString.isBlank()) {
                    filteredList = currentList
                } else {
                    filteredList = currentList.filter {
                        it is TaskListItem.DtTask && it.name?.normalizeString()?.contains(searchString) == true
                    }
                        .toMutableList()
                }

                return FilterResults().apply {
                    values = filteredList
                }
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                notifyDataSetChanged()
            }

        }
    }
}