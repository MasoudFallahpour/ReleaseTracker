package ir.fallahpoor.releasetracker.data.repository.library

import ir.fallahpoor.releasetracker.data.database.LibraryDao
import ir.fallahpoor.releasetracker.data.database.entity.LibraryEntity
import ir.fallahpoor.releasetracker.data.network.GitHubApi
import ir.fallahpoor.releasetracker.data.network.models.LatestReleaseDto
import ir.fallahpoor.releasetracker.data.network.models.SearchRepositoriesResultDto
import ir.fallahpoor.releasetracker.data.repository.library.models.Library
import ir.fallahpoor.releasetracker.data.repository.library.models.SearchRepositoriesResult
import ir.fallahpoor.releasetracker.data.toLibrary
import ir.fallahpoor.releasetracker.data.toLibraryEntity
import ir.fallahpoor.releasetracker.data.toSearchRepositoriesResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class LibraryRepositoryImpl
@Inject constructor(
    private val libraryDao: LibraryDao, private val gitHubApi: GitHubApi
) : LibraryRepository {

    companion object {
        private const val GITHUB_BASE_URL = "https://github.com/"
        private const val MAX_VERSION_LENGTH = 15
    }

    private var searchQuery: String? = null
    private var searchResults: List<SearchRepositoriesResult> = emptyList()

    override suspend fun getLibraryVersion(libraryName: String, libraryUrl: String): String {
        val libraryPath = libraryUrl.trim().removePrefix(GITHUB_BASE_URL)
        val libraryOwner = libraryPath.substring(0 until libraryPath.indexOf("/"))
        val libraryRepo = libraryPath.substring(libraryPath.indexOf("/") + 1)
        val latestReleaseDto: LatestReleaseDto =
            gitHubApi.getLatestRelease(libraryOwner, libraryRepo)
        return getRefinedLibraryVersion(libraryName.trim(), latestReleaseDto)
    }

    private fun getRefinedLibraryVersion(
        libraryName: String, latestReleaseDto: LatestReleaseDto
    ): String {
        val version: String = latestReleaseDto.name.ifBlank { latestReleaseDto.tagName }
        return getRefinedLibraryVersion(libraryName, version)
    }

    /**
     * Sometimes the given version may contain irrelevant words/letters. Some examples are
     * 'Dagger 2.9.0' or 'v2.1.0'. This method removes such words/letters from the given
     * version.
     */
    private fun getRefinedLibraryVersion(libraryName: String, version: String): String =
        version.replace(libraryName, "", ignoreCase = true) // Remove the library name
            .replace("version", "", ignoreCase = true) // Remove the word "version"
            .replace("release", "", ignoreCase = true) // Remove the word "release"
            .replace("v", "", ignoreCase = true) // Remove the letter 'v'
            .replace("r", "", ignoreCase = true) // Remove the letter 'r'
            .trim()
            .take(MAX_VERSION_LENGTH)

    override suspend fun addLibrary(name: String, url: String) {
        val version = getLibraryVersion(libraryName = name, libraryUrl = url)
        libraryDao.insert(
            LibraryEntity(
                name = name.trim(),
                url = url.trim(),
                version = version,
                pinned = 0
            )
        )
    }

    override suspend fun deleteLibrary(library: Library) {
        libraryDao.delete(libraryName = library.name, libraryUrl = library.url)
    }

    override suspend fun updateLibrary(library: Library) {
        libraryDao.update(library.toLibraryEntity())
    }

    override suspend fun pinLibrary(library: Library, pin: Boolean) {
        val newLibrary = library.copy(isPinned = pin)
        libraryDao.update(library = newLibrary.toLibraryEntity())
    }

    override suspend fun getLibraries(): List<Library> =
        libraryDao.getAll().map(LibraryEntity::toLibrary)

    override fun getLibrariesAsFlow(): Flow<List<Library>> = libraryDao.getAllAsFlow()
        .map { libraryEntities -> libraryEntities.map(LibraryEntity::toLibrary) }

    override suspend fun searchLibraries(libraryName: String): List<SearchRepositoriesResult> {
        if (libraryName != searchQuery) {
            val searchRepositoriesResultDto = gitHubApi.searchRepositories(libraryName)
            searchQuery = libraryName
            searchResults =
                searchRepositoriesResultDto.items.map(SearchRepositoriesResultDto::toSearchRepositoriesResult)
        }
        return removeExistingLibraries(searchResults)
    }

    private suspend fun removeExistingLibraries(searchResults: List<SearchRepositoriesResult>): List<SearchRepositoriesResult> {
        val existingLibraries = libraryDao.getAll()
            .map { libraryEntity -> libraryEntity.name.lowercase() to libraryEntity.url.lowercase() }
            .toSet()
        return searchResults.filter { searchRepositoriesResult ->
            (searchRepositoriesResult.name.lowercase() to searchRepositoriesResult.url.lowercase()) !in existingLibraries
        }
    }

}