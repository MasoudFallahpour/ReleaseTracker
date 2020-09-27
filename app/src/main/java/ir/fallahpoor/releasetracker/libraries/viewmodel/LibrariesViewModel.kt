package ir.fallahpoor.releasetracker.libraries.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.viewModelScope
import ir.fallahpoor.releasetracker.common.BaseViewModel
import ir.fallahpoor.releasetracker.common.ExceptionParser
import ir.fallahpoor.releasetracker.common.ViewState
import ir.fallahpoor.releasetracker.data.entity.Library
import ir.fallahpoor.releasetracker.data.repository.LibraryRepository
import kotlinx.coroutines.launch

class LibrariesViewModel
@ViewModelInject constructor(
    private val libraryRepository: LibraryRepository,
    private val exceptionParser: ExceptionParser
) : BaseViewModel<List<Library>>() {

    fun getLibraries() {

        viewModelScope.launch {

            setViewState(ViewState.loading())

            try {
                val libraries: List<Library> = libraryRepository.getLibraries()
                setViewState(ViewState.success(libraries))
            } catch (t: Throwable) {
                val message = exceptionParser.getMessage(t)
                setViewState(ViewState.error(message))
            }

        }

    }

}