package ir.fallahpoor.releasetracker.libraries.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.fallahpoor.releasetracker.common.SingleLiveData
import ir.fallahpoor.releasetracker.data.entity.Library
import ir.fallahpoor.releasetracker.data.repository.LibraryRepository
import ir.fallahpoor.releasetracker.data.utils.ExceptionParser
import ir.fallahpoor.releasetracker.data.utils.SortOrder
import ir.fallahpoor.releasetracker.data.utils.storage.Storage
import ir.fallahpoor.releasetracker.libraries.view.states.LibrariesListState
import ir.fallahpoor.releasetracker.libraries.view.states.LibraryDeleteState
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LibrariesViewModel
@Inject constructor(
    private val libraryRepository: LibraryRepository,
    private val storage: Storage,
    private val exceptionParser: ExceptionParser
) : ViewModel() {

    var libraryToDelete: Library? = null

    private val triggerLiveData = MutableLiveData<Unit>()

    val librariesListState: LiveData<LibrariesListState> = triggerLiveData.switchMap {
        storage.setSortOrder(currentSortOrder)
        libraryRepository.getLibraries(currentSortOrder, searchTerm)
            .map {
                LibrariesListState.LibrariesLoaded(it)
            }
            .asLiveData()
    }

    private var searchTerm = ""

    private val _deleteLiveData = SingleLiveData<LibraryDeleteState>()
    val deleteState: LiveData<LibraryDeleteState> = _deleteLiveData

    val lastUpdateCheckState: LiveData<String> =
        libraryRepository.getLastUpdateCheck()
            .asLiveData()

    var currentSortOrder by mutableStateOf(storage.getSortOrder())

    fun getLibraries(
        sortOrder: SortOrder = storage.getSortOrder(),
        searchTerm: String = this.searchTerm
    ) {
        this.currentSortOrder = sortOrder
        this.searchTerm = searchTerm
        triggerLiveData.value = Unit
    }

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

        viewModelScope.launch {

            try {
                libraryRepository.deleteLibrary(library)
                _deleteLiveData.value = LibraryDeleteState.Deleted
            } catch (t: Throwable) {
                val message = exceptionParser.getMessage(t)
                _deleteLiveData.value = LibraryDeleteState.Error(message)
            }

        }

    }

}