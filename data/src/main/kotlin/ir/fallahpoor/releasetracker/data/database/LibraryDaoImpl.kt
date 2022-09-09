package ir.fallahpoor.releasetracker.data.database

import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import ir.fallahpoor.releasetracker.data.Database
import ir.fallahpoor.releasetracker.data.database.entity.LibraryEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LibraryDaoImpl @Inject constructor(database: Database) : LibraryDao {

    private val libraryQueries = database.libraryQueries

    override fun getAllAsFlow(): Flow<List<LibraryEntity>> =
        libraryQueries.getAll().asFlow().mapToList()

    override suspend fun getAll() = libraryQueries.getAll().executeAsList()

    override suspend fun get(libraryName: String): LibraryEntity? = try {
        libraryQueries.get(libraryName).executeAsOne()
    } catch (e: NullPointerException) {
        null
    } catch (e: IllegalStateException) {
        null
    }

    override suspend fun insert(library: LibraryEntity) {
        libraryQueries.insert(library)
    }

    override suspend fun update(library: LibraryEntity) {
        libraryQueries.update(version = library.version, name = library.name)
    }

    override suspend fun delete(libraryName: String) {
        libraryQueries.delete(libraryName)
    }

}