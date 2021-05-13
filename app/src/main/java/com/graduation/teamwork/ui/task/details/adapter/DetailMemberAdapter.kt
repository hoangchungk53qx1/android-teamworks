package com.graduation.teamwork.ui.task.details.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.graduation.teamwork.R
import com.graduation.teamwork.databinding.ItDetailMemberBinding
import com.graduation.teamwork.extensions.setVisiable
import com.graduation.teamwork.models.DtUser
import com.graduation.teamwork.ui.base.BaseAdapter

/**
 * Created by TranTien on 3/3/21.
 */

class DetailMemberAdapter(
    list: List<DtUser>,
    private var selectedMembers: MutableList<DtUser>,
    private val onItemClicked: (item: DtUser, position: Int) -> Unit
) : BaseAdapter<DtUser, ItDetailMemberBinding>(list) {

    private var hasSelectedMember = false

    override fun onCreateVH(parent: ViewGroup, viewType: Int): ItemVH<ItDetailMemberBinding> =
        ItDetailMemberBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        ).let(::ItemVH)

    override fun onBindVH(holder: ItemVH<ItDetailMemberBinding>, position: Int) {
        val item = getItem(position)
        with(holder.binding) {
            root.setOnClickListener {
                item.isSelected = !item.isSelected

                hasSelectedMember = true
                selectedMember(item, position)
                onItemClicked(item, position)
            }

            Glide
                .with(root)
                .load(item.image?.url)
                .placeholder(R.drawable.ic_user_default)
                .into(imgAvatar)

            tvCode.text = item._id
            tvTitle.text = item.fullname
            imgAccept.setVisiable(selectedMembers.contains(item))
        }
    }

    fun selectedMember(member: DtUser, position: Int) {
        if (selectedMembers.contains(member)) {
            selectedMembers.remove(member)
        } else {
            selectedMembers.add(member)
        }

        notifyItemChanged(position)
    }

    fun submitSelected(list: List<DtUser>) {
        selectedMembers.clear()
        selectedMembers.addAll(list)

        notifyDataSetChanged()
    }

    fun getSelectedMembers() = selectedMembers

    fun hasSelectedMember() = hasSelectedMember

    fun resetSelected() {
        hasSelectedMember = false
    }
}