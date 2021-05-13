package com.graduation.teamwork.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.graduation.teamwork.R
import com.graduation.teamwork.data.local.Constant
import com.graduation.teamwork.databinding.ItHeaderTaskBinding
import com.graduation.teamwork.databinding.ItTaskBinding
import com.graduation.teamwork.extensions.formatDate
import com.graduation.teamwork.extensions.normalizeString
import com.graduation.teamwork.extensions.setVisiable
import com.graduation.teamwork.extensions.toDate
import com.graduation.teamwork.models.data.TaskListItem
import com.graduation.teamwork.ui.room.scroll.StageMemberAdapter

class HomeAdapter(
    listItem: List<TaskListItem>,
    private val onItemClicked: (item: TaskListItem) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>(), Filterable {

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
        filteredList.clear()

        currentList.addAll(listItem)
        filteredList.addAll(listItem)

        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int =
        when (getItem(position)) {
            is TaskListItem.Header -> TYPE_HEADER
            else -> TYPE_DATA
        }

    inner class HomeViewHolder(private val binding: ItTaskBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bindData(task: TaskListItem.DtTask) {
            binding.run {
                root.setOnClickListener {
                    onItemClicked(task)
                }
                val stageMemberAdapter = StageMemberAdapter(task.users ?: mutableListOf())

                tvTitle.text = task.name

                val countSubTaskCompleted =
                    task.subtasks?.filter { it.isCompleted }?.count() ?: 0
                val totalSubTask = (task.subtasks?.count() ?: 1).coerceAtLeast(1)
                circleProgress.progress =
                    (((countSubTaskCompleted / totalSubTask.toFloat()) * 100).toInt())

                if (!task.labels.isNullOrEmpty()) {
                    val color = Constant.LabelColor.toColor(task.labels.first())
                    vColor.setBackgroundResource(color)

                    circleProgress.setProgressTextColor(color)
                    circleProgress.setProgressStartColor(color)
                    circleProgress.setProgressEndColor(color)
                }

                tvTime.text = task.deadline.toDate().formatDate()
                tvActivity.text = task.comments?.count().toString()
                tvAttachment.text = task.attachments?.count().toString()

                tvTime.setVisiable(task.deadline != 0L)
                tvActivity.setVisiable(!task.comments.isNullOrEmpty())
                tvAttachment.setVisiable(!task.attachments.isNullOrEmpty())
                vColor.setVisiable(!task.labels.isNullOrEmpty())


                recyclerMember.apply {
                    layoutManager =
                        LinearLayoutManager(root.context, LinearLayoutManager.HORIZONTAL, false)
                    setHasFixedSize(true)
                    adapter = stageMemberAdapter

                    setVisiable(!task.users.isNullOrEmpty())
                }
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
                imgNext.setOnClickListener { onItemClicked(header) }

                tvHeader.text = header.name

                Glide.with(root.context)
                    .load(header.image)
                    .placeholder(R.drawable.bg_demo)
                    .centerCrop()
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

    override fun getItemCount(): Int = filteredList.size

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val searchString = if (constraint?.toString() != null) constraint.toString() else ""

                filteredList = if (searchString.isBlank()) {
                    currentList
                } else {
                    currentList.filter {
                        it is TaskListItem.DtTask && it.name?.normalizeString()
                            ?.contains(searchString) == true
                    }.toMutableList()
                }

                return FilterResults().apply { values = filteredList }
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                notifyDataSetChanged()
            }

        }
    }

    fun getCurrentList() = currentList
}