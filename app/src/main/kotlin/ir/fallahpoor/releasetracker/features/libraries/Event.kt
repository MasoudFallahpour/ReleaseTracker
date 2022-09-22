package ir.fallahpoor.releasetracker.features.libraries

import ir.fallahpoor.releasetracker.data.SortOrder
import ir.fallahpoor.releasetracker.data.repository.library.models.Library

sealed class Event {
    data class PinLibrary(val library: Library, val pin: Boolean) : Event()
    data class DeleteLibrary(val library: Library) : Event()
    data class ChangeSortOrder(val sortOrder: SortOrder) : Event()
    data class ChangeSearchQuery(val searchQuery: String) : Event()
}