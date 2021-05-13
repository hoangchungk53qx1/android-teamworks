package com.graduation.teamwork.ui.task.details.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.graduation.teamwork.databinding.ItDetailTaskBinding
import com.graduation.teamwork.extensions.setVisiable
import com.graduation.teamwork.models.DtSubtask
import com.graduation.teamwork.ui.base.BaseAdapter

/**
 * Created by TranTien on 3/1/21.
 */

class DetailTaskListAdapter(
    listItem: List<DtSubtask>,
    private val onItemClicked: (item: DtSubtask) -> Unit
) : BaseAdapter<DtSubtask, ItDetailTaskBinding>(listItem) {

    override fun onCreateVH(parent: ViewGroup, viewType: Int): ItemVH<ItDetailTaskBinding> =
        ItDetailTaskBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        ).let(::ItemVH)

    override fun onBindVH(holder: ItemVH<ItDetailTaskBinding>, position: Int) {
        val item = getItem(position)
        with(holder.binding) {
            root.setOnClickListener {
                item.isCompleted = !item.isCompleted!!
                imgAccept.setVisiable(item.isCompleted)

                onItemClicked(item)
            }

            tvTitle.text = item.name
            imgAccept.setVisiable(item.isCompleted)
            vLine.setVisiable(position < currentList.size - 1)
        }
    }
}