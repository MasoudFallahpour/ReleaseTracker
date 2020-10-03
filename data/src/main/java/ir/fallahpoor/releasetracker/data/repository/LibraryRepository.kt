package ir.fallahpoor.releasetracker.data.repository

import androidx.lifecycle.LiveData
import ir.fallahpoor.releasetracker.data.entity.Library

interface LibraryRepository {

    suspend fun addLibrary(libraryName: String, libraryUrl: String, libraryVersion: String)

    suspend fun updateLibrary(library: Library)

    suspend fun getLibrary(libraryName: String): Library?

    fun getLibrariesByLiveData(): LiveData<List<Library>>

    suspend fun getLibraries(): List<Library>

    suspend fun getLibraryVersion(libraryName: String, libraryUrl: String): String

    suspend fun setFavourite(library: Library, isFavourite: Boolean)

}