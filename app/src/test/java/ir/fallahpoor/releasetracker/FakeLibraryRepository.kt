package ir.fallahpoor.releasetracker

import ir.fallahpoor.releasetracker.data.entity.Library
import ir.fallahpoor.releasetracker.data.repository.LibraryRepository
import ir.fallahpoor.releasetracker.data.utils.SortOrder
import kotlinx.coroutines.flow.Flow

class FakeLibraryRepository : LibraryRepository {

    companion object {
        const val EXISTING_LIBRARY_NAME = "ReleaseTracker"
        const val LIBRARY_VERSION = "0.2"
    }

    override suspend fun addLibrary(
        libraryName: String,
        libraryUrl: String,
        libraryVersion: String
    ) {
    }

    override suspend fun updateLibrary(library: Library) {
        TODO("Not yet implemented")
    }

    override suspend fun getLibrary(libraryName: String): Library? {
        return if (libraryName == EXISTING_LIBRARY_NAME) {
            Library(
                name = "ReleaseTracker",
                url = "https://github.com/masoodfallahpoor/ReleaseTracker",
                version = LIBRARY_VERSION
            )
        } else {
            null
        }
    }

    override fun getLibraries(sortOrder: SortOrder, searchTerm: String): Flow<List<Library>> {
        TODO("Not yet implemented")
    }

    override suspend fun getLibraries(): List<Library> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteLibrary(library: Library) {
        TODO("Not yet implemented")
    }

    override suspend fun getLibraryVersion(libraryName: String, libraryUrl: String): String {
        return LIBRARY_VERSION
    }

    override suspend fun pinLibrary(library: Library, pinned: Boolean) {
        TODO("Not yet implemented")
    }

    override fun getLastUpdateCheck(): Flow<String> {
        TODO("Not yet implemented")
    }

    override fun setLastUpdateCheck(date: String) {
        TODO("Not yet implemented")
    }

}