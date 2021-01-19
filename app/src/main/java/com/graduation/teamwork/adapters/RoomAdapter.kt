package com.graduation.teamwork.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.graduation.teamwork.databinding.ItRoomBinding
import com.graduation.teamwork.extensions.normalizeString
import com.graduation.teamwork.models.DtRoom
import com.graduation.teamwork.models.Room
import com.graduation.teamwork.models.data.TaskListItem

/**
 * com.graduation.teamwork.adapters
 * Created on 11/19/20
 */

class RoomAdapter(
    listItem: List<DtRoom>,
    private val onItemClicked: ((item: DtRoom, position: Int) -> Unit)

) : RecyclerView.Adapter<RoomAdapter.RoomViewHolder>(), Filterable {
    private val TAG = "__RoomAdapter"

    init {
        currentList = listItem.toMutableList()
        var filteredList = listItem.toMutableList()
    }

    companion object {
        var currentList = mutableListOf<DtRoom>()
        var filteredList = mutableListOf<DtRoom>()
    }

    private fun getItem(position: Int) = filteredList[position]

    fun submitList(listItem: List<DtRoom>) {
        currentList.clear()
        currentList.addAll(listItem)

        filteredList.clear()
        filteredList.addAll(listItem)

        notifyDataSetChanged()
    }

    inner class RoomViewHolder(private val binding: ItRoomBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bindData(room: DtRoom) {
            Log.d(TAG, "bindData: ${room.name} - ${room.createAt}")
            with(binding) {
                imgRoom.setOnClickListener {
                    onItemClicked(room, adapterPosition)
                }

                tvRoom.text = room.name.orEmpty()

                // todo
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoomViewHolder =
        ItRoomBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        ).let(::RoomViewHolder)

    override fun onBindViewHolder(holder: RoomViewHolder, position: Int) {
        Log.d(TAG, "onBindViewHolder: ${position}")
        holder.bindData(getItem(position))
    }

    override fun getItemCount(): Int = filteredList.size
    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val searchString = if (constraint?.toString() != null) constraint.toString() else ""

                if (searchString.isBlank()) {
                    filteredList = currentList
                } else {
                    filteredList = currentList.filter {
                        it.name?.normalizeString()?.contains(searchString) == true
                    }
                        .toMutableList()
                }

                return FilterResults().apply {
                    values = HomeAdapter.filteredList
                }
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                notifyDataSetChanged()
            }

        }
    }
}