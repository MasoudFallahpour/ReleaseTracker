package ir.fallahpoor.releasetracker.libraries.viewmodel

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
        storage.setSortOrder(sortOrder)
        libraryRepository.getLibraries(sortOrder, searchTerm)
            .map {
                LibrariesListState.LibrariesLoaded(it)
            }
            .asLiveData()
    }

    private var sortOrder: SortOrder = storage.getSortOrder()
    private var searchTerm = ""

    private val _deleteLiveData = SingleLiveData<LibraryDeleteState>()
    val deleteState: LiveData<LibraryDeleteState> = _deleteLiveData

    val lastUpdateCheckState: LiveData<String> =
        libraryRepository.getLastUpdateCheck()
            .asLiveData()

    fun getLibraries(
        sortOrder: SortOrder = storage.getSortOrder(),
        searchTerm: String = this.searchTerm
    ) {
        this.sortOrder = sortOrder
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