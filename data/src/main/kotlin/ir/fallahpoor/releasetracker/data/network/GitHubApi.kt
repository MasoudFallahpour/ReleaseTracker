package ir.fallahpoor.releasetracker.data.network

import ir.fallahpoor.releasetracker.data.network.models.LatestReleaseDto
import ir.fallahpoor.releasetracker.data.network.models.SearchRepositoriesResultsDto

interface GitHubApi {
    suspend fun getLatestRelease(owner: String, repository: String): LatestReleaseDto

    suspend fun searchRepositories(
        repositoryName: String,
        page: Int = 1,
        pageSize: Int = 30
    ): SearchRepositoriesResultsDto
}