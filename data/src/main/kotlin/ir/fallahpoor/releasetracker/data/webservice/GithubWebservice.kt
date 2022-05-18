package ir.fallahpoor.releasetracker.data.webservice

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import ir.fallahpoor.releasetracker.data.BuildConfig
import ir.fallahpoor.releasetracker.data.entity.LibraryVersion
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GithubWebservice @Inject constructor(private val httpClient: HttpClient) {

    suspend fun getLatestVersion(owner: String, repository: String): LibraryVersion =
        httpClient.get("https://api.github.com/repos/$owner/$repository/releases/latest") {
            header(HttpHeaders.Authorization, "token ${BuildConfig.ACCESS_TOKEN}")
        }.body()

}