package com.graduation.teamwork.ui.slider

import android.view.LayoutInflater
import android.view.ViewGroup
import com.graduation.teamwork.databinding.ItSliderGroupBinding
import com.graduation.teamwork.models.DtGroup
import com.graduation.teamwork.ui.base.BaseAdapter

/**
 * com.graduation.teamwork.adapters
 * Created on 11/14/20
 */

class SliderAdapter(
    listItem: List<DtGroup>,
    private val onItemClicked: ((item: DtGroup, position: Int) -> Unit)
) : BaseAdapter<DtGroup, ItSliderGroupBinding>(listItem) {

    override fun onCreateVH(parent: ViewGroup, viewType: Int): ItemVH<ItSliderGroupBinding> =
        ItSliderGroupBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        ).let(::ItemVH)

    override fun onBindVH(holder: ItemVH<ItSliderGroupBinding>, position: Int) {
        val item = getItem(position)
        with(holder.binding) {
            lnSlideGroup.setOnClickListener {
                onItemClicked(item, position)
            }

//                imgIcon.setImageResource(
//                    root.context.resources.getDrawable()
//                )
            tvName.text = item.name
        }
    }
}