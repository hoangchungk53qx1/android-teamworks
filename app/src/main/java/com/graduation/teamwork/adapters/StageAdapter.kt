package com.graduation.teamwork.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.graduation.teamwork.R
import com.graduation.teamwork.databinding.ItTaskBinding
import com.graduation.teamwork.models.DtStage
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

    fun submitList(listItem: List<TaskListItem>){
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
                    Log.d(TAG, "updateViews: TIME - SP ${task.deadline}")
                    onItemClicked(task, adapterPosition)
                }

                Log.d(TAG, "bindData: ${task.name}")
                tvNameTask.text = task.name
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
        Log.d("BIND_", "onBindViewHolder: >>>> bind in $position")
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