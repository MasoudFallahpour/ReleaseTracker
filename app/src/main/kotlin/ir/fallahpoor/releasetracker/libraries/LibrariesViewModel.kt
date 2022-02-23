@file:OptIn(ExperimentalCoroutinesApi::class)

package ir.fallahpoor.releasetracker.libraries

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.fallahpoor.releasetracker.data.entity.Library
import ir.fallahpoor.releasetracker.data.repository.LibraryRepository
import ir.fallahpoor.releasetracker.data.utils.SortOrder
import ir.fallahpoor.releasetracker.data.utils.storage.Storage
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class LibrariesViewModel
@Inject constructor(
    private val libraryRepository: LibraryRepository,
    private val storage: Storage
) : ViewModel() {

    private data class Params(
        val sortOrder: SortOrder,
        val searchTerm: String
    )

    private val sortOrderFlow = MutableStateFlow(storage.getSortOrder())
    private val searchQueryFlow = MutableStateFlow("")
    private val triggerFlow: Flow<Params> =
        sortOrderFlow.combine(searchQueryFlow) { sortOrder: SortOrder, searchQuery: String ->
            Params(sortOrder, searchQuery)
        }

    val state: StateFlow<LibrariesListScreenState> =
        triggerFlow.distinctUntilChanged()
            .flatMapLatest { params ->
                libraryRepository.getLibrariesAsFlow()
                    .map { libraries: List<Library> ->
                        libraries.filter {
                            it.name.contains(params.searchTerm, ignoreCase = true)
                        }
                    }
                    .map { libraries: List<Library> ->
                        when (params.sortOrder) {
                            SortOrder.A_TO_Z -> libraries.sortedBy {
                                it.name.lowercase(Locale.getDefault())
                            }
                            SortOrder.Z_TO_A -> libraries.sortedByDescending {
                                it.name.lowercase(Locale.getDefault())
                            }
                            SortOrder.PINNED_FIRST -> libraries.sortedByDescending { it.pinned }
                        }
                    }
            }.map { libraries: List<Library> ->
                LibrariesListScreenState(
                    sortOrder = sortOrderFlow.value,
                    searchQuery = searchQueryFlow.value,
                    librariesListState = LibrariesListState.LibrariesLoaded(libraries)
                )
            }.stateIn(
                viewModelScope,
                SharingStarted.Eagerly,
                LibrariesListScreenState(sortOrder = storage.getSortOrder())
            )

    val lastUpdateCheckState: StateFlow<String> =
        libraryRepository.getLastUpdateCheck()
            .stateIn(viewModelScope, SharingStarted.Eagerly, "N/A")

    fun handleEvent(event: Event) {
        when (event) {
            is Event.PinLibrary -> pinLibrary(event.library, event.pin)
            is Event.DeleteLibrary -> deleteLibrary(event.library)
            is Event.ChangeSortOrder -> changeSortOrder(event.sortOrder)
            is Event.ChangeSearchQuery -> changeSearchQuery(event.searchQuery)
        }
    }

    private fun pinLibrary(library: Library, pin: Boolean) {
        viewModelScope.launch {
            try {
                libraryRepository.pinLibrary(library, pin)
            } catch (_: Throwable) {
            }
        }
    }

    private fun deleteLibrary(library: Library) {
        viewModelScope.launch {
            try {
                libraryRepository.deleteLibrary(library)
            } catch (_: Throwable) {
            }
        }
    }

    private fun changeSortOrder(sortOrder: SortOrder) {
        storage.setSortOrder(sortOrder)
        sortOrderFlow.value = sortOrder
    }

    private fun changeSearchQuery(searchQuery: String) {
        searchQueryFlow.value = searchQuery
    }

}