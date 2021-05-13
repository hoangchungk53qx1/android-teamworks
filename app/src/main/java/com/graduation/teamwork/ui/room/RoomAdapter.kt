package com.graduation.teamwork.ui.room

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import com.graduation.teamwork.databinding.ItRoomBinding
import com.graduation.teamwork.extensions.normalizeString
import com.graduation.teamwork.models.DtRoom
import com.graduation.teamwork.ui.base.BaseAdapter
import com.graduation.teamwork.ui.home.HomeAdapter

/**
 * com.graduation.teamwork.adapters
 * Created on 11/19/20
 */

class RoomAdapter(
    listItem: List<DtRoom>,
    private val onItemClicked: ((item: DtRoom, position: Int) -> Unit)
) : BaseAdapter<DtRoom, ItRoomBinding>(listItem), Filterable {
    private val TAG = "__RoomAdapter"

    init {
        var filteredList = listItem.toMutableList()
    }

    companion object {
        var filteredList = mutableListOf<DtRoom>()
    }

    override fun submitList(list: List<DtRoom>) {
        filteredList.clear()
        filteredList.addAll(list)

        super.submitList(list)
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val searchString = if (constraint?.toString() != null) constraint.toString() else ""

                filteredList = if (searchString.isBlank()) {
                    currentList
                } else {
                    currentList.filter {
                        it.name?.normalizeString()?.contains(searchString) == true
                    }
                        .toMutableList()
                }

                return FilterResults().apply { values = HomeAdapter.filteredList }
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                notifyDataSetChanged()
            }
        }
    }

    override fun getItemSize(): Int = filteredList.size

    override fun getItem(position: Int): DtRoom = filteredList[position]

    override fun onCreateVH(parent: ViewGroup, viewType: Int): ItemVH<ItRoomBinding> =
        ItRoomBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        ).let(::ItemVH)

    override fun onBindVH(holder: ItemVH<ItRoomBinding>, position: Int) {
        val item = getItem(position)
        with(holder.binding) {
            imgRoom.setOnClickListener {
                onItemClicked(item, position)
            }

            tvRoom.text = item.name.orEmpty()
        }
    }
}