package ir.fallahpoor.releasetracker.data.database

import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import ir.fallahpoor.releasetracker.data.Database
import ir.fallahpoor.releasetracker.data.database.entity.LibraryEntity
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class LibraryDaoImpl @Inject constructor(
    database: Database,
    private val dispatcher: CoroutineDispatcher
) : LibraryDao {

    private val libraryQueries = database.libraryQueries

    override fun getAllAsFlow(): Flow<List<LibraryEntity>> =
        libraryQueries.getAll().asFlow().mapToList()

    override suspend fun getAll(): List<LibraryEntity> = withContext(dispatcher) {
        libraryQueries.getAll().executeAsList()
    }

    override suspend fun get(libraryName: String): LibraryEntity? = withContext(dispatcher) {
        try {
            libraryQueries.get(libraryName).executeAsOne()
        } catch (e: NullPointerException) {
            null
        } catch (e: IllegalStateException) {
            null
        }
    }

    override suspend fun insert(library: LibraryEntity) = withContext(dispatcher) {
        libraryQueries.insert(library)
    }

    override suspend fun update(library: LibraryEntity) = withContext(dispatcher) {
        libraryQueries.update(
            url = library.url,
            version = library.version,
            pinned = library.pinned,
            name = library.name
        )
    }

    override suspend fun delete(libraryName: String) = withContext(dispatcher) {
        libraryQueries.delete(libraryName)
    }

}