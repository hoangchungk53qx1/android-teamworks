package com.graduation.teamwork.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.compose.ui.text.input.textInputServiceFactory
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.graduation.teamwork.R
import com.graduation.teamwork.databinding.ItMemberBinding
import com.graduation.teamwork.models.data.MemberInRoom

class MemberAdapter(
    listItem: List<MemberInRoom>,
    private val onItemClicked: (item: MemberInRoom) -> Unit
) : RecyclerView.Adapter<MemberAdapter.MemberViewHolder>() {

    init {
        currentList = listItem.toMutableList()
    }

    companion object {
        var currentList = mutableListOf<MemberInRoom>()
    }

    fun getItem(position: Int) = currentList[position]

    fun submitList(listItem: List<MemberInRoom>) {
        currentList.clear()

        currentList.addAll(listItem)

        notifyDataSetChanged()
    }

    inner class MemberViewHolder(val binding: ItMemberBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindData(member: MemberInRoom) {
            binding.run {
                btnMore.setOnClickListener {
                    onItemClicked(member)
                }


                Log.d("__MEMBER_ADAPTER", "bindData: ${member.user?.fullname} - ${member.user?.image?.url}")

                Glide.with(root.context)
                    .load(member.user?.image?.url)
                    .placeholder(R.drawable.default_avatar)
                    .into(imgAvatar)

                tvName.text = member.user?.fullname
                tvMail.text = member.user?.mail
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemberViewHolder =
        ItMemberBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        ).let(::MemberViewHolder)

    override fun onBindViewHolder(holder: MemberViewHolder, position: Int) {
        holder.bindData(getItem(position))
    }

    override fun getItemCount(): Int = currentList.size
}