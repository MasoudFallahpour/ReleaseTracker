package ir.fallahpoor.releasetracker.data.fakes

import ir.fallahpoor.releasetracker.data.TestData
import ir.fallahpoor.releasetracker.data.entity.LibraryVersion
import ir.fallahpoor.releasetracker.data.webservice.GithubWebService

class FakeGithubWebService : GithubWebService {

    override suspend fun getLatestVersion(owner: String, repo: String): LibraryVersion =
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

}