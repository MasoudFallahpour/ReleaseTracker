package ir.fallahpoor.releasetracker.data.repository.library

import ir.fallahpoor.releasetracker.data.database.entity.LibraryEntity

data class Library(
    val name: String,
    val url: String,
    val version: String,
    val isPinned: Boolean = false
) {

    fun toLibraryEntity() = LibraryEntity(
        name = name,
        url = url,
        version = version,
        pinned = if (isPinned) 1 else 0
    )

}