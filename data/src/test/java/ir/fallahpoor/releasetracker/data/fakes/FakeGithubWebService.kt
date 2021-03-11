package ir.fallahpoor.releasetracker.data.fakes

import ir.fallahpoor.releasetracker.data.entity.LibraryVersion
import ir.fallahpoor.releasetracker.data.webservice.GithubWebservice

class FakeGithubWebService : GithubWebservice {

    override suspend fun getLatestVersion(owner: String, repo: String): LibraryVersion {
        return LibraryVersion("0.2", "v0.2")
    }

}