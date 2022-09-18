package ir.fallahpoor.releasetracker.data.network.models

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class SearchResultItem(
    @SerialName("id")
    val id: Long,
    @SerialName("name")
    val name: String,
    @SerialName("html_url")
    val url: String,
    @SerialName("description")
    val description: String
)