package ir.fallahpoor.releasetracker.libraries.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ir.fallahpoor.releasetracker.common.ViewState
import ir.fallahpoor.releasetracker.data.entity.Library
import ir.fallahpoor.releasetracker.data.repository.LibraryRepository
import ir.fallahpoor.releasetracker.data.utils.ExceptionParser
import kotlinx.coroutines.launch

class LibrariesViewModel
@ViewModelInject constructor(
    private val libraryRepository: LibraryRepository,
    private val exceptionParser: ExceptionParser
) : ViewModel() {

    private val _librariesViewState = MutableLiveData<ViewState<List<Library>>>()
    private val _favouriteViewState = MutableLiveData<ViewState<Unit>>()
    val librariesViewState: LiveData<ViewState<List<Library>>> = _librariesViewState
    val favouriteViewState: LiveData<ViewState<Unit>> = _favouriteViewState

    fun getLibraries() {

        viewModelScope.launch {

            _librariesViewState.value = ViewState.loading()

            try {
                val libraries: List<Library> = libraryRepository.getLibraries()
                _librariesViewState.value = ViewState.success(libraries)
            } catch (t: Throwable) {
                val message = exceptionParser.getMessage(t)
                _librariesViewState.value = ViewState.error(message)
            }

        }

    }

    fun setFavourite(library: Library, isFavourite: Boolean) {

        viewModelScope.launch {

            _favouriteViewState.value = ViewState.loading()

            try {
                libraryRepository.setFavourite(library, isFavourite)
                _favouriteViewState.value = ViewState.success(Unit)
            } catch (t: Throwable) {
                val message = exceptionParser.getMessage(t)
                _favouriteViewState.value = ViewState.error(message)
            }

        }

    }

}