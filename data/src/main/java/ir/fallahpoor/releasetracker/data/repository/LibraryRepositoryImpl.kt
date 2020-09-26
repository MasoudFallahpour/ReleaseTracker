package ir.fallahpoor.releasetracker.data.repository

import ir.fallahpoor.releasetracker.data.database.LibraryDao
import ir.fallahpoor.releasetracker.data.entity.Library
import javax.inject.Inject

class LibraryRepositoryImpl
@Inject constructor(
    private val libraryDao: LibraryDao
) : LibraryRepository {

    override suspend fun addLibrary(libraryName: String, libraryUrl: String) {
        libraryDao.insert(Library(libraryName, libraryUrl))
    }

}