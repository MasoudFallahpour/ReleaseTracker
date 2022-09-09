package ir.fallahpoor.releasetracker.data.network

interface GithubApi {
    suspend fun getLatestVersion(owner: String, repository: String): LibraryVersion
}