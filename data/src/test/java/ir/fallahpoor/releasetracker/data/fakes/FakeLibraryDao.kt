package ir.fallahpoor.releasetracker.data.fakes

import androidx.lifecycle.MutableLiveData
import androidx.sqlite.db.SupportSQLiteQuery
import ir.fallahpoor.releasetracker.data.database.LibraryDao
import ir.fallahpoor.releasetracker.data.entity.Library
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeLibraryDao : LibraryDao {

    private val _librariesLiveData = MutableLiveData<List<Library>>()
    private val libraries = mutableListOf<Library>()

    override fun getAll(query: SupportSQLiteQuery): Flow<List<Library>> =
        flow {
            libraries
        }

    override suspend fun getAll(): List<Library> = libraries.sortedBy {
        it.name
    }

    override suspend fun insert(library: Library) {
        libraries.add(library)
        _librariesLiveData.value = libraries
    }

    override suspend fun update(library: Library) {
        val removed = libraries.remove(library)
        if (removed) {
            libraries.add(library)
            _librariesLiveData.value = libraries
        }
    }

    override suspend fun delete(libraryName: String) {
        libraries.removeIf {
            it.name == libraryName
        }
        _librariesLiveData.value = libraries
    }

    override suspend fun get(libraryName: String): Library? =
        _librariesLiveData.value?.find {
            it.name == libraryName
        }

}