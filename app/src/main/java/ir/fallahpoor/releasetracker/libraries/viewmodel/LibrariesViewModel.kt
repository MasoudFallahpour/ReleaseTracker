package ir.fallahpoor.releasetracker.libraries.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import ir.fallahpoor.releasetracker.common.SingleLiveData
import ir.fallahpoor.releasetracker.data.entity.Library
import ir.fallahpoor.releasetracker.data.repository.LibraryRepository
import ir.fallahpoor.releasetracker.data.utils.ExceptionParser
import ir.fallahpoor.releasetracker.data.utils.LocalStorage
import ir.fallahpoor.releasetracker.libraries.view.states.LibrariesListState
import ir.fallahpoor.releasetracker.libraries.view.states.LibraryDeleteState
import ir.fallahpoor.releasetracker.libraries.view.states.LibraryPinState
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

    var libraryToDelete: Library? = null

    private val triggerLiveData = MutableLiveData<Unit>()

    val librariesListState: LiveData<LibrariesListState> = triggerLiveData.switchMap {
        val order = getOrder()
        localStorage.setOrder(order.name)
        libraryRepository.getLibraries(order, searchTerm)
            .map {
                LibrariesListState.LibrariesLoaded(it)
            }
            .asLiveData()
    }

    private var order: Order = getDefaultOrder()
    private var searchTerm = ""

    private val _deleteLiveData = SingleLiveData<LibraryDeleteState>()
    val deleteState: LiveData<LibraryDeleteState> = _deleteLiveData

    private val _pinLiveData = SingleLiveData<LibraryPinState>()
    val pinState: LiveData<LibraryPinState> = _pinLiveData

    val lastUpdateCheckState: LiveData<String> =
        libraryRepository.getLastUpdateCheck()
            .map { it }
            .asLiveData()

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

    fun setPinned(library: Library, pin: Boolean) {

        _pinLiveData.value = LibraryPinState.InProgress

        viewModelScope.launch {

            try {
                libraryRepository.setPinned(library, pin)
            } catch (t: Throwable) {
                val message = exceptionParser.getMessage(t)
                _pinLiveData.value = LibraryPinState.Error(message)
            }

        }

    }

    fun deleteLibrary(libraryName: String) {

        _deleteLiveData.value = LibraryDeleteState.InProgress

        try {
            viewModelScope.launch {
                libraryRepository.deleteLibrary(libraryName)
                _deleteLiveData.value = LibraryDeleteState.Deleted
            }
        } catch (t: Throwable) {
            val message = exceptionParser.getMessage(t)
            _deleteLiveData.value = LibraryDeleteState.Error(message)
        }

    }

}