package ir.fallahpoor.releasetracker.libraries.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.hilt.navigation.compose.hiltViewModel
import ir.fallahpoor.releasetracker.data.entity.Library
import ir.fallahpoor.releasetracker.data.utils.NightMode
import ir.fallahpoor.releasetracker.data.utils.SortOrder
import ir.fallahpoor.releasetracker.libraries.Event
import ir.fallahpoor.releasetracker.libraries.LibrariesListScreenUiState
import ir.fallahpoor.releasetracker.libraries.LibrariesViewModel
import ir.fallahpoor.releasetracker.theme.ReleaseTrackerTheme

object LibrariesListScreenTags {
    const val SCREEN = "librariesListScreen"
    const val TOOLBAR = "librariesListScreenToolbar"
    const val CONTENT = "librariesListScreenContent"
}

@Composable
fun LibrariesListScreen(
    librariesViewModel: LibrariesViewModel = hiltViewModel(),
    isNightModeSupported: Boolean,
    currentNightMode: NightMode,
    onNightModeChange: (NightMode) -> Unit,
    onLibraryClick: (Library) -> Unit,
    onAddLibraryClick: () -> Unit,
    scaffoldState: ScaffoldState = rememberScaffoldState()
) {
    val uiState: LibrariesListScreenUiState by librariesViewModel.uiState.collectAsState()
    val lastUpdateCheck: String by librariesViewModel.lastUpdateCheck.collectAsState()
    val isSystemInDarkTheme = isSystemInDarkTheme()
    val isNightModeOn by derivedStateOf {
        when (currentNightMode) {
            NightMode.OFF -> false
            NightMode.ON -> true
            NightMode.AUTO -> isSystemInDarkTheme
        }
    }

    ReleaseTrackerTheme(darkTheme = isNightModeOn) {
        Scaffold(
            modifier = Modifier.testTag(LibrariesListScreenTags.SCREEN),
            scaffoldState = scaffoldState,
            snackbarHost = { scaffoldState.snackbarHostState },
            topBar = {
                Toolbar(
                    modifier = Modifier.testTag(LibrariesListScreenTags.TOOLBAR),
                    currentSortOrder = uiState.sortOrder,
                    onSortOrderChange = { sortOrder: SortOrder ->
                        librariesViewModel.handleEvent(Event.ChangeSortOrder(sortOrder))
                    },
                    isNightModeSupported = isNightModeSupported,
                    currentNightMode = currentNightMode,
                    onNightModeChange = onNightModeChange,
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
                modifier = Modifier
                    .fillMaxSize()
                    .testTag(LibrariesListScreenTags.CONTENT),
                librariesListState = uiState.librariesListState,
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