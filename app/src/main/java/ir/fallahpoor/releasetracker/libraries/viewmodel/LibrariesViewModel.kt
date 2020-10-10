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

    enum class Order {
        A_TO_Z,
        Z_TO_A,
        PINNED_FIRST
    }

    private var order = getDefaultOrder()
    private val _pinViewState = MutableLiveData<ViewState<Unit>>()
    private val _deleteLiveData = MutableLiveData<ViewState<Unit>>()
    private val triggerLiveData = MutableLiveData<Unit>()

    val pinViewState: LiveData<ViewState<Unit>> = _pinViewState
    val librariesViewState: LiveData<ViewState<List<Library>>> =
        Transformations.switchMap(triggerLiveData) {
            val sortingOrder = getSortingOrder()
            localStorage.setOrder(sortingOrder.name)
            Transformations.map(libraryRepository.getLibrariesByLiveData(sortingOrder)) { libraries: List<Library> ->
                ViewState.success(libraries)
            }
        }
    val deleteViewState: LiveData<ViewState<Unit>> = _deleteLiveData

    private fun getSortingOrder(): LibraryRepository.Order =
        when (order) {
            Order.A_TO_Z -> LibraryRepository.Order.A_TO_Z
            Order.Z_TO_A -> LibraryRepository.Order.Z_TO_A
            Order.PINNED_FIRST -> LibraryRepository.Order.PINNED_FIRST
        }

    fun getLibraries(order: Order = getDefaultOrder()) {
        this.order = order
        triggerLiveData.value = Unit
    }

    private fun getDefaultOrder(): Order =
        Order.valueOf(localStorage.getOrder() ?: Order.A_TO_Z.name)

    fun setPinned(library: Library, isPinned: Boolean) {

        _pinViewState.value = ViewState.loading()

        viewModelScope.launch {

            try {
                libraryRepository.setPinned(library, isPinned)
                _pinViewState.value = ViewState.success(Unit)
            } catch (t: Throwable) {
                val message = exceptionParser.getMessage(t)
                _pinViewState.value = ViewState.error(message)
            }

        }

    }

    fun deleteLibraries(libraryNames: List<String>) {

        _deleteLiveData.value = ViewState.loading()

        try {
            viewModelScope.launch {
                libraryRepository.deleteLibraries(libraryNames)
                _deleteLiveData.value = ViewState.success(Unit)
            }
        } catch (t: Throwable) {
            val message = exceptionParser.getMessage(t)
            _deleteLiveData.value = ViewState.error(message)
        }

    }

}