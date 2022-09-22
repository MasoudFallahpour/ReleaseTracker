package ir.fallahpoor.releasetracker.data.repository.library

import ir.fallahpoor.releasetracker.data.repository.library.models.Library
import ir.fallahpoor.releasetracker.data.repository.library.models.SearchRepositoriesResult
import kotlinx.coroutines.flow.Flow

interface LibraryRepository {

    suspend fun getLibrary(libraryName: String): Library?

    suspend fun getLibraryVersion(libraryName: String, libraryUrl: String): String

    suspend fun addLibrary(libraryName: String, libraryUrl: String, libraryVersion: String)

    suspend fun deleteLibrary(library: Library)

    suspend fun updateLibrary(library: Library)

    suspend fun pinLibrary(library: Library, pin: Boolean)

    suspend fun getLibraries(): List<Library>

    fun getLibrariesAsFlow(): Flow<List<Library>>

    suspend fun searchLibraries(libraryName: String): List<SearchRepositoriesResult>

}