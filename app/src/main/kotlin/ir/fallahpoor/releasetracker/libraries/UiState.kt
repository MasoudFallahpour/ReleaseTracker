package ir.fallahpoor.releasetracker.libraries

import ir.fallahpoor.releasetracker.data.SortOrder
import ir.fallahpoor.releasetracker.data.repository.library.Library

data class LibrariesListScreenUiState(
    val sortOrder: SortOrder = SortOrder.A_TO_Z,
    val searchQuery: String = "",
    val librariesListState: LibrariesListState = LibrariesListState.Loading
)

sealed class LibrariesListState {
    object Loading : LibrariesListState()
    data class LibrariesLoaded(val libraries: List<Library>) : LibrariesListState()
}