package ir.fallahpoor.releasetracker.data.network

import ir.fallahpoor.releasetracker.data.network.models.LibraryVersion
import ir.fallahpoor.releasetracker.data.network.models.SearchResults

interface GitHubApi {
    suspend fun getLatestRelease(owner: String, repository: String): LibraryVersion

    suspend fun searchRepositories(
        repositoryName: String,
        page: Int = 1,
        pageSize: Int = 30
    ): SearchResults
}