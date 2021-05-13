package com.graduation.teamwork.ui.notification

import android.view.LayoutInflater
import android.view.ViewGroup
import com.graduation.teamwork.databinding.ItNotiBinding
import com.graduation.teamwork.models.firebase.InfoNoti
import com.graduation.teamwork.ui.base.BaseAdapter

class NotifyAdapter(
    list: List<InfoNoti>,
    private val onItemClicked: (item: InfoNoti) -> Unit
) : BaseAdapter<InfoNoti, ItNotiBinding>(list) {

    override fun onCreateVH(parent: ViewGroup, viewType: Int): ItemVH<ItNotiBinding> =
        ItNotiBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        ).let(::ItemVH)

    override fun onBindVH(holder: ItemVH<ItNotiBinding>, position: Int) {
        val item = getItem(position)
        with(holder.binding) {
            root.setOnClickListener {
                onItemClicked(item)
            }

            tvInfo.text = item.content
            tvTimeNoti.text = item.timestamp
        }
    }
}