package ir.fallahpoor.releasetracker.data.repository

import ir.fallahpoor.releasetracker.data.entity.Library

interface LibraryRepository {

    suspend fun addLibrary(libraryName: String, libraryUrl: String)

    suspend fun getLibraries(): List<Library>

}