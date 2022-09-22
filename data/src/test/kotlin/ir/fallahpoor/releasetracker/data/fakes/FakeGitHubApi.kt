package ir.fallahpoor.releasetracker.data.fakes

import ir.fallahpoor.releasetracker.data.network.GitHubApi
import ir.fallahpoor.releasetracker.data.network.models.LatestRelease
import ir.fallahpoor.releasetracker.data.network.models.SearchRepositoriesResult
import ir.fallahpoor.releasetracker.data.repository.library.Library
import ir.fallahpoor.releasetracker.data.toSearchResultItem

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

    override suspend fun getLatestRelease(owner: String, repository: String): LatestRelease {
        val library: Library? = allLibraries.find { it.url.endsWith("$owner/$repository") }
        if (library != null) {
            return LatestRelease(name = library.version, tagName = "")
        } else {
            throw Exception()
        }
    }

    override suspend fun searchRepositories(
        repositoryName: String,
        page: Int,
        pageSize: Int
    ): SearchRepositoriesResult {
        val items = allLibraries.filter {
            it.name.contains(repositoryName, ignoreCase = true)
        }.mapIndexed { index, library ->
            library.toSearchResultItem(id = index)
        }
        return SearchRepositoriesResult(
            totalCount = items.size,
            incompleteResults = false,
            items = items
        )
    }

}