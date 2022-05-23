package ir.fallahpoor.releasetracker.data.webservice

import ir.fallahpoor.releasetracker.data.entity.LibraryVersion

interface GithubWebService {
    suspend fun getLatestVersion(owner: String, repository: String): LibraryVersion
}