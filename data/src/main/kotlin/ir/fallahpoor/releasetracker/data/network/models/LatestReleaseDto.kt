package ir.fallahpoor.releasetracker.data.network.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class LatestReleaseDto(
    @SerialName("name")
    val name: String,
    @SerialName("tag_name")
    val tagName: String
)