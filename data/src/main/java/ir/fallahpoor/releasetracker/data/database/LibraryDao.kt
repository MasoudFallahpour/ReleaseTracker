package ir.fallahpoor.releasetracker.data.database

import androidx.room.*
import androidx.sqlite.db.SupportSQLiteQuery
import ir.fallahpoor.releasetracker.data.entity.Library
import kotlinx.coroutines.flow.Flow

@Dao
interface LibraryDao {

    @RawQuery(observedEntities = [Library::class])
    fun getAll(query: SupportSQLiteQuery): Flow<List<Library>>

    @Query("SELECT * FROM ${DatabaseContract.TABLE_NAME} ORDER BY ${DatabaseContract.FIELD_NAME}")
    suspend fun getAll(): List<Library>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(library: Library)

    @Update
    suspend fun update(library: Library)

    @Query("DELETE FROM ${DatabaseContract.TABLE_NAME} WHERE ${DatabaseContract.FIELD_NAME} = :libraryName")
    suspend fun delete(libraryName: String)

    @Query("SELECT * FROM ${DatabaseContract.TABLE_NAME} WHERE ${DatabaseContract.FIELD_NAME} = :libraryName COLLATE NOCASE LIMIT 1")
    suspend fun get(libraryName: String): Library?

}