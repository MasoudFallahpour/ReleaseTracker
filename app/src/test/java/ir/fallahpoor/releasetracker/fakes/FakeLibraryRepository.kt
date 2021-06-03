package ir.fallahpoor.releasetracker.fakes

import ir.fallahpoor.releasetracker.data.entity.Library
import ir.fallahpoor.releasetracker.data.repository.LibraryRepository
import ir.fallahpoor.releasetracker.data.utils.SortOrder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.IOException

class FakeLibraryRepository : LibraryRepository {

    companion object {
        const val LIBRARY_NAME_TO_CAUSE_ERROR = "someLibraryName"
        const val LIBRARY_VERSION = "0.2"
    }

    private val libraries = mutableListOf<Library>()

    override suspend fun addLibrary(
        libraryName: String,
        libraryUrl: String,
        libraryVersion: String
    ) {
        if (libraryName == LIBRARY_NAME_TO_CAUSE_ERROR) {
            throw IOException()
        } else {
            val library: Library? = libraries.find { it.name == libraryName }
            if (library != null) {
                throw IOException()
            } else {
                libraries.add(Library(libraryName, libraryUrl, libraryVersion, 0))
            }
        }
    }

    override suspend fun updateLibrary(library: Library) {
        val removed: Boolean = libraries.removeIf { it.name == library.name }
        if (removed) {
            libraries.add(library)
        }
    }

    override suspend fun getLibrary(libraryName: String): Library? =
        libraries.firstOrNull { it.name == libraryName }

    override fun getLibraries(sortOrder: SortOrder, searchTerm: String): Flow<List<Library>> {

        val filteredLibraries = libraries.filter {
            it.name.contains(searchTerm, ignoreCase = true)
        }

        val sortedLibraries: List<Library> = when (sortOrder) {
            SortOrder.A_TO_Z -> filteredLibraries.sortedBy { it.name }
            SortOrder.Z_TO_A -> filteredLibraries.sortedByDescending { it.name }
            SortOrder.PINNED_FIRST -> filteredLibraries.sortedByDescending { it.pinned }
        }

        return flow {
            emit(sortedLibraries)
        }

    }

    override suspend fun getLibraries(): List<Library> = libraries

    override suspend fun deleteLibrary(library: Library) {
        if (library.name == LIBRARY_NAME_TO_CAUSE_ERROR) {
            throw IOException()
        } else {
            libraries.removeIf { it.name == library.name }
        }
    }

    override suspend fun getLibraryVersion(libraryName: String, libraryUrl: String): String {
        // TODO Correct the implementation
        return LIBRARY_VERSION
    }

    override suspend fun pinLibrary(library: Library, pinned: Boolean) {
        updateLibrary(library.copy(pinned = if (pinned) 1 else 0))
    }

    override fun getLastUpdateCheck(): Flow<String> =
        flow {
            emit("Jan 14 2021 14:20")
        }

    override fun setLastUpdateCheck(date: String) {
        TODO("Not yet implemented")
    }

}