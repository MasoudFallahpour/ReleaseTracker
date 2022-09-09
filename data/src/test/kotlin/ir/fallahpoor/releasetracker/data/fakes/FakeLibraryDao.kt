package ir.fallahpoor.releasetracker.data.fakes

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asFlow
import ir.fallahpoor.releasetracker.data.database.LibraryDao
import ir.fallahpoor.releasetracker.data.database.entity.LibraryEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FakeLibraryDao : LibraryDao {

    private val librariesLiveData = MutableLiveData<List<LibraryEntity>>()
    private val libraries = mutableListOf<LibraryEntity>()

    override fun getAllAsFlow(): Flow<List<LibraryEntity>> = librariesLiveData.asFlow().map {
        libraries.sortedBy { it.name }
    }

    override suspend fun getAll(): List<LibraryEntity> = libraries.sortedBy {
        it.name
    }

    override suspend fun insert(library: LibraryEntity) {
        libraries += library
        updateLibrariesLiveData()
    }

    override suspend fun update(library: LibraryEntity) {
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

    override suspend fun get(libraryName: String): LibraryEntity? = libraries.find {
        it.name.contentEquals(libraryName, ignoreCase = true)
    }

    private fun updateLibrariesLiveData() {
        librariesLiveData.value = libraries
    }

}