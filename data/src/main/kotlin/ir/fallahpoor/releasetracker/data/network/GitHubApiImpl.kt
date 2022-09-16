package ir.fallahpoor.releasetracker.data.network

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import ir.fallahpoor.releasetracker.data.network.models.SearchResults
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GitHubApiImpl
@Inject constructor(
    private val httpClient: HttpClient
) : GitHubApi {

    override suspend fun getLatestRelease(owner: String, repository: String): LibraryVersion =
        httpClient.get("repos/$owner/$repository/releases/latest").body()

    override suspend fun searchRepositories(
        repositoryName: String,
        page: Int,
        pageSize: Int
    ): SearchResults =
        httpClient.get("search/repositories") {
            parameter("q", "$repositoryName in:name")
            parameter("sort", "stars")
            parameter("page", page)
            parameter("per_page", "30")
        }.body()

}