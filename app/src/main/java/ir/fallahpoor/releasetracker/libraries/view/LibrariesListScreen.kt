package ir.fallahpoor.releasetracker.libraries.view

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ir.fallahpoor.releasetracker.R
import ir.fallahpoor.releasetracker.common.NightModeManager
import ir.fallahpoor.releasetracker.common.SPACE_NORMAL
import ir.fallahpoor.releasetracker.common.composables.DefaultSnackbar
import ir.fallahpoor.releasetracker.common.composables.Toolbar
import ir.fallahpoor.releasetracker.common.composables.ToolbarMode
import ir.fallahpoor.releasetracker.data.entity.Library
import ir.fallahpoor.releasetracker.data.utils.SortOrder
import ir.fallahpoor.releasetracker.libraries.view.dialogs.DeleteLibraryDialog
import ir.fallahpoor.releasetracker.libraries.view.states.LibrariesListState
import ir.fallahpoor.releasetracker.libraries.view.states.LibraryDeleteState
import ir.fallahpoor.releasetracker.libraries.viewmodel.LibrariesViewModel
import ir.fallahpoor.releasetracker.theme.ReleaseTrackerTheme
import kotlinx.coroutines.launch

@ExperimentalAnimationApi
@ExperimentalFoundationApi
@Composable
fun LibrariesListScreen(
    librariesViewModel: LibrariesViewModel,
    nightModeManager: NightModeManager,
    currentSortOrder: SortOrder,
    onLibraryClick: (Library) -> Unit,
    onAddLibraryClick: () -> Unit
) {

    val librariesListState: LibrariesListState by librariesViewModel.librariesListState.observeAsState(
        LibrariesListState.Loading
    )
    val libraryDeleteState: LibraryDeleteState by librariesViewModel.deleteState.observeAsState(
        LibraryDeleteState.Fresh
    )
    val isNightModeOn: Boolean by nightModeManager.isNightModeOn.observeAsState(
        nightModeManager.isNightModeOn()
    )
    val lastUpdateCheck: String by librariesViewModel.lastUpdateCheckState.observeAsState("N/A")
    val scaffoldState = rememberScaffoldState()

    librariesViewModel.getLibraries()

    ReleaseTrackerTheme(
        darkTheme = isNightModeOn
    ) {
        Scaffold(
            topBar = {
                var toolbarMode by rememberSaveable { mutableStateOf(ToolbarMode.Normal) }
                Toolbar(
                    toolbarMode = toolbarMode,
                    onToolbarModeChange = {
                        toolbarMode = it
                        if (it == ToolbarMode.Normal) {
                            librariesViewModel.getLibraries(searchTerm = "")
                        }
                    },
                    currentSortOrder = currentSortOrder,
                    onSortOrderChange = { sortOrder: SortOrder ->
                        librariesViewModel.getLibraries(sortOrder)
                    },
                    isNightModeSupported = nightModeManager.isNightModeSupported,
                    currentNightMode = nightModeManager.getNightMode(),
                    onNightModeChange = nightModeManager::setNightMode,
                    onSearchQueryChange = { query: String ->
                        librariesViewModel.getLibraries(searchTerm = query)
                    },
                    onSearchQuerySubmit = { query: String ->
                        librariesViewModel.getLibraries(searchTerm = query)
                    },
                    onSearchQueryClear = {
                        librariesViewModel.getLibraries(searchTerm = "")
                    }
                )
            },
            scaffoldState = scaffoldState,
            snackbarHost = {
                scaffoldState.snackbarHostState
            }
        ) {
            var showDeleteLibraryDialog by rememberSaveable { mutableStateOf(false) }
            LibrariesListContent(
                librariesListState = librariesListState,
                libraryDeleteState = libraryDeleteState,
                scaffoldState = scaffoldState,
                lastUpdateCheck = lastUpdateCheck,
                onLibraryClick = onLibraryClick,
                onLibraryLongClick = { library: Library ->
                    librariesViewModel.libraryToDelete = library
                    showDeleteLibraryDialog = true
                },
                onPinLibraryClick = { library: Library, pinned: Boolean ->
                    librariesViewModel.pinLibrary(library, pinned)
                },
                onAddLibraryClick = onAddLibraryClick,
            )
            if (showDeleteLibraryDialog) {
                DeleteLibraryDialog(
                    libraryName = librariesViewModel.libraryToDelete?.name ?: "",
                    onDeleteClicked = {
                        showDeleteLibraryDialog = false
                        librariesViewModel.libraryToDelete?.let {
                            librariesViewModel.deleteLibrary(it)
                        }
                    },
                    onDismiss = {
                        showDeleteLibraryDialog = false
                    }
                )
            }
        }
    }

}

@ExperimentalFoundationApi
@Composable
private fun LibrariesListContent(
    librariesListState: LibrariesListState,
    libraryDeleteState: LibraryDeleteState,
    scaffoldState: ScaffoldState,
    lastUpdateCheck: String,
    onLibraryClick: (Library) -> Unit,
    onLibraryLongClick: (Library) -> Unit,
    onPinLibraryClick: (Library, Boolean) -> Unit,
    onAddLibraryClick: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LastUpdateCheckText(lastUpdateCheck)
        when (librariesListState) {
            is LibrariesListState.Loading -> CircularProgressIndicator()
            is LibrariesListState.LibrariesLoaded -> {
                val libraries: List<Library> = librariesListState.libraries
                LibrariesList(
                    libraries = libraries,
                    scaffoldState = scaffoldState,
                    libraryDeleteState = libraryDeleteState,
                    onLibraryClick = onLibraryClick,
                    onLibraryLongClick = onLibraryLongClick,
                    onPinLibraryClick = onPinLibraryClick,
                    onAddLibraryClick = onAddLibraryClick
                )
            }
            LibrariesListState.Fresh -> {
            }
        }
    }
}

@Composable
private fun LastUpdateCheckText(lastUpdateCheck: String) {
    Text(
        text = stringResource(R.string.last_check_for_updates, lastUpdateCheck),
        modifier = Modifier
            .fillMaxWidth()
            .padding(SPACE_NORMAL.dp)
    )
}

@ExperimentalFoundationApi
@Composable
private fun LibrariesList(
    libraries: List<Library>,
    scaffoldState: ScaffoldState,
    libraryDeleteState: LibraryDeleteState,
    onLibraryClick: (Library) -> Unit,
    onLibraryLongClick: (Library) -> Unit,
    onPinLibraryClick: (Library, Boolean) -> Unit,
    onAddLibraryClick: () -> Unit
) {
    Box(
        contentAlignment = Alignment.BottomStart,
        modifier = Modifier.fillMaxSize()
    ) {
        if (libraries.isEmpty()) {
            NoLibrariesText()
        } else {
            if (libraryDeleteState is LibraryDeleteState.InProgress) {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth()
                )
            }
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                itemsIndexed(libraries) { index: Int, library: Library ->
                    LibraryItem(
                        library = library,
                        onLibraryClick = onLibraryClick,
                        onLibraryLongClick = onLibraryLongClick,
                        onPinLibraryClick = onPinLibraryClick
                    )
                    if (index != libraries.lastIndex) {
                        Divider()
                    }
                }
            }
        }
        AddLibraryButton(
            clickListener = onAddLibraryClick
        )
        Snackbar(libraryDeleteState, scaffoldState)
    }
}

@Composable
private fun Snackbar(libraryDeleteState: LibraryDeleteState, scaffoldState: ScaffoldState) {

    val coroutineScope = rememberCoroutineScope()

    when (libraryDeleteState) {
        is LibraryDeleteState.Error -> {
            coroutineScope.launch {
                scaffoldState.snackbarHostState.showSnackbar(
                    message = libraryDeleteState.message
                )
            }
        }
        is LibraryDeleteState.Deleted -> {
            val message = stringResource(R.string.library_deleted)
            coroutineScope.launch {
                scaffoldState.snackbarHostState.showSnackbar(
                    message = message
                )
            }
        }
    }

    DefaultSnackbar(
        snackbarHostState = scaffoldState.snackbarHostState
    )

}

@Composable
private fun NoLibrariesText() {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Text(
            text = stringResource(R.string.no_libraries),
            modifier = Modifier.padding(SPACE_NORMAL.dp)
        )
    }
}

@ExperimentalFoundationApi
@Composable
private fun LibraryItem(
    library: Library,
    onLibraryClick: (Library) -> Unit,
    onLibraryLongClick: (Library) -> Unit,
    onPinLibraryClick: (Library, Boolean) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .combinedClickable(
                onClick = {
                    onLibraryClick(library)
                },
                onLongClick = {
                    onLibraryLongClick(library)
                }
            )
            .padding(
                end = SPACE_NORMAL.dp,
                top = SPACE_NORMAL.dp,
                bottom = SPACE_NORMAL.dp
            )
    ) {
        PinToggleButton(
            library = library,
            onPinCheckedChange = onPinLibraryClick
        )
        Column(
            modifier = Modifier.weight(1f)
        ) {
            LibraryNameText(library)
            LibraryUrlText(library)
        }
        Text(
            text = library.version
        )
    }
}

@Composable
private fun PinToggleButton(library: Library, onPinCheckedChange: (Library, Boolean) -> Unit) {
    IconToggleButton(
        checked = library.isPinned(),
        onCheckedChange = {
            onPinCheckedChange(library, it)
        }
    ) {
        val pinImage = if (library.isPinned()) {
            Icons.Filled.PushPin
        } else {
            Icons.Outlined.PushPin
        }
        Icon(
            imageVector = pinImage,
            tint = MaterialTheme.colors.secondary,
            contentDescription = stringResource(R.string.pin_library)
        )
    }
}

@Composable
private fun LibraryNameText(library: Library) {
    EllipsisText(
        text = library.name,
        style = MaterialTheme.typography.body1
    )
}

@Composable
private fun LibraryUrlText(library: Library) {
    EllipsisText(
        text = library.url,
        style = MaterialTheme.typography.body2,
        modifier = Modifier.padding(end = SPACE_NORMAL.dp)
    )
}

@Composable
private fun EllipsisText(text: String, style: TextStyle, modifier: Modifier = Modifier) {
    Text(
        text = text,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        style = style,
        modifier = modifier
    )
}

@Composable
private fun AddLibraryButton(clickListener: () -> Unit) {
    FloatingActionButton(
        onClick = clickListener,
        modifier = Modifier.padding(SPACE_NORMAL.dp)
    ) {
        Icon(
            imageVector = Icons.Filled.Add,
            contentDescription = stringResource(R.string.add_library)
        )
    }
}

@ExperimentalFoundationApi
@Preview
@Composable
private fun LibrariesListContentPreview() {
    ReleaseTrackerTheme {
        Surface {
            LibrariesListContent(
                librariesListState = LibrariesListState.Loading,
                libraryDeleteState = LibraryDeleteState.Fresh,
                scaffoldState = rememberScaffoldState(),
                lastUpdateCheck = "N/A",
                onLibraryClick = {},
                onLibraryLongClick = {},
                onPinLibraryClick = { _, _ -> },
                onAddLibraryClick = {}
            )
        }
    }
}