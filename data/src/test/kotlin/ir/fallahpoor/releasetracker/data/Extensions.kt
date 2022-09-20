package ir.fallahpoor.releasetracker.data

import ir.fallahpoor.releasetracker.data.network.models.SearchResultItem
import ir.fallahpoor.releasetracker.data.repository.library.Library

fun Library.toSearchResultItem(id: Int) = SearchResultItem(
    id = id.toLong(),
    name = this.name,
    url = this.url,
    description = ""
)