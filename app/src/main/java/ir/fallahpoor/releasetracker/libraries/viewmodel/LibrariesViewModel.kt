package ir.fallahpoor.releasetracker.libraries.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
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

    private val _favouriteViewState = MutableLiveData<ViewState<Unit>>()
    private val triggerLiveData = MutableLiveData<Unit>()

    val favouriteViewState: LiveData<ViewState<Unit>> = _favouriteViewState
    val librariesViewState: LiveData<ViewState<List<Library>>> =
        Transformations.switchMap(triggerLiveData) {
            Transformations.map(libraryRepository.getLibrariesByLiveData()) { libraries: List<Library> ->
                ViewState.success(libraries)
            }
        }

    fun getLibraries() {
        triggerLiveData.value = Unit
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