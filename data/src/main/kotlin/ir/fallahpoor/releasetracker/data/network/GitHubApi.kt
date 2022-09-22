package ir.fallahpoor.releasetracker.data.network

import ir.fallahpoor.releasetracker.data.network.models.LatestRelease
import ir.fallahpoor.releasetracker.data.network.models.SearchRepositoriesResult

interface GitHubApi {
    suspend fun getLatestRelease(owner: String, repository: String): LatestRelease

    suspend fun searchRepositories(
        repositoryName: String,
        page: Int = 1,
        pageSize: Int = 30
    ): SearchRepositoriesResult
}