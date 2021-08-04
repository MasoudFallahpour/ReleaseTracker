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
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LibrariesViewModel
@Inject constructor(
    private val libraryRepository: LibraryRepository,
    private val storage: Storage,
    private val exceptionParser: ExceptionParser
) : ViewModel() {

    private data class Params(
        val sortOrder: SortOrder,
        val searchTerm: String
    )

    private val sortOrderLiveData = MutableLiveData(storage.getSortOrder())
    private val searchQueryLiveData = MutableLiveData("")
    private val getLibrariesTriggerLiveData = MediatorLiveData<Params>().apply {
        addSource(sortOrderLiveData) { sortOrder: SortOrder ->
            value = Params(sortOrder, searchQueryLiveData.value!!)
        }
        addSource(searchQueryLiveData) { searchQuery: String ->
            value = Params(sortOrderLiveData.value!!, searchQuery)
        }
    }

    val librariesListState: LiveData<LibrariesListState> =
        getLibrariesTriggerLiveData.distinctUntilChanged()
            .switchMap { params: Params ->
                libraryRepository.getLibrariesAsFlow()
                    .asLiveData()
                    .map { libraries: List<Library> ->
                        libraries.filter {
                            it.name.contains(params.searchTerm, ignoreCase = true)
                        }
                    }
                    .map { libraries: List<Library> ->
                        when (params.sortOrder) {
                            SortOrder.A_TO_Z -> libraries.sortedBy { it.name }
                            SortOrder.Z_TO_A -> libraries.sortedByDescending { it.name }
                            SortOrder.PINNED_FIRST -> libraries.sortedByDescending { it.pinned }
                        }
                    }
            }.map { libraries: List<Library> ->
                LibrariesListState.LibrariesLoaded(libraries)
            }

    var libraryToDelete: Library? = null
    var searchQuery = ""
    var sortOrder by mutableStateOf(storage.getSortOrder())

    private val _deleteLiveData = SingleLiveData<LibraryDeleteState>()
    val deleteState: LiveData<LibraryDeleteState> = _deleteLiveData

    val lastUpdateCheckState: LiveData<String> =
        libraryRepository.getLastUpdateCheck()
            .asLiveData()

    fun getLibraries(sortOrder: SortOrder, searchQuery: String) {
        storage.setSortOrder(sortOrder)
        sortOrderLiveData.value = sortOrder
        searchQueryLiveData.value = searchQuery
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

            _deleteLiveData.value =
                try {
                    libraryRepository.deleteLibrary(library)
                    LibraryDeleteState.Deleted
                } catch (t: Throwable) {
                    val message = exceptionParser.getMessage(t)
                    LibraryDeleteState.Error(message)
                }

        }

    }

}