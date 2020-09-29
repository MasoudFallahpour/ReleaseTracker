package ir.fallahpoor.releasetracker.data.webservice

import ir.fallahpoor.releasetracker.data.entity.GithubResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface GithubWebservice {

    @GET("repos/{owner}/{repo}/releases/latest")
    suspend fun getLatestVersion(
        @Path("owner") owner: String,
        @Path("repo") repo: String
    ): GithubResponse

}