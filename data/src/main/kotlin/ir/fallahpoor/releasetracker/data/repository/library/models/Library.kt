package ir.fallahpoor.releasetracker.data.repository.library.models

data class Library(
    val name: String,
    val url: String,
    val version: String,
    val isPinned: Boolean = false
)