package ir.fallahpoor.releasetracker.data.repository

import androidx.lifecycle.LiveData
import ir.fallahpoor.releasetracker.data.database.LibraryDao
import ir.fallahpoor.releasetracker.data.entity.Library
import ir.fallahpoor.releasetracker.data.entity.LibraryVersion
import ir.fallahpoor.releasetracker.data.webservice.GithubWebservice
import java.util.*
import javax.inject.Inject

class LibraryRepositoryImpl
@Inject constructor(
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

    override fun getLibrariesByLiveData(): LiveData<List<Library>> {
        return libraryDao.getAllLiveData()
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
        version.toLowerCase(Locale.US)
            // Remove the library name
            .replace(libraryName, "", ignoreCase = true)
            // Remove the word "version"
            .replace("version", "", ignoreCase = true)
            // Remove the word "release"
            .replace("release", "", ignoreCase = true)
            // Remove the letter 'v'
            .replace("v", "", ignoreCase = true)
            .trim()

    override suspend fun getLibraries(): List<Library> {
        return libraryDao.getAll()
    }

    override suspend fun getLibraryVersion(libraryName: String, libraryUrl: String): String {

        val libraryPath = libraryUrl.removePrefix(GITHUB_BASE_URL)
        val libraryOwner = libraryPath.substring(0 until libraryPath.indexOf("/"))
        val libraryRepo = libraryPath.substring(libraryPath.indexOf("/") + 1)

        val libraryVersion: LibraryVersion =
            githubWebservice.getLatestVersion(libraryOwner, libraryRepo)

        return getLibraryVersion(libraryName, libraryVersion)

    }

    override suspend fun setFavourite(library: Library, isFavourite: Boolean) {
        val newLibrary = library.copy(isFavourite = if (isFavourite) 1 else 0)
        libraryDao.update(newLibrary)
    }

}
