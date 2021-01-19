package com.graduation.teamwork.ui.base

import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

abstract class BaseViewHolder<T> (viewBinding: ViewBinding) : RecyclerView.ViewHolder(viewBinding.root) {

    abstract fun bindData(data: T)
}