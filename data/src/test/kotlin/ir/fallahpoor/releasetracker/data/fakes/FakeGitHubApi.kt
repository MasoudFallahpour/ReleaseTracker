package ir.fallahpoor.releasetracker.data.fakes

import ir.fallahpoor.releasetracker.data.TestData
import ir.fallahpoor.releasetracker.data.network.GitHubApi
import ir.fallahpoor.releasetracker.data.network.LibraryVersion
import ir.fallahpoor.releasetracker.data.network.models.SearchResults

class FakeGitHubApi : GitHubApi {

    override suspend fun getLatestRelease(owner: String, repository: String): LibraryVersion =
        if (owner == TestData.OWNER_1) {
            LibraryVersion(
                name = TestData.VERSION_1,
                tagName = TestData.TAG_NAME_1
            )
        } else {
            LibraryVersion(
                name = "",
                tagName = TestData.TAG_NAME_2
            )
        }

    override suspend fun searchRepositories(
        repositoryName: String,
        page: Int,
        pageSize: Int
    ): List<SearchResults> {
        TODO("Not yet implemented")
    }

}