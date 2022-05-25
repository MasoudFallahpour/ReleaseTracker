@file:OptIn(ExperimentalCoroutinesApi::class)

package ir.fallahpoor.releasetracker.libraries

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.fallahpoor.releasetracker.data.entity.Library
import ir.fallahpoor.releasetracker.data.repository.LibraryRepository
import ir.fallahpoor.releasetracker.data.utils.SortOrder
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.*
import javax.inject.Inject

@HiltViewModel
class LibrariesViewModel
@Inject constructor(
    private val libraryRepository: LibraryRepository
) : ViewModel() {

    private data class Params(
        val sortOrder: SortOrder,
        val searchQuery: String
    )

    private val searchQueryFlow = MutableStateFlow("")
    private val getLibrariesTriggerFlow: Flow<Params> = combine(
        libraryRepository.getSortOrderAsFlow(),
        searchQueryFlow
    ) { sortOrder: SortOrder, searchQuery: String ->
        Params(sortOrder, searchQuery)
    }

    val uiState: StateFlow<LibrariesListScreenUiState> =
        getLibrariesTriggerFlow.distinctUntilChanged()
            .flatMapLatest { params -> getLibrariesAsFlow(params) }
            .map { libraries ->
                LibrariesListScreenUiState(
                    sortOrder = libraryRepository.getSortOrder(),
                    searchQuery = searchQueryFlow.value,
                    librariesListState = LibrariesListState.LibrariesLoaded(libraries)
                )
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.Eagerly,
                initialValue = LibrariesListScreenUiState(sortOrder = libraryRepository.getSortOrder())
            )

    val lastUpdateCheck: StateFlow<String> = libraryRepository.getLastUpdateCheck()
        .stateIn(scope = viewModelScope, started = SharingStarted.Eagerly, initialValue = "N/A")

    fun handleEvent(event: Event) {
        when (event) {
            is Event.PinLibrary -> pinLibrary(event.library, event.pin)
            is Event.DeleteLibrary -> deleteLibrary(event.library)
            is Event.ChangeSortOrder -> changeSortOrder(event.sortOrder)
            is Event.ChangeSearchQuery -> searchQueryFlow.value = event.searchQuery
        }
    }

    private fun pinLibrary(library: Library, pin: Boolean) {
        viewModelScope.launch {
            try {
                libraryRepository.pinLibrary(library, pin)
            } catch (t: Throwable) {
                Timber.e(t)
            }
        }
    }

    private fun deleteLibrary(library: Library) {
        viewModelScope.launch {
            try {
                libraryRepository.deleteLibrary(library)
            } catch (t: Throwable) {
                Timber.e(t)
            }
        }
    }

    private fun changeSortOrder(sortOrder: SortOrder) {
        viewModelScope.launch {
            try {
                libraryRepository.setSortOrder(sortOrder)
            } catch (t: Throwable) {
                Timber.e(t)
            }
        }
    }

    private fun getLibrariesAsFlow(params: Params): Flow<List<Library>> =
        libraryRepository.getLibrariesAsFlow()
            .map { libraries ->
                libraries.filter {
                    it.name.contains(params.searchQuery, ignoreCase = true)
                }
            }
            .map { libraries ->
                libraries.sort(params.sortOrder)
            }

    private fun List<Library>.sort(sortOrder: SortOrder): List<Library> = when (sortOrder) {
        SortOrder.A_TO_Z -> sortedBy { it.name.lowercase(Locale.getDefault()) }
        SortOrder.Z_TO_A -> sortedByDescending { it.name.lowercase(Locale.getDefault()) }
        SortOrder.PINNED_FIRST -> sortedByDescending { it.pinned }
    }

}