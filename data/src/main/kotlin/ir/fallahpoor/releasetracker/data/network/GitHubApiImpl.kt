package ir.fallahpoor.releasetracker.data.network

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import ir.fallahpoor.releasetracker.data.network.models.LatestRelease
import ir.fallahpoor.releasetracker.data.network.models.SearchRepositoriesResult
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GitHubApiImpl
@Inject constructor(
    private val httpClient: HttpClient
) : GitHubApi {

    override suspend fun getLatestRelease(owner: String, repository: String): LatestRelease =
        httpClient.get("repos/$owner/$repository/releases/latest").body()

    override suspend fun searchRepositories(
        repositoryName: String,
        page: Int,
        pageSize: Int
    ): SearchRepositoriesResult =
        httpClient.get("search/repositories") {
            parameter("q", "$repositoryName in:name")
            parameter("sort", "stars")
            parameter("page", page)
            parameter("per_page", pageSize)
        }.body()

}