package ir.fallahpoor.releasetracker.data

import ir.fallahpoor.releasetracker.data.database.entity.LibraryEntity
import ir.fallahpoor.releasetracker.data.network.models.SearchRepositoriesResultItem
import ir.fallahpoor.releasetracker.data.repository.library.Library

fun Library.toSearchResultItem(id: Int) = SearchRepositoriesResultItem(
    id = id.toLong(),
    name = this.name,
    url = this.url,
    description = ""
)

fun LibraryEntity.toLibrary() = Library(
    name = this.name,
    url = this.url,
    version = this.version,
    isPinned = this.pinned == 1L
)