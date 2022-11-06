package ir.fallahpoor.releasetracker.data.network.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SearchRepositoriesResultDto(
    @SerialName("id")
    val id: Long,
    @SerialName("name")
    val name: String,
    @SerialName("html_url")
    val url: String,
    @SerialName("description")
    val description: String
)