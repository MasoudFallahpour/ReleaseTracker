package ir.fallahpoor.releasetracker.data.repository

import ir.fallahpoor.releasetracker.data.entity.Library
import kotlinx.coroutines.flow.Flow

interface LibraryRepository {

    enum class Order {
        A_TO_Z,
        Z_TO_A,
        PINNED_FIRST
    }

    suspend fun addLibrary(libraryName: String, libraryUrl: String, libraryVersion: String)

    suspend fun updateLibrary(library: Library)

    suspend fun getLibrary(libraryName: String): Library?

    fun getLibraries(order: Order): Flow<List<Library>>

    suspend fun getLibraries(): List<Library>

    suspend fun deleteLibraries(libraryNames: List<String>)

    suspend fun getLibraryVersion(libraryName: String, libraryUrl: String): String

    suspend fun setPinned(library: Library, pinned: Boolean)

    fun getLastUpdateCheck(): Flow<String>

}