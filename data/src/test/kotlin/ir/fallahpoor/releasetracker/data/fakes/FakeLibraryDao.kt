package ir.fallahpoor.releasetracker.data.fakes

import androidx.lifecycle.MutableLiveData
import ir.fallahpoor.releasetracker.data.database.LibraryDao
import ir.fallahpoor.releasetracker.data.entity.Library
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeLibraryDao : LibraryDao {

    private val _librariesLiveData = MutableLiveData<List<Library>>()
    private val libraries = mutableListOf<Library>()

    override fun getAllSortedByNameAscending(searchTerm: String): Flow<List<Library>> =
        flow {
            emit(
                libraries
                    .filter {
                        it.name.contains(searchTerm, ignoreCase = true)
                    }.sortedBy {
                        it.name
                    }
            )
        }

    override fun getAllSortedByNameDescending(searchTerm: String): Flow<List<Library>> =
        flow {
            emit(
                libraries
                    .filter {
                        it.name.contains(searchTerm, ignoreCase = true)
                    }.sortedByDescending {
                        it.name
                    }
            )
        }

    override fun getAllSortedByPinnedFirst(searchTerm: String): Flow<List<Library>> =
        flow {
            emit(
                libraries
                    .filter {
                        it.name.contains(searchTerm, ignoreCase = true)
                    }.sortedBy {
                        it.pinned
                    }.sortedBy {
                        it.name
                    }
            )
        }

    override suspend fun getAll(): List<Library> = libraries.sortedBy {
        it.name
    }

    override suspend fun insert(library: Library) {
        libraries.add(library)
        _librariesLiveData.value = libraries
    }

    override suspend fun update(library: Library) {
        val removed = libraries.remove(get(library.name))
        if (removed) {
            libraries.add(library)
            _librariesLiveData.value = libraries
        }
    }

    override suspend fun delete(libraryName: String) {
        val removed = libraries.removeIf {
            it.name == libraryName
        }
        if (removed) {
            _librariesLiveData.value = libraries
        }
    }

    override suspend fun get(libraryName: String): Library? =
        _librariesLiveData.value?.find {
            it.name == libraryName
        }

}