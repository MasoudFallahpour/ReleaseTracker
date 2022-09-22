package ir.fallahpoor.releasetracker.data.repository.library.models

data class SearchRepositoriesResult(
    val id: Long,
    val name: String,
    val url: String,
    val description: String
)