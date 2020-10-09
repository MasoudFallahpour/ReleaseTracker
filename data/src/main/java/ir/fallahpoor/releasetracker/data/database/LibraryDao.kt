package ir.fallahpoor.releasetracker.data.database

import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteQuery
import ir.fallahpoor.releasetracker.data.entity.Library

@Dao
interface LibraryDao {

    @RawQuery(observedEntities = [Library::class])
    fun getAllLiveData(query: SupportSQLiteQuery): LiveData<List<Library>>

    @Query("SELECT * FROM library ORDER BY name")
    suspend fun getAll(): List<Library>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(library: Library)

    @Update
    suspend fun update(library: Library)

    @Query("DELETE FROM library WHERE name IN (:ids)")
    suspend fun delete(ids: List<String>)

    @Query("SELECT * FROM library WHERE name = :libraryName COLLATE NOCASE LIMIT 1")
    suspend fun get(libraryName: String): Library?

}