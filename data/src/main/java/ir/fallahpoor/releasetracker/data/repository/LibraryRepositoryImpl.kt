package ir.fallahpoor.releasetracker.data.repository

import androidx.sqlite.db.SimpleSQLiteQuery
import ir.fallahpoor.releasetracker.data.database.LibraryDao
import ir.fallahpoor.releasetracker.data.entity.Library
import ir.fallahpoor.releasetracker.data.entity.LibraryVersion
import ir.fallahpoor.releasetracker.data.utils.LocalStorage
import ir.fallahpoor.releasetracker.data.webservice.GithubWebservice
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LibraryRepositoryImpl
@Inject constructor(
    private val localStorage: LocalStorage,
    private val libraryDao: LibraryDao,
    private val githubWebservice: GithubWebservice
) : LibraryRepository {

    companion object {
        private const val GITHUB_BASE_URL = "https://github.com/"
    }

    override suspend fun addLibrary(
        libraryName: String,
        libraryUrl: String,
        libraryVersion: String
    ) {
        libraryDao.insert(Library(libraryName, libraryUrl, libraryVersion))
    }

    override suspend fun updateLibrary(library: Library) {
        libraryDao.update(library)
    }

    override suspend fun getLibrary(libraryName: String): Library? {
        return libraryDao.get(libraryName)
    }

    override fun getLibraries(order: LibraryRepository.Order): Flow<List<Library>> {

        val query = when (order) {
            LibraryRepository.Order.A_TO_Z -> "SELECT * FROM library ORDER BY name ASC"
            LibraryRepository.Order.Z_TO_A -> "SELECT * FROM library ORDER BY name DESC"
            LibraryRepository.Order.PINNED_FIRST -> "SELECT * FROM library ORDER BY pinned DESC, name ASC"
        }

        return libraryDao.getAll(SimpleSQLiteQuery(query))

    }

    private fun getLibraryVersion(libraryName: String, libraryVersion: LibraryVersion): String {
        return if (libraryVersion.name.isNotBlank()) {
            getRefinedLibraryVersion(libraryName, libraryVersion.name)
        } else {
            getRefinedLibraryVersion(libraryName, libraryVersion.tagName)
        }
    }

    /**
     * Sometimes the given version may contain irrelevant words/letters. Some examples are
     * 'Dagger 2.9.0' or 'v2.1.0'. This method removes such words/letters from the given
     * version.
     */
    private fun getRefinedLibraryVersion(libraryName: String, version: String): String =
        version
            .replace(libraryName, "", ignoreCase = true) // Remove the library name
            .replace("version", "", ignoreCase = true) // Remove the word "version"
            .replace("release", "", ignoreCase = true) // Remove the word "release"
            .replace("v", "", ignoreCase = true) // Remove the letter 'v'
            .trim()

    override suspend fun getLibraries(): List<Library> =
        libraryDao.getAll()

    override suspend fun deleteLibraries(libraryNames: List<String>) {
        libraryDao.delete(libraryNames)
    }

    override suspend fun getLibraryVersion(libraryName: String, libraryUrl: String): String {

        val libraryPath = libraryUrl.removePrefix(GITHUB_BASE_URL)
        val libraryOwner = libraryPath.substring(0 until libraryPath.indexOf("/"))
        val libraryRepo = libraryPath.substring(libraryPath.indexOf("/") + 1)

        val libraryVersion: LibraryVersion =
            githubWebservice.getLatestVersion(libraryOwner, libraryRepo)

        return getLibraryVersion(libraryName, libraryVersion)

    }

    override suspend fun setPinned(library: Library, pinned: Boolean) {
        val newLibrary = library.copy(pinned = if (pinned) 1 else 0)
        libraryDao.update(newLibrary)
    }

    override fun getLastUpdateCheck(): Flow<String> =
        localStorage.getLastUpdateCheck()

}
