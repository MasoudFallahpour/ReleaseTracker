package ir.fallahpoor.releasetracker.libraries.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ir.fallahpoor.releasetracker.R
import ir.fallahpoor.releasetracker.data.entity.Library

class LibrariesAdapter(
    private val libraries: List<Library>
) : RecyclerView.Adapter<LibrariesAdapter.LibraryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LibraryViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.list_item_library, parent, false)
        return LibraryViewHolder(view)
    }

    override fun onBindViewHolder(holder: LibraryViewHolder, position: Int) {
        val library: Library = libraries[position]
        holder.libraryNameTextView.text = library.libraryName
        holder.libraryVersionTextView.text = library.version
    }

    override fun getItemCount() = libraries.size

    class LibraryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val libraryNameTextView: TextView = itemView.findViewById(R.id.libraryNameTextView)
        val libraryVersionTextView: TextView = itemView.findViewById(R.id.libraryVersionTextView)

    }

}