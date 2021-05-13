package com.graduation.teamwork.ui.task.details.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.graduation.teamwork.data.local.Constant
import com.graduation.teamwork.databinding.ItDetailLabelBinding
import com.graduation.teamwork.extensions.setVisiable
import com.graduation.teamwork.ui.base.BaseAdapter

class DetailLabelAdapter(
    defaultList: List<Constant.LabelColor>,
    private var selectedList: MutableList<Int>,
) : BaseAdapter<Constant.LabelColor, ItDetailLabelBinding>(defaultList) {

    private var hasSelectedColor = false

    override fun onCreateVH(parent: ViewGroup, viewType: Int): ItemVH<ItDetailLabelBinding> =
        ItDetailLabelBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        ).let(::ItemVH)

    override fun onBindVH(holder: ItemVH<ItDetailLabelBinding>, position: Int) {
        val item = getItem(position)

        with(holder.binding) {
            root.setOnClickListener {
                selectedColor(item.value, position)
                hasSelectedColor = true
            }

            vColor.setBackgroundResource(Constant.LabelColor.toColor(item))
            imgAccept.setVisiable(selectedList.contains(item.value))
        }
    }

    fun selectedColor(value: Int, position: Int) {
        selectedList.clear()
        selectedList.add(value)

        notifyDataSetChanged()
    }

    fun submitSelected(list: List<Int>) {
        selectedList.clear()
        selectedList.addAll(list)

        notifyDataSetChanged()
    }

    fun getSelectedColor() = selectedList

    fun hasSelectedColor() = hasSelectedColor

    fun resetSelected() {
        hasSelectedColor = false
    }

}