package ir.fallahpoor.releasetracker.data

import ir.fallahpoor.releasetracker.data.database.entity.LibraryEntity
import ir.fallahpoor.releasetracker.data.repository.library.Library

fun LibraryEntity.toLibrary(): Library = Library(
    name = this.name,
    url = this.url,
    version = this.version,
    isPinned = this.pinned == 1L
)