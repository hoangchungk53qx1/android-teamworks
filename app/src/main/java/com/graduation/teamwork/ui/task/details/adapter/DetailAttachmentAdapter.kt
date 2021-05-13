package com.graduation.teamwork.ui.task.details.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.graduation.teamwork.R
import com.graduation.teamwork.databinding.ItDetailAttachmentImageBinding
import com.graduation.teamwork.databinding.ItDetailAttachmentLinkBinding
import com.graduation.teamwork.extensions.*
import com.graduation.teamwork.models.Attachment
import com.graduation.teamwork.models.TypeAttachment


/**
 * Created by TranTien on 3/1/21.
 */

class DetailAttachmentAdapter(
    val list: List<Attachment>,
    private val onItemClicked: (item: Attachment) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var currentList = mutableListOf<Attachment>()

    init {
        currentList = list.toMutableList()
    }

    inner class DocumentViewHolder(private val binding: ItDetailAttachmentLinkBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindData(item: Attachment, position: Int) {
            with(binding) {
                root.setOnClickListener { onItemClicked(getItem(adapterPosition)) }

                btnGoto.setVisiable(item.type == TypeAttachment.LINK.value)
                btnGoto.setOnClickListener { root.context.gotoUrl(item.data?.url ?: "") }
                btnMore.setOnClickListener {
                    //TODO: SHOW POPUP DELETE DATA
                }

                tvTitle.text = item.data?.name
                tvTime.text = item.createAt?.toDate()?.formatDate()
                tvLine.setVisiable(item != currentList.last())
            }
        }
    }

    inner class ImageViewHolder(private val binding: ItDetailAttachmentImageBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindData(item: Attachment) {
            with(binding) {
                root.setOnClickListener { onItemClicked(getItem(adapterPosition)) }

                btnMore.setOnClickListener {
                    //TODO: SHOW POPUP DELETE DATA
                }

                Glide.with(root)
                    .load(item.data?.url)
                    .placeholder(R.drawable.img_placeholder)
                    .into(imgAttachment)
            }
        }
    }

    override fun getItemViewType(position: Int): Int =
        when (getItem(position).type) {
            TypeAttachment.IMAGE.value -> TYPE_IMAGE
            else -> TYPE_NOT_IMAGE
        }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder = if (viewType == TYPE_IMAGE) {
        ItDetailAttachmentImageBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        ).let(::ImageViewHolder)
    } else {
        ItDetailAttachmentLinkBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        ).let(::DocumentViewHolder)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder.itemViewType == TYPE_IMAGE) {
            (holder as ImageViewHolder).bindData(getItem(position))
        } else {
            (holder as DocumentViewHolder).bindData(getItem(position), position)
        }
    }

    override fun getItemCount(): Int = currentList.size

    private fun getItem(position: Int) = currentList[position]

    fun submitList(list: List<Attachment>) {
        currentList.clear()
        currentList.addAll(list)

        notifyDataSetChanged()
    }

    companion object {
        const val TYPE_IMAGE = 0
        const val TYPE_NOT_IMAGE = 1
    }
}