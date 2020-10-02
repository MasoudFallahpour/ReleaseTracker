package ir.fallahpoor.releasetracker.addlibrary.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.viewModelScope
import ir.fallahpoor.releasetracker.common.BaseViewModel
import ir.fallahpoor.releasetracker.common.ViewState
import ir.fallahpoor.releasetracker.data.entity.Library
import ir.fallahpoor.releasetracker.data.repository.LibraryRepository
import ir.fallahpoor.releasetracker.data.utils.ExceptionParser
import kotlinx.coroutines.launch

class AddLibraryViewModel
@ViewModelInject constructor(
    private val libraryRepository: LibraryRepository,
    private val exceptionParser: ExceptionParser
) : BaseViewModel<Unit>() {

    fun addLibrary(libraryName: String, libraryUrl: String) {

        viewModelScope.launch {

            setViewState(ViewState.loading())

            try {
                val library: Library? = libraryRepository.getLibrary(libraryName)
                val libraryAlreadyExists = library != null
                if (libraryAlreadyExists) {
                    setViewState(ViewState.error("Library already exists"))
                } else {
                    val libraryVersion: String =
                        libraryRepository.getLibraryVersion(libraryName, libraryUrl)
                    libraryRepository.addLibrary(libraryName, libraryUrl, libraryVersion)
                    setViewState(ViewState.success(Unit))
                }
            } catch (t: Throwable) {
                val message = exceptionParser.getMessage(t)
                setViewState(ViewState.error(message))
            }

        }

    }

}