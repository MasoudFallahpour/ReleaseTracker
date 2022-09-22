package ir.fallahpoor.releasetracker.data.repository.library

import ir.fallahpoor.releasetracker.data.database.LibraryDao
import ir.fallahpoor.releasetracker.data.database.entity.LibraryEntity
import ir.fallahpoor.releasetracker.data.network.GitHubApi
import ir.fallahpoor.releasetracker.data.network.LibraryVersion
import ir.fallahpoor.releasetracker.data.network.models.SearchResults
import ir.fallahpoor.releasetracker.data.toLibrary
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class LibraryRepositoryImpl
@Inject constructor(
    private val libraryDao: LibraryDao,
    private val gitHubApi: GitHubApi
) : LibraryRepository {

    companion object {
        private const val GITHUB_BASE_URL = "https://github.com/"
    }

    override suspend fun getLibrary(libraryName: String): Library? {
        val libraryEntity = libraryDao.get(libraryName.trim())
        return libraryEntity?.toLibrary()
    }

    override suspend fun getLibraryVersion(libraryName: String, libraryUrl: String): String {
        val libraryPath = libraryUrl.trim().removePrefix(GITHUB_BASE_URL)
        val libraryOwner = libraryPath.substring(0 until libraryPath.indexOf("/"))
        val libraryRepo = libraryPath.substring(libraryPath.indexOf("/") + 1)
        val libraryVersion: LibraryVersion = gitHubApi.getLatestRelease(libraryOwner, libraryRepo)
        return getRefinedLibraryVersion(libraryName.trim(), libraryVersion)
    }

    private fun getRefinedLibraryVersion(
        libraryName: String,
        libraryVersion: LibraryVersion
    ): String {
        val version: String = libraryVersion.name.ifBlank { libraryVersion.tagName }
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

    override suspend fun addLibrary(
        libraryName: String,
        libraryUrl: String,
        libraryVersion: String
    ) {
        libraryDao.insert(
            LibraryEntity(
                name = libraryName.trim(),
                url = libraryUrl.trim(),
                version = libraryVersion,
                pinned = 0
            )
        )
    }

    override suspend fun deleteLibrary(library: Library) {
        libraryDao.delete(library.name)
    }

    override suspend fun updateLibrary(library: Library) {
        libraryDao.update(library.toLibraryEntity())
    }

    override suspend fun pinLibrary(library: Library, pin: Boolean) {
        val newLibrary = library.copy(isPinned = pin)
        libraryDao.update(newLibrary.toLibraryEntity())
    }

    override suspend fun getLibraries(): List<Library> =
        libraryDao.getAll().map { libraryEntity -> libraryEntity.toLibrary() }

    override fun getLibrariesAsFlow(): Flow<List<Library>> =
        libraryDao.getAllAsFlow().map { libraryEntities -> libraryEntities.map { it.toLibrary() } }

    override suspend fun searchLibraries(libraryName: String): SearchResults =
        gitHubApi.searchRepositories(libraryName)

}
