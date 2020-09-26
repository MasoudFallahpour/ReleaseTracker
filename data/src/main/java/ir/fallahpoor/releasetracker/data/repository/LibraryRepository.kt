package ir.fallahpoor.releasetracker.data.repository

interface LibraryRepository {

    suspend fun addLibrary(libraryName: String, libraryUrl: String)

}