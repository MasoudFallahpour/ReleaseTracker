package ir.fallahpoor.releasetracker.data.fakes

import ir.fallahpoor.releasetracker.data.network.GitHubApi
import ir.fallahpoor.releasetracker.data.network.LibraryVersion
import ir.fallahpoor.releasetracker.data.network.models.SearchResultItem
import ir.fallahpoor.releasetracker.data.network.models.SearchResults
import ir.fallahpoor.releasetracker.data.repository.library.Library

class FakeGitHubApi : GitHubApi {

    private val allLibraries = listOf(
        FakeData.Coil.library,
        FakeData.Coroutines.library,
        FakeData.Eks.library,
        FakeData.Koin.library,
        FakeData.Kotlin.library,
        FakeData.Timber.library,
        FakeData.ReleaseTracker.library
    )

    override suspend fun getLatestRelease(owner: String, repository: String): LibraryVersion {
        val library: Library? = allLibraries.find { it.url.endsWith("$owner/$repository") }
        if (library != null) {
            return LibraryVersion(name = library.version, tagName = "")
        } else {
            throw Exception()
        }
    }

    override suspend fun searchRepositories(
        repositoryName: String, page: Int, pageSize: Int
    ): SearchResults {
        val items = allLibraries.filter {
            it.name.contains(repositoryName, ignoreCase = true)
        }.mapIndexed { index, library ->
            library.toSearchResult(id = index.toLong())
        }
        return SearchResults(totalCount = items.size, incompleteResults = false, items = items)
    }

    private fun Library.toSearchResult(id: Long) = SearchResultItem(
        id = id, name = this.name, url = this.url, description = ""
    )

}