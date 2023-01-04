package ir.fallahpoor.releasetracker.data.database

import ir.fallahpoor.releasetracker.data.database.entity.LibraryEntity
import kotlinx.coroutines.flow.Flow

interface LibraryDao {

    fun getAllLibrariesAsFlow(): Flow<List<LibraryEntity>>

    suspend fun getAllLibraries(): List<LibraryEntity>

    suspend fun insertLibrary(library: LibraryEntity)

    suspend fun updateLibrary(library: LibraryEntity)

    suspend fun deleteLibrary(libraryName: String, libraryUrl: String)

}