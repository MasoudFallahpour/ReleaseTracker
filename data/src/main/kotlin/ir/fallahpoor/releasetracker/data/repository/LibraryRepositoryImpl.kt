package ir.fallahpoor.releasetracker.data.repository

import ir.fallahpoor.releasetracker.data.database.LibraryDao
import ir.fallahpoor.releasetracker.data.entity.Library
import ir.fallahpoor.releasetracker.data.entity.LibraryVersion
import ir.fallahpoor.releasetracker.data.storage.Storage
import ir.fallahpoor.releasetracker.data.utils.SortOrder
import ir.fallahpoor.releasetracker.data.webservice.GithubWebService
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LibraryRepositoryImpl
@Inject constructor(
    private val storage: Storage,
    private val libraryDao: LibraryDao,
    private val githubWebservice: GithubWebService
) : LibraryRepository {

    companion object {
        private const val GITHUB_BASE_URL = "https://github.com/"
    }

    override suspend fun getLibrary(libraryName: String): Library? =
        libraryDao.get(libraryName.trim())

    override suspend fun getLibraryVersion(libraryName: String, libraryUrl: String): String {

        val libraryPath = libraryUrl.trim().removePrefix(GITHUB_BASE_URL)
        val libraryOwner = libraryPath.substring(0 until libraryPath.indexOf("/"))
        val libraryRepo = libraryPath.substring(libraryPath.indexOf("/") + 1)

        val libraryVersion: LibraryVersion =
            githubWebservice.getLatestVersion(libraryOwner, libraryRepo)

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
        libraryDao.insert(Library(libraryName.trim(), libraryUrl.trim(), libraryVersion))
    }

    override suspend fun deleteLibrary(library: Library) {
        libraryDao.delete(library.name)
    }

    override suspend fun updateLibrary(library: Library) {
        libraryDao.update(library)
    }

    override suspend fun pinLibrary(library: Library, pinned: Boolean) {
        val newLibrary = library.copy(pinned = if (pinned) 1 else 0)
        libraryDao.update(newLibrary)
    }

    override suspend fun getLibraries(): List<Library> = libraryDao.getAll()

    override fun getLibrariesAsFlow(): Flow<List<Library>> = libraryDao.getAllAsFlow()

    override fun getLastUpdateCheck(): Flow<String> = storage.getLastUpdateCheck()

    override suspend fun setLastUpdateCheck(date: String) {
        storage.setLastUpdateCheck(date)
    }

    override fun getSortOrder(): SortOrder = storage.getSortOrder()

    override fun getSortOrderAsFlow(): Flow<SortOrder> = storage.getSortOrderAsFlow()

    override suspend fun setSortOrder(sortOrder: SortOrder) {
        storage.setSortOrder(sortOrder)
    }

}
