package ir.fallahpoor.releasetracker.data.database

import androidx.lifecycle.LiveData
import androidx.room.*
import ir.fallahpoor.releasetracker.data.entity.Library

@Dao
interface LibraryDao {

    @Query("SELECT * FROM library ORDER BY name")
    fun getAllLiveData(): LiveData<List<Library>>

    @Query("SELECT * FROM library ORDER BY name")
    suspend fun getAll(): List<Library>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(library: Library)

    @Update
    suspend fun update(library: Library)

    @Delete
    suspend fun delete(library: Library)

    @Query("SELECT * FROM library WHERE name = :libraryName COLLATE NOCASE LIMIT 1")
    suspend fun get(libraryName: String): Library?

}