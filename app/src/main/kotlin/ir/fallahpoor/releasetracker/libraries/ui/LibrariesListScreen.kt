package ir.fallahpoor.releasetracker.libraries.ui

import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.hilt.navigation.compose.hiltViewModel
import ir.fallahpoor.releasetracker.common.managers.NightModeManager
import ir.fallahpoor.releasetracker.data.entity.Library
import ir.fallahpoor.releasetracker.data.utils.NightMode
import ir.fallahpoor.releasetracker.data.utils.SortOrder
import ir.fallahpoor.releasetracker.libraries.Event
import ir.fallahpoor.releasetracker.libraries.LibrariesListScreenState
import ir.fallahpoor.releasetracker.libraries.LibrariesViewModel
import ir.fallahpoor.releasetracker.theme.ReleaseTrackerTheme

@Composable
fun LibrariesListScreen(
    librariesViewModel: LibrariesViewModel = hiltViewModel(),
    nightModeManager: NightModeManager,
    onLibraryClick: (Library) -> Unit,
    onAddLibraryClick: () -> Unit,
    scaffoldState: ScaffoldState = rememberScaffoldState()
) {
    val isNightModeOn: Boolean by nightModeManager.isNightModeOnLiveData.observeAsState(
        nightModeManager.isNightModeOn
    )
    val currentNightMode: NightMode by nightModeManager.nightModeLiveData.observeAsState(
        nightModeManager.currentNightMode
    )
    val state: LibrariesListScreenState by librariesViewModel.state.collectAsState()
    val lastUpdateCheck: String by librariesViewModel.lastUpdateCheckState.collectAsState()

    ReleaseTrackerTheme(darkTheme = isNightModeOn) {
        Scaffold(
            scaffoldState = scaffoldState,
            snackbarHost = { scaffoldState.snackbarHostState },
            topBar = {
                Toolbar(
                    currentSortOrder = state.sortOrder,
                    onSortOrderChange = { sortOrder: SortOrder ->
                        librariesViewModel.handleEvent(Event.ChangeSortOrder(sortOrder))
                    },
                    isNightModeSupported = nightModeManager.isNightModeSupported,
                    currentNightMode = currentNightMode,
                    onNightModeChange = nightModeManager::setNightMode,
                    onSearchQueryChange = { query: String ->
                        librariesViewModel.handleEvent(Event.ChangeSearchQuery(query))
                    },
                    onSearchQuerySubmit = { query: String ->
                        librariesViewModel.handleEvent(Event.ChangeSearchQuery(query))
                    }
                )
            }
        ) {
            LibrariesListContent(
                librariesListState = state.librariesListState,
                lastUpdateCheck = lastUpdateCheck,
                onLibraryClick = onLibraryClick,
                onLibraryDismissed = { library: Library ->
                    librariesViewModel.handleEvent(Event.DeleteLibrary(library))
                },
                onPinLibraryClick = { library: Library, pinned: Boolean ->
                    librariesViewModel.handleEvent(Event.PinLibrary(library, pinned))
                },
                onAddLibraryClick = onAddLibraryClick,
            )
        }
    }
}