package ir.fallahpoor.releasetracker.libraries.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ir.fallahpoor.releasetracker.R
import ir.fallahpoor.releasetracker.data.entity.Library
import ir.fallahpoor.releasetracker.databinding.ListItemLibraryBinding

class LibrariesAdapter :
    ListAdapter<Library, LibrariesAdapter.LibraryViewHolder>(LibraryDiffCallback()) {

    var selectionTracker: SelectionTracker<String>? = null

    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LibraryViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.list_item_library, parent, false)
        return LibraryViewHolder(view)
    }

    override fun onBindViewHolder(holder: LibraryViewHolder, position: Int) {
        val isSelected = selectionTracker?.isSelected(getItem(position).name) ?: false
        holder.bindData(isSelected)
    }

    override fun getItemId(position: Int): Long = getItem(position).hashCode().toLong()

    inner class LibraryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val binding = ListItemLibraryBinding.bind(itemView)

        fun bindData(isSelected: Boolean) {
            binding.checkImageView.isGone = !isSelected
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