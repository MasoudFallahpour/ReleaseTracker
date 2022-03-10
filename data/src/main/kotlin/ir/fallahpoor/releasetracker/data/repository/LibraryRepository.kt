package ir.fallahpoor.releasetracker.data.repository

import ir.fallahpoor.releasetracker.data.entity.Library
import ir.fallahpoor.releasetracker.data.utils.SortOrder
import kotlinx.coroutines.flow.Flow

// TODO probably we can separate this interface into multiple ones.

interface LibraryRepository {

    suspend fun getLibrary(libraryName: String): Library?

    suspend fun getLibraryVersion(libraryName: String, libraryUrl: String): String

    suspend fun addLibrary(libraryName: String, libraryUrl: String, libraryVersion: String)

    suspend fun deleteLibrary(library: Library)

    suspend fun updateLibrary(library: Library)

    suspend fun pinLibrary(library: Library, pinned: Boolean)

    suspend fun getLibraries(): List<Library>

    fun getLibrariesAsFlow(): Flow<List<Library>>

    fun getLastUpdateCheck(): Flow<String>

    suspend fun setLastUpdateCheck(date: String)

    fun getSortOrder(): SortOrder

    fun getSortOrderAsFlow(): Flow<SortOrder>

    suspend fun setSortOrder(sortOrder: SortOrder)

}