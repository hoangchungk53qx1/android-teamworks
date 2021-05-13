package com.graduation.teamwork.ui.room.details.member

import android.view.LayoutInflater
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.graduation.teamwork.R
import com.graduation.teamwork.databinding.ItMemberBinding
import com.graduation.teamwork.models.data.MemberInRoom
import com.graduation.teamwork.ui.base.BaseAdapter

class MemberAdapter(
    listItem: List<MemberInRoom>,
    private val onItemClicked: (item: MemberInRoom) -> Unit
) : BaseAdapter<MemberInRoom, ItMemberBinding>(listItem) {

    override fun onCreateVH(parent: ViewGroup, viewType: Int): ItemVH<ItMemberBinding> =
        ItMemberBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        ).let(::ItemVH)

    override fun onBindVH(holder: ItemVH<ItMemberBinding>, position: Int) {
        val member = getItem(position)

        holder.binding.run {
            btnMore.setOnClickListener {
                onItemClicked(member)
            }
            
            Glide.with(root.context)
                .load(member.user?.image?.url)
                .placeholder(R.drawable.default_avatar)
                .into(imgAvatar)

            tvName.text = member.user?.fullname
            tvMail.text = member.user?.mail
        }
    }
}