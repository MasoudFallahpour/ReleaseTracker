package ir.fallahpoor.releasetracker.libraries.view

import ir.fallahpoor.releasetracker.data.entity.Library
import ir.fallahpoor.releasetracker.data.utils.SortOrder

data class LibrariesListScreenState(
    val sortOrder: SortOrder = SortOrder.A_TO_Z,
    val searchQuery: String = "",
    val librariesListState: LibrariesListState = LibrariesListState.Loading
)

sealed class LibrariesListState {
    object Loading : LibrariesListState()
    data class LibrariesLoaded(val libraries: List<Library>) : LibrariesListState()
}