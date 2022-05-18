package ir.fallahpoor.releasetracker.data.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class LibraryVersion(
    @SerialName("name")
    val name: String,
    @SerialName("tag_name")
    val tagName: String
)
