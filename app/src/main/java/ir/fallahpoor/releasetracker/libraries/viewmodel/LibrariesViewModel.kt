package ir.fallahpoor.releasetracker.libraries.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import ir.fallahpoor.releasetracker.common.ViewState
import ir.fallahpoor.releasetracker.data.entity.Library
import ir.fallahpoor.releasetracker.data.repository.LibraryRepository
import ir.fallahpoor.releasetracker.data.utils.ExceptionParser
import ir.fallahpoor.releasetracker.data.utils.LocalStorage
import kotlinx.coroutines.launch

class LibrariesViewModel
@ViewModelInject constructor(
    private val libraryRepository: LibraryRepository,
    private val localStorage: LocalStorage,
    private val exceptionParser: ExceptionParser
) : ViewModel() {

    enum class SortingOrder {
        A_TO_Z,
        Z_TO_A,
        PINNED_FIRST
    }

    private var sortingOrder = getDefaultSortingOrder()
    private val _favouriteViewState = MutableLiveData<ViewState<Unit>>()
    private val triggerLiveData = MutableLiveData<Unit>()

    val favouriteViewState: LiveData<ViewState<Unit>> = _favouriteViewState
    val librariesViewState: LiveData<ViewState<List<Library>>> =
        Transformations.switchMap(triggerLiveData) {
            val sortingOrder = getSortingOrder()
            localStorage.setSortingOrder(sortingOrder.name)
            Transformations.map(libraryRepository.getLibrariesByLiveData(sortingOrder)) { libraries: List<Library> ->
                ViewState.success(libraries)
            }
        }

    private fun getSortingOrder(): LibraryRepository.SortingOrder =
        when (sortingOrder) {
            SortingOrder.A_TO_Z -> LibraryRepository.SortingOrder.A_TO_Z
            SortingOrder.Z_TO_A -> LibraryRepository.SortingOrder.Z_TO_A
            SortingOrder.PINNED_FIRST -> LibraryRepository.SortingOrder.PINNED_FIRST
        }

    fun getLibraries(sortingOrder: SortingOrder = getDefaultSortingOrder()) {
        this.sortingOrder = sortingOrder
        triggerLiveData.value = Unit
    }

    fun setPinned(library: Library, isPinned: Boolean) {

        viewModelScope.launch {

            _favouriteViewState.value = ViewState.loading()

            try {
                libraryRepository.setPinned(library, isPinned)
                _favouriteViewState.value = ViewState.success(Unit)
            } catch (t: Throwable) {
                val message = exceptionParser.getMessage(t)
                _favouriteViewState.value = ViewState.error(message)
            }

        }

    }

    private fun getDefaultSortingOrder(): SortingOrder =
        SortingOrder.valueOf(localStorage.getSortingOrder() ?: SortingOrder.A_TO_Z.name)

}