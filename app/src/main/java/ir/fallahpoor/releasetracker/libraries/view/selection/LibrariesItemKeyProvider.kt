package ir.fallahpoor.releasetracker.libraries.view.selection

import androidx.recyclerview.selection.ItemKeyProvider
import ir.fallahpoor.releasetracker.libraries.view.LibrariesAdapter

class LibrariesItemKeyProvider(
    private val librariesAdapter: LibrariesAdapter
) : ItemKeyProvider<String>(SCOPE_CACHED) {

    override fun getKey(position: Int): String {
        return librariesAdapter.currentList[position].name
    }

    override fun getPosition(key: String): Int {
        return librariesAdapter.currentList.indexOfFirst { it.name == key }
    }

}