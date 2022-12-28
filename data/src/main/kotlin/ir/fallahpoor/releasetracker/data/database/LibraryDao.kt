package ir.fallahpoor.releasetracker.data.database

import ir.fallahpoor.releasetracker.data.database.entity.LibraryEntity
import kotlinx.coroutines.flow.Flow

interface LibraryDao {

    fun getAllAsFlow(): Flow<List<LibraryEntity>>

    suspend fun getAll(): List<LibraryEntity>

    suspend fun insert(library: LibraryEntity)

    suspend fun update(library: LibraryEntity)

    suspend fun delete(libraryName: String, libraryUrl: String)

}