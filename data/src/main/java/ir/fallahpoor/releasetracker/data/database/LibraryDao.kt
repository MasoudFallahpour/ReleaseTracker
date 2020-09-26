package ir.fallahpoor.releasetracker.data.database

import androidx.room.*
import ir.fallahpoor.releasetracker.data.entity.Library

@Dao
interface LibraryDao {

    @Query("SELECT * FROM library")
    suspend fun getAll(): List<Library>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(library: Library)

    @Delete
    suspend fun delete(library: Library)

}