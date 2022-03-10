package ir.fallahpoor.releasetracker.data.fakes

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asFlow
import ir.fallahpoor.releasetracker.data.database.LibraryDao
import ir.fallahpoor.releasetracker.data.entity.Library
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FakeLibraryDao : LibraryDao {

    private val librariesLiveData = MutableLiveData<List<Library>>()
    private val libraries = mutableListOf<Library>()

    override fun getAllAsFlow(): Flow<List<Library>> = librariesLiveData.asFlow().map {
        libraries.sortedBy { it.name }
    }

    override suspend fun getAll(): List<Library> = libraries.sortedBy {
        it.name
    }

    override suspend fun insert(library: Library) {
        libraries += library
        updateLibrariesLiveData()
    }

    override suspend fun update(library: Library) {
        val removed = libraries.remove(get(library.name))
        if (removed) {
            libraries += library
            updateLibrariesLiveData()
        }
    }

    override suspend fun delete(libraryName: String) {
        val removed = libraries.removeIf {
            it.name == libraryName
        }
        if (removed) {
            updateLibrariesLiveData()
        }
    }

    override suspend fun get(libraryName: String): Library? = libraries.find {
        it.name.contentEquals(libraryName, ignoreCase = true)
    }

    private fun updateLibrariesLiveData() {
        librariesLiveData.value = libraries
    }

}