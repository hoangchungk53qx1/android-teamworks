package com.graduation.teamwork.ui.room.scroll

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.graduation.teamwork.R
import com.graduation.teamwork.databinding.ItMemberCycleBinding
import com.graduation.teamwork.extensions.getNameAbbreviation
import com.graduation.teamwork.extensions.gone
import com.graduation.teamwork.extensions.visiable
import com.graduation.teamwork.models.DtUser
import com.graduation.teamwork.ui.base.BaseAdapter

class StageMemberAdapter(
    list: List<DtUser>
) : BaseAdapter<DtUser, ItMemberCycleBinding>(list) {
    override fun onCreateVH(parent: ViewGroup, viewType: Int): ItemVH<ItMemberCycleBinding> =
        ItMemberCycleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            .let(::ItemVH)

    override fun onBindVH(holder: ItemVH<ItMemberCycleBinding>, position: Int) {
        val item = getItem(position)

        with(holder.binding) {
            if (!item.image?.url.isNullOrBlank()) {
                Glide.with(root.context)
                    .load(item.image?.url)
                    .placeholder(R.drawable.img_placeholder)
                    .centerCrop()
                    .into(imgAvatar)

                imgAvatar.background = null

                tvTitle.gone()
            } else {
                tvTitle.text = item.fullname?.getNameAbbreviation()
                tvTitle.visiable()
            }
        }
    }

}