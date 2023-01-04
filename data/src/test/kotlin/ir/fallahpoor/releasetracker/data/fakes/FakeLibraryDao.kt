package ir.fallahpoor.releasetracker.data.fakes

import android.database.sqlite.SQLiteConstraintException
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asFlow
import ir.fallahpoor.releasetracker.data.database.LibraryDao
import ir.fallahpoor.releasetracker.data.database.entity.LibraryEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FakeLibraryDao : LibraryDao {

    private val librariesLiveData = MutableLiveData<List<LibraryEntity>>()
    private val libraries = mutableListOf<LibraryEntity>()

    override fun getAllLibrariesAsFlow(): Flow<List<LibraryEntity>> =
        librariesLiveData.asFlow().map {
            libraries.sortedBy { it.name }
        }

    override suspend fun getAllLibraries(): List<LibraryEntity> = libraries.sortedBy {
        it.name
    }

    override suspend fun insertLibrary(library: LibraryEntity) {
        if (libraries.contains(library)) {
            throw SQLiteConstraintException()
        } else {
            libraries += library
            updateLibrariesLiveData()
        }
    }

    override suspend fun updateLibrary(library: LibraryEntity) {
        val removed = libraries.remove(get(library.name))
        if (removed) {
            libraries += library
            updateLibrariesLiveData()
        }
    }

    fun get(libraryName: String): LibraryEntity? = libraries.find {
        it.name.contentEquals(libraryName, ignoreCase = true)
    }

    override suspend fun deleteLibrary(libraryName: String, libraryUrl: String) {
        val removed = libraries.removeIf {
            it.name == libraryName && it.url == libraryUrl
        }
        if (removed) {
            updateLibrariesLiveData()
        }
    }

    private fun updateLibrariesLiveData() {
        librariesLiveData.value = libraries
    }

}