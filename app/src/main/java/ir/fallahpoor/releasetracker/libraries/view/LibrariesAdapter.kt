package ir.fallahpoor.releasetracker.libraries.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ir.fallahpoor.releasetracker.R
import ir.fallahpoor.releasetracker.data.entity.Library

class LibrariesAdapter(
    private val libraries: List<Library>,
    private val favoriteClickListener: (Library, Boolean) -> Unit
) : RecyclerView.Adapter<LibrariesAdapter.LibraryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LibraryViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.list_item_library, parent, false)
        return LibraryViewHolder(view)
    }

    override fun onBindViewHolder(holder: LibraryViewHolder, position: Int) {
        val library: Library = libraries[position]
        holder.bindData(library)
    }

    override fun getItemCount() = libraries.size

    inner class LibraryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val nameTextView: TextView = itemView.findViewById(R.id.libraryNameTextView)
        private val urlTextView: TextView = itemView.findViewById(R.id.libraryUrlTextView)
        private val versionTextView: TextView = itemView.findViewById(R.id.libraryVersionTextView)
        private val favouriteCheckBox: CheckBox = itemView.findViewById(R.id.favouriteCheckBox)

        fun bindData(library: Library) {
            nameTextView.text = library.name
            urlTextView.text = library.url
            versionTextView.text = library.version
            favouriteCheckBox.isChecked = library.isFavourite == 1
            favouriteCheckBox.setOnClickListener {
                val isChecked = library.isFavourite == 0
                favoriteClickListener.invoke(library, isChecked)
            }
        }

    }

}