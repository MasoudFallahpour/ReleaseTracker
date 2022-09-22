package ir.fallahpoor.releasetracker.data.network.models

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class SearchRepositoriesResult(
    @SerialName("total_count")
    val totalCount: Int,
    @SerialName("incomplete_results")
    val incompleteResults: Boolean,
    @SerialName("items")
    val items: List<SearchRepositoriesResultItem>
)