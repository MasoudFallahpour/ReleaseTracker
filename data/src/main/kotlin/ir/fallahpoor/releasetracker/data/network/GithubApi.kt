package ir.fallahpoor.releasetracker.data.network

interface GithubApi {
    suspend fun getLatestRelease(owner: String, repository: String): LibraryVersion
}