package ir.fallahpoor.releasetracker.libraries.viewmodel

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.fallahpoor.releasetracker.common.SingleLiveData
import ir.fallahpoor.releasetracker.data.entity.Library
import ir.fallahpoor.releasetracker.data.repository.LibraryRepository
import ir.fallahpoor.releasetracker.data.utils.ExceptionParser
import ir.fallahpoor.releasetracker.data.utils.LocalStorage
import ir.fallahpoor.releasetracker.libraries.view.states.LibrariesListState
import ir.fallahpoor.releasetracker.libraries.view.states.LibraryDeleteState
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LibrariesViewModel
@Inject constructor(
    private val libraryRepository: LibraryRepository,
    private val localStorage: LocalStorage,
    private val exceptionParser: ExceptionParser
) : ViewModel() {

    enum class SortOrder {
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

    private var sortOrder: SortOrder = getDefaultSortOrder()
    private var searchTerm = ""

    private val _deleteLiveData = SingleLiveData<LibraryDeleteState>()
    val deleteState: LiveData<LibraryDeleteState> = _deleteLiveData

    val lastUpdateCheckState: LiveData<String> =
        libraryRepository.getLastUpdateCheck()
            .map { it }
            .asLiveData()

    private fun getOrder() = when (sortOrder) {
        SortOrder.A_TO_Z -> LibraryRepository.SortOrder.A_TO_Z
        SortOrder.Z_TO_A -> LibraryRepository.SortOrder.Z_TO_A
        SortOrder.PINNED_FIRST -> LibraryRepository.SortOrder.PINNED_FIRST
    }

    fun getLibraries(
        sortOrder: SortOrder = getDefaultSortOrder(),
        searchTerm: String = this.searchTerm
    ) {
        this.sortOrder = sortOrder
        this.searchTerm = searchTerm
        triggerLiveData.value = Unit
    }

    private fun getDefaultSortOrder() =
        SortOrder.valueOf(localStorage.getOrder() ?: SortOrder.A_TO_Z.name)

    fun pinLibrary(library: Library, pin: Boolean) {

        viewModelScope.launch {

            try {
                libraryRepository.pinLibrary(library, pin)
            } catch (t: Throwable) {
            }

        }

    }

    fun deleteLibrary(library: Library) {

        _deleteLiveData.value = LibraryDeleteState.InProgress

        try {
            viewModelScope.launch {
                libraryRepository.deleteLibrary(library)
                _deleteLiveData.value = LibraryDeleteState.Deleted
            }
        } catch (t: Throwable) {
            val message = exceptionParser.getMessage(t)
            _deleteLiveData.value = LibraryDeleteState.Error(message)
        }

    }

}