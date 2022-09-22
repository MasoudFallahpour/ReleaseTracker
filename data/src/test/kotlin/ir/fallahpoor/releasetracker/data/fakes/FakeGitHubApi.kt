package ir.fallahpoor.releasetracker.data.fakes

import ir.fallahpoor.releasetracker.data.network.GitHubApi
import ir.fallahpoor.releasetracker.data.network.models.LatestReleaseDto
import ir.fallahpoor.releasetracker.data.network.models.SearchRepositoriesResultDto
import ir.fallahpoor.releasetracker.data.repository.library.models.Library
import ir.fallahpoor.releasetracker.data.toSearchRepositoriesResultItemDto

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

    override suspend fun getLatestRelease(owner: String, repository: String): LatestReleaseDto {
        val library: Library? = allLibraries.find { it.url.endsWith("$owner/$repository") }
        if (library != null) {
            return LatestReleaseDto(name = library.version, tagName = "")
        } else {
            throw Exception()
        }
    }

    override suspend fun searchRepositories(
        repositoryName: String,
        page: Int,
        pageSize: Int
    ): SearchRepositoriesResultDto {
        val items = allLibraries.filter {
            it.name.contains(repositoryName, ignoreCase = true)
        }.mapIndexed { index, library ->
            library.toSearchRepositoriesResultItemDto(id = index)
        }
        return SearchRepositoriesResultDto(
            totalCount = items.size,
            incompleteResults = false,
            items = items
        )
    }

}