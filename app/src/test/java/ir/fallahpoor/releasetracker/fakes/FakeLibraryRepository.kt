package ir.fallahpoor.releasetracker.fakes

import ir.fallahpoor.releasetracker.data.entity.Library
import ir.fallahpoor.releasetracker.data.repository.LibraryRepository
import ir.fallahpoor.releasetracker.data.utils.SortOrder
import kotlinx.coroutines.flow.Flow

class FakeLibraryRepository : LibraryRepository {

    companion object {
        const val LIBRARY_VERSION = "0.2"
    }

    private val libraries = mutableListOf<Library>()

    override suspend fun addLibrary(
        libraryName: String,
        libraryUrl: String,
        libraryVersion: String
    ) {
        val library: Library? = libraries.find { it.name == libraryName }
        if (library != null) {
            throw RuntimeException()
        } else {
            libraries.add(Library(libraryName, libraryUrl, libraryVersion, 0))
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
        TODO("Not yet implemented")
    }

    override suspend fun getLibraries(): List<Library> = libraries

    override suspend fun deleteLibrary(library: Library) {
        libraries.removeIf { it.name == library.name }
    }

    override suspend fun getLibraryVersion(libraryName: String, libraryUrl: String): String {
        // TODO Correct the implementation
        return LIBRARY_VERSION
    }

    override suspend fun pinLibrary(library: Library, pinned: Boolean) {
        updateLibrary(library.copy(pinned = if (pinned) 1 else 0))
    }

    override fun getLastUpdateCheck(): Flow<String> {
        TODO("Not yet implemented")
    }

    override fun setLastUpdateCheck(date: String) {
        TODO("Not yet implemented")
    }

}