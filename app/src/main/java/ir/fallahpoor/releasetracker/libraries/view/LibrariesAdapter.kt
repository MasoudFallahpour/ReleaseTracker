package ir.fallahpoor.releasetracker.libraries.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ir.fallahpoor.releasetracker.R
import ir.fallahpoor.releasetracker.data.entity.Library

class LibrariesAdapter(
    private val pinClickListener: (Library, Boolean) -> Unit
) : ListAdapter<Library, LibrariesAdapter.LibraryViewHolder>(LibraryDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LibraryViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.list_item_library, parent, false)
        return LibraryViewHolder(view)
    }

    override fun onBindViewHolder(holder: LibraryViewHolder, position: Int) {
        val library: Library = getItem(position)
        holder.bindData(library)
    }

    inner class LibraryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val nameTextView: TextView = itemView.findViewById(R.id.libraryNameTextView)
        private val urlTextView: TextView = itemView.findViewById(R.id.libraryUrlTextView)
        private val versionTextView: TextView = itemView.findViewById(R.id.libraryVersionTextView)
        private val pinCheckBox: CheckBox = itemView.findViewById(R.id.pinCheckBox)

        fun bindData(library: Library) {
            nameTextView.text = library.name
            urlTextView.text = library.url
            versionTextView.text = library.version
            pinCheckBox.isChecked = library.pinned == 1
            pinCheckBox.setOnClickListener {
                val isChecked = library.pinned == 0
                pinClickListener.invoke(library, isChecked)
            }
        }

    }

}

class LibraryDiffCallback : DiffUtil.ItemCallback<Library>() {

    override fun areItemsTheSame(oldItem: Library, newItem: Library): Boolean {
        return oldItem.name == newItem.name
    }

    override fun areContentsTheSame(oldItem: Library, newItem: Library): Boolean {
        return oldItem == newItem
    }

}