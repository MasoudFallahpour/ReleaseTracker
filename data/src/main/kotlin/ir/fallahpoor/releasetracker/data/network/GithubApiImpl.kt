package ir.fallahpoor.releasetracker.data.network

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import ir.fallahpoor.releasetracker.data.BuildConfig
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GithubApiImpl
@Inject constructor(
    private val httpClient: HttpClient
) : GithubApi {

    override suspend fun getLatestVersion(owner: String, repository: String): LibraryVersion =
        httpClient.get("https://api.github.com/repos/$owner/$repository/releases/latest") {
            header(HttpHeaders.Authorization, "token ${BuildConfig.ACCESS_TOKEN}")
        }.body()

}