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

class LibrariesAdapter(
    private val clickListener: (Library) -> Unit,
    private val pinClickListener: (Library, Boolean) -> Unit
) : ListAdapter<Library, LibrariesAdapter.LibraryViewHolder>(LibraryDiffCallback()) {

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
        val library: Library = getItem(position)
        val isSelected = selectionTracker?.isSelected(getItem(position).name) ?: false
        holder.bindData(library, isSelected)
    }

    override fun getItemId(position: Int): Long = getItem(position).hashCode().toLong()

    inner class LibraryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val binding = ListItemLibraryBinding.bind(itemView)

        fun bindData(library: Library, isSelected: Boolean) {
            binding.libraryNameTextView.text = library.name
            binding.libraryUrlTextView.text = library.url
            binding.libraryVersionTextView.text = library.version
            binding.pinCheckBox.isChecked = library.pinned == 1
            binding.pinCheckBox.setOnClickListener {
                val isChecked = library.pinned == 0
                pinClickListener.invoke(library, isChecked)
            }
            binding.checkImageView.isGone = !isSelected
            itemView.setOnClickListener {
                clickListener.invoke(library)
            }
        }

        fun getItemDetails(): ItemDetailsLookup.ItemDetails<String> =
            object : ItemDetailsLookup.ItemDetails<String>() {

                override fun getPosition(): Int = adapterPosition

                override fun getSelectionKey(): String? = getItem(adapterPosition).name

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