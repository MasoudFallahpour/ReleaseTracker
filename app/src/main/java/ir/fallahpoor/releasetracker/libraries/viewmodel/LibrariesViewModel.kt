package ir.fallahpoor.releasetracker.libraries.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import ir.fallahpoor.releasetracker.common.SingleLiveData
import ir.fallahpoor.releasetracker.common.ViewState
import ir.fallahpoor.releasetracker.data.entity.Library
import ir.fallahpoor.releasetracker.data.repository.LibraryRepository
import ir.fallahpoor.releasetracker.data.utils.ExceptionParser
import ir.fallahpoor.releasetracker.data.utils.LocalStorage
import kotlinx.coroutines.flow.map
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

    private var order: Order = getDefaultOrder()
    private var searchTerm = ""
    var isActionModeEnabled = false
    var numSelectedItems = 0

    private val _pinViewState = MutableLiveData<ViewState<Unit>>()
    val pinViewState: LiveData<ViewState<Unit>> = _pinViewState

    private val _deleteLiveData = SingleLiveData<ViewState<Unit>>()
    val deleteViewState: LiveData<ViewState<Unit>> = _deleteLiveData

    val lastUpdateCheckViewState: LiveData<ViewState<String>> =
        libraryRepository.getLastUpdateCheck()
            .map {
                ViewState.success(it)
            }
            .asLiveData()

    private val triggerLiveData = MutableLiveData<Unit>()
    val librariesViewState: LiveData<ViewState<List<Library>>> =
        triggerLiveData.switchMap {
            val order = getOrder()
            localStorage.setOrder(order.name)
            libraryRepository.getLibraries(order, searchTerm)
                .map { libraries: List<Library> ->
                    ViewState.success(libraries)
                }
                .asLiveData()
        }


    private fun getOrder() = when (order) {
        Order.A_TO_Z -> LibraryRepository.Order.A_TO_Z
        Order.Z_TO_A -> LibraryRepository.Order.Z_TO_A
        Order.PINNED_FIRST -> LibraryRepository.Order.PINNED_FIRST
    }

    fun getLibraries(order: Order = getDefaultOrder(), searchTerm: String = "") {
        this.order = order
        this.searchTerm = searchTerm
        triggerLiveData.value = Unit
    }

    private fun getDefaultOrder() =
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