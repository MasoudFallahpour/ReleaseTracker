package ir.fallahpoor.releasetracker.data

import ir.fallahpoor.releasetracker.data.database.entity.LibraryEntity
import ir.fallahpoor.releasetracker.data.network.models.SearchRepositoriesResultDto
import ir.fallahpoor.releasetracker.data.repository.library.models.Library
import ir.fallahpoor.releasetracker.data.repository.library.models.SearchRepositoriesResult

fun LibraryEntity.toLibrary() = Library(
    name = this.name,
    url = this.url,
    version = this.version,
    isPinned = this.pinned == 1L
)

fun Library.toLibraryEntity() = LibraryEntity(
    name = name,
    url = url,
    version = version,
    pinned = if (isPinned) 1 else 0
)

fun SearchRepositoriesResultDto.toSearchRepositoriesResult() = SearchRepositoriesResult(
    id = this.id,
    name = this.name,
    url = this.url,
    description = this.description
)