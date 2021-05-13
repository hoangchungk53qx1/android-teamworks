package com.graduation.teamwork.ui.room.scroll

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.graduation.teamwork.R
import com.graduation.teamwork.data.local.Constant
import com.graduation.teamwork.databinding.ItTaskBinding
import com.graduation.teamwork.extensions.formatDate
import com.graduation.teamwork.extensions.setVisiable
import com.graduation.teamwork.extensions.toDate
import com.graduation.teamwork.models.data.TaskListItem

/**
 * com.graduation.teamwork.adapters
 * Created on 11/21/20
 */

class StageAdapter(
    listItem: List<TaskListItem>,
    private val onItemClicked: ((item: TaskListItem.DtTask, position: Int) -> Unit)
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val TAG = "__StageAdapter"
    var currentList = mutableListOf<TaskListItem>()

    init {
        currentList = listItem.toMutableList()
    }

    companion object {

        private const val TYPE_HEADER = 0
        private const val TYPE_CELL = 1
    }

    fun submitList(listItem: List<TaskListItem>) {
        Log.d(TAG, "submitList: ${listItem.size}")
        currentList.clear()
        currentList.addAll(listItem)

        notifyDataSetChanged()
    }

    private fun getItem(position: Int): TaskListItem = currentList[position]

    inner class StageViewHolder(val binding: ItTaskBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindData(task: TaskListItem.DtTask) {
            binding.run {
                root.setOnClickListener {
                    onItemClicked(task, adapterPosition)
                }

                val stageMemberAdapter = StageMemberAdapter(task.users ?: mutableListOf())

                tvTitle.text = task.name

                if (!task.labels.isNullOrEmpty()) {
                    val color = Constant.LabelColor.toColor(task.labels.first())
                    vColor.setBackgroundResource(color)

                    circleProgress.setProgressTextColor(color)
                    circleProgress.setProgressStartColor(color)
                    circleProgress.setProgressEndColor(color)
                }

                val countSubTaskCompleted =
                    task.subtasks?.filter { it.isCompleted }?.count() ?: 0
                val totalSubTask = (task.subtasks?.count() ?: 1).coerceAtLeast(1)
                circleProgress.progress = (((countSubTaskCompleted / totalSubTask.toFloat()) * 100).toInt())

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

    override fun getItemViewType(position: Int): Int = TYPE_CELL
//        when (position) {
//            0 -> TYPE_HEADER
//            else -> TYPE_CELL
//        }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder =
        when (viewType) {
            TYPE_HEADER -> {
                object : RecyclerView.ViewHolder(
                    LayoutInflater.from(parent.context).inflate(
                        R.layout.hvp_header_placeholder,
                        parent,
                        false
                    )
                ) {}
            }
            else -> ItTaskBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ).let(::StageViewHolder)
        }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            TYPE_HEADER -> {

            }
            else -> {
                //NO THING
                (holder as StageViewHolder).bindData(getItem(position) as TaskListItem.DtTask)
            }
        }
    }

    override fun getItemCount(): Int = currentList.size

}