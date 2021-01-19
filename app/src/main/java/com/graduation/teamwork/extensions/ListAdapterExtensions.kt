package com.graduation.teamwork.extensions

import android.util.Log
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

/**
 * Update the [RecyclerView]'s [ListAdapter] with the provided list of items.
 *
 * Originally, [ListAdapter] will not update the view if the provided list is the same as
 * currently loaded one. This is by design as otherwise the provided DiffUtil.ItemCallback<T>
 * could never work - the [ListAdapter] must have the previous list if items to compare new
 * ones to using provided diff callback.
 * However, it's very convenient to call [ListAdapter.submitList] with the same list and expect
 * the view to be updated. This extension function handles this case by making a copy of the
 * list if the provided list is the same instance as currently loaded one.
 *
 * For more info see 'RJFares' and 'insa_c' answers on
 * https://stackoverflow.com/questions/49726385/listadapter-not-updating-item-in-reyclerview
 */
fun <T, VH : RecyclerView.ViewHolder> ListAdapter<T, VH>.updateList(list: List<T>?) {
    // ListAdapter<>.submitList() contains (stripped):
      if (list == this.currentList) {
          // nothing to do
          return;
      }
    this.submitList(if (list == this.currentList) list.toList() else list)
}