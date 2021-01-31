package ir.fallahpoor.releasetracker.libraries.view

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ir.fallahpoor.releasetracker.data.entity.Library

class LibrariesAdapter :
    ListAdapter<Library, LibrariesAdapter.LibraryViewHolder>(LibraryDiffCallback()) {

    var selectionTracker: SelectionTracker<String>? = null

    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LibraryViewHolder {
        return LibraryViewHolder(View(parent.context))
    }

    override fun onBindViewHolder(holder: LibraryViewHolder, position: Int) {
        val isSelected = selectionTracker?.isSelected(getItem(position).name) ?: false
        holder.bindData(isSelected)
    }

    override fun getItemId(position: Int): Long = getItem(position).hashCode().toLong()

    inner class LibraryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindData(isSelected: Boolean) {
//            binding.checkImageView.isGone = !isSelected
        }

        fun getItemDetails(): ItemDetailsLookup.ItemDetails<String> =
            object : ItemDetailsLookup.ItemDetails<String>() {

                override fun getPosition(): Int = adapterPosition

                override fun getSelectionKey(): String = getItem(adapterPosition).name

            }

    }

    private class LibraryDiffCallback : DiffUtil.ItemCallback<Library>() {

        override fun areItemsTheSame(oldItem: Library, newItem: Library): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(oldItem: Library, newItem: Library): Boolean {
            return oldItem == newItem
        }

    }

}