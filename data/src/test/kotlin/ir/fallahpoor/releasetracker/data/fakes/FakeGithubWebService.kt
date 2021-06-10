package ir.fallahpoor.releasetracker.data.fakes

import ir.fallahpoor.releasetracker.data.TestData.OWNER_1
import ir.fallahpoor.releasetracker.data.TestData.TAG_NAME_1
import ir.fallahpoor.releasetracker.data.TestData.TAG_NAME_2
import ir.fallahpoor.releasetracker.data.TestData.VERSION_1
import ir.fallahpoor.releasetracker.data.entity.LibraryVersion
import ir.fallahpoor.releasetracker.data.webservice.GithubWebservice

class FakeGithubWebService : GithubWebservice {

    override suspend fun getLatestVersion(owner: String, repo: String): LibraryVersion {
        return if (owner == OWNER_1) {
            LibraryVersion(
                name = VERSION_1,
                tagName = TAG_NAME_1
            )
        } else {
            LibraryVersion(
                name = "",
                tagName = TAG_NAME_2
            )
        }
    }

}