package ir.fallahpoor.releasetracker.fakes

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asFlow
import androidx.lifecycle.map
import ir.fallahpoor.releasetracker.data.exceptions.ExceptionParser
import ir.fallahpoor.releasetracker.data.network.models.SearchRepositoriesResult
import ir.fallahpoor.releasetracker.data.network.models.SearchRepositoriesResultItem
import ir.fallahpoor.releasetracker.data.repository.library.Library
import ir.fallahpoor.releasetracker.data.repository.library.LibraryRepository
import kotlinx.coroutines.flow.Flow
import java.io.IOException

class FakeLibraryRepository : LibraryRepository {

    companion object {
        const val LIBRARY_NAME_TO_CAUSE_ERROR_WHEN_ADDING = "Coroutines"
        const val LIBRARY_NAME_TO_CAUSE_ERROR_WHEN_DELETING = FakeData.Kotlin.name
        const val ERROR_MESSAGE = ExceptionParser.SOMETHING_WENT_WRONG
        const val LIBRARY_VERSION = "0.2"
    }

    private val localLibraries = mutableListOf(
        FakeData.Coil.library,
        FakeData.Kotlin.library,
        FakeData.Koin.library
    )
    private val remoteLibraries = listOf(
        FakeData.Coil.library,
        FakeData.Coroutines.library,
        FakeData.Eks.library,
        FakeData.Koin.library,
        FakeData.Kotlin.library,
        FakeData.Timber.library,
        FakeData.ReleaseTracker.library
    )
    private val localLibrariesLiveData = MutableLiveData<List<Library>>(localLibraries)

    override suspend fun addLibrary(
        libraryName: String,
        libraryUrl: String,
        libraryVersion: String
    ) {
        if (libraryName.trim() == LIBRARY_NAME_TO_CAUSE_ERROR_WHEN_ADDING) {
            throw IOException(ERROR_MESSAGE)
        } else {
            val library: Library? = localLibraries.find {
                it.name.equals(libraryName.trim(), ignoreCase = true)
            }
            if (library != null) {
                throw RuntimeException(ExceptionParser.SOMETHING_WENT_WRONG)
            } else {
                localLibraries += Library(
                    libraryName.trim(),
                    libraryUrl.trim(),
                    libraryVersion,
                    isPinned = false
                )
                updateLibrariesLiveData(localLibraries)
            }
        }
    }

    override suspend fun updateLibrary(library: Library) {
        val removed: Boolean = localLibraries.removeIf {
            it.name.equals(library.name, ignoreCase = true)
        }
        if (removed) {
            localLibraries += library
            updateLibrariesLiveData(localLibraries)
        }
    }

    override suspend fun getLibrary(libraryName: String): Library? =
        localLibraries.firstOrNull { it.name.equals(libraryName.trim(), ignoreCase = true) }

    override fun getLibrariesAsFlow(): Flow<List<Library>> =
        localLibrariesLiveData.map { libraries: List<Library> ->
            libraries.sortedBy { it.name }
        }.asFlow()

    override suspend fun searchLibraries(libraryName: String): SearchRepositoriesResult {
        val items = remoteLibraries.filter { it.name.contains(libraryName, ignoreCase = true) }
            .mapIndexed { index, library ->
                library.toSearchResultItem(id = index.toLong())
            }
        return SearchRepositoriesResult(
            totalCount = items.size,
            incompleteResults = false,
            items = items
        )
    }

    override suspend fun getLibraries(): List<Library> = localLibraries

    override suspend fun deleteLibrary(library: Library) {
        if (library.name == LIBRARY_NAME_TO_CAUSE_ERROR_WHEN_DELETING) {
            throw RuntimeException(ERROR_MESSAGE)
        } else {
            val removed =
                localLibraries.removeIf { it.name.equals(library.name, ignoreCase = true) }
            if (removed) {
                updateLibrariesLiveData(localLibraries)
            }
        }
    }

    override suspend fun getLibraryVersion(libraryName: String, libraryUrl: String): String {
        // TODO Correct the implementation
        return LIBRARY_VERSION
    }

    override suspend fun pinLibrary(library: Library, pin: Boolean) {
        updateLibrary(library.copy(isPinned = pin))
    }

    fun deleteLibraries() {
        localLibraries.clear()
        updateLibrariesLiveData(localLibraries)
    }

    private fun updateLibrariesLiveData(libraries: List<Library>) {
        localLibrariesLiveData.value = libraries
    }

    private fun Library.toSearchResultItem(id: Long) = SearchRepositoriesResultItem(
        id = id,
        name = this.name,
        url = this.url,
        description = ""
    )

}