package ir.fallahpoor.releasetracker.fakes

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asFlow
import androidx.lifecycle.map
import ir.fallahpoor.releasetracker.common.GITHUB_BASE_URL
import ir.fallahpoor.releasetracker.data.InternetNotConnectedException
import ir.fallahpoor.releasetracker.data.repository.library.Library
import ir.fallahpoor.releasetracker.data.repository.library.LibraryRepository
import ir.fallahpoor.releasetracker.data.utils.ExceptionParser
import ir.fallahpoor.releasetracker.data.utils.SortOrder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeLibraryRepository : LibraryRepository {

    object Coil {
        const val name = "Coil"
        const val url = GITHUB_BASE_URL + "coil-kt/coil"
        const val version = "1.3.1"
        val library = Library(name = name, url = url, version = version, isPinned = false)
    }

    object Kotlin {
        const val name = "Kotlin"
        const val url = GITHUB_BASE_URL + "JetBrains/kotlin"
        const val version = "1.5.21"
        val library = Library(name = name, url = url, version = version, isPinned = false)
    }

    object Koin {
        const val name = "Koin"
        const val url = GITHUB_BASE_URL + "InsertKoinIO/koin"
        const val version = "3.1.2"
        val library = Library(name = name, url = url, version = version, isPinned = true)
    }

    companion object {
        const val LAST_UPDATE_CHECK = "N/A"
        const val LIBRARY_NAME_TO_CAUSE_ERROR_WHEN_ADDING = "Coroutines"
        const val LIBRARY_NAME_TO_CAUSE_ERROR_WHEN_DELETING = Kotlin.name
        const val ERROR_MESSAGE = ExceptionParser.SOMETHING_WENT_WRONG
        const val LIBRARY_VERSION = "0.2"
    }

    private val libraries = mutableListOf(
        Coil.library,
        Kotlin.library,
        Koin.library
    )
    private val librariesLiveData = MutableLiveData<List<Library>>(libraries)
    private var sortOrderLiveData = MutableLiveData(SortOrder.A_TO_Z)

    override suspend fun addLibrary(
        libraryName: String,
        libraryUrl: String,
        libraryVersion: String
    ) {
        if (libraryName.trim() == LIBRARY_NAME_TO_CAUSE_ERROR_WHEN_ADDING) {
            throw InternetNotConnectedException()
        } else {
            val library: Library? = libraries.find {
                it.name.equals(libraryName.trim(), ignoreCase = true)
            }
            if (library != null) {
                throw RuntimeException(ExceptionParser.SOMETHING_WENT_WRONG)
            } else {
                libraries += Library(libraryName.trim(), libraryUrl.trim(), libraryVersion, false)
                updateLibrariesLiveData(libraries)
            }
        }
    }

    override suspend fun updateLibrary(library: Library) {
        val removed: Boolean = libraries.removeIf {
            it.name.equals(library.name.trim(), ignoreCase = true)
        }
        if (removed) {
            libraries += library
            updateLibrariesLiveData(libraries)
        }
    }

    override suspend fun getLibrary(libraryName: String): Library? =
        libraries.firstOrNull { it.name.equals(libraryName.trim(), ignoreCase = true) }

    override fun getLibrariesAsFlow(): Flow<List<Library>> =
        librariesLiveData.map { libraries: List<Library> ->
            libraries.sortedBy { it.name }
        }.asFlow()

    override suspend fun getLibraries(): List<Library> = libraries

    override suspend fun deleteLibrary(library: Library) {
        if (library.name == LIBRARY_NAME_TO_CAUSE_ERROR_WHEN_DELETING) {
            throw RuntimeException(ERROR_MESSAGE)
        } else {
            val removed = libraries.removeIf { it.name.equals(library.name, ignoreCase = true) }
            if (removed) {
                updateLibrariesLiveData(libraries)
            }
        }
    }

    override suspend fun getLibraryVersion(libraryName: String, libraryUrl: String): String {
        // TODO Correct the implementation
        return LIBRARY_VERSION
    }

    override suspend fun pinLibrary(library: Library, pinned: Boolean) {
        updateLibrary(library.copy(isPinned = pinned))
    }

    override fun getLastUpdateCheck(): Flow<String> = flow {
        emit(LAST_UPDATE_CHECK)
    }

    override suspend fun setLastUpdateCheck(date: String) {
        TODO("Not yet implemented")
    }

    override suspend fun setSortOrder(sortOrder: SortOrder) {
        sortOrderLiveData.value = sortOrder
    }

    override fun getSortOrder() = sortOrderLiveData.value!!

    override fun getSortOrderAsFlow(): Flow<SortOrder> = sortOrderLiveData.asFlow()

    fun deleteLibraries() {
        libraries.clear()
        updateLibrariesLiveData(libraries)
    }

    private fun updateLibrariesLiveData(libraries: List<Library>) {
        librariesLiveData.value = libraries
    }

}