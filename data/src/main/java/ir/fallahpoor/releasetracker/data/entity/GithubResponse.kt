package ir.fallahpoor.releasetracker.data.entity

import com.google.gson.annotations.SerializedName

class GithubResponse(
    @SerializedName("name")
    val name: String,
    @SerializedName("tag_name")
    val tagName: String
)
