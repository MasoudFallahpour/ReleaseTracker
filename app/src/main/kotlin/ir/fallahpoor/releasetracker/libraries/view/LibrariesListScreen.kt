package ir.fallahpoor.releasetracker.libraries.view

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ir.fallahpoor.releasetracker.R
import ir.fallahpoor.releasetracker.common.SPACE_NORMAL
import ir.fallahpoor.releasetracker.common.composables.DefaultSnackbar
import ir.fallahpoor.releasetracker.common.composables.Screen
import ir.fallahpoor.releasetracker.common.managers.NightModeManager
import ir.fallahpoor.releasetracker.data.entity.Library
import ir.fallahpoor.releasetracker.data.utils.NightMode
import ir.fallahpoor.releasetracker.data.utils.SortOrder
import ir.fallahpoor.releasetracker.libraries.view.composables.Toolbar
import ir.fallahpoor.releasetracker.libraries.view.composables.ToolbarMode
import ir.fallahpoor.releasetracker.libraries.view.composables.dialogs.DeleteLibraryDialog
import ir.fallahpoor.releasetracker.libraries.view.states.LibrariesListState
import ir.fallahpoor.releasetracker.libraries.view.states.LibraryDeleteState
import ir.fallahpoor.releasetracker.libraries.viewmodel.LibrariesViewModel
import ir.fallahpoor.releasetracker.theme.ReleaseTrackerTheme

@ExperimentalAnimationApi
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LibrariesListScreen(
    librariesViewModel: LibrariesViewModel,
    nightModeManager: NightModeManager,
    onLibraryClick: (Library) -> Unit,
    onAddLibraryClick: () -> Unit
) {

    val isNightModeOn: Boolean by nightModeManager.isNightModeOnLiveData.observeAsState(
        nightModeManager.isNightModeOn
    )
    val currentNightMode: NightMode by nightModeManager.nightModeLiveData.observeAsState(
        nightModeManager.currentNightMode
    )
    val librariesListState: LibrariesListState by librariesViewModel.librariesListState.observeAsState(
        LibrariesListState.Loading
    )
    val libraryDeleteState: LibraryDeleteState by librariesViewModel.deleteState.observeAsState(
        LibraryDeleteState.Fresh
    )
    val lastUpdateCheck: String by librariesViewModel.lastUpdateCheckState.observeAsState("N/A")
    val getLibraries = {
        librariesViewModel.getLibraries(
            sortOrder = librariesViewModel.sortOrder,
            searchQuery = librariesViewModel.searchQuery
        )
    }

    getLibraries()

    Screen(
        isDarkTheme = isNightModeOn,
        scaffoldState = scaffoldState,
        topBar = {
            var toolbarMode by rememberSaveable { mutableStateOf(ToolbarMode.Normal) }
            Toolbar(
                toolbarMode = toolbarMode,
                onToolbarModeChange = {
                    toolbarMode = it
                    if (it == ToolbarMode.Normal) {
                        librariesViewModel.searchQuery = ""
                        getLibraries()
                    }
                },
                currentSortOrder = librariesViewModel.sortOrder,
                onSortOrderChange = { sortOrder: SortOrder ->
                    librariesViewModel.sortOrder = sortOrder
                    getLibraries()
                },
                isNightModeSupported = nightModeManager.isNightModeSupported,
                currentNightMode = currentNightMode,
                onNightModeChange = nightModeManager::setNightMode,
                onSearchQueryChange = { query: String ->
                    librariesViewModel.searchQuery = query
                    getLibraries()
                },
                onSearchQuerySubmit = { query: String ->
                    librariesViewModel.searchQuery = query
                    getLibraries()
                }
            )
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

@ExperimentalAnimationApi
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
            is LibrariesListState.Loading -> {
                ProgressIndicator()
            }
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
private fun ProgressIndicator() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
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

@ExperimentalAnimationApi
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
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(
                    items = libraries,
                    key = { library: Library -> library.name }
                ) { library: Library ->
                    LibraryItem(
                        library = library,
                        onLibraryClick = { onLibraryClick(library) },
                        onLibraryLongClick = { onLibraryLongClick(library) },
                        onPinLibraryClick = { pin: Boolean ->
                            onPinLibraryClick(library, pin)
                        }
                    )
                    Divider()
                }
            }
        }
        AddLibraryButton(clickListener = onAddLibraryClick)
        Snackbar(libraryDeleteState, scaffoldState)
    }
}

@Composable
private fun Snackbar(libraryDeleteState: LibraryDeleteState, scaffoldState: ScaffoldState) {

    when (libraryDeleteState) {
        is LibraryDeleteState.Error -> {
            LaunchedEffect(scaffoldState.snackbarHostState) {
                scaffoldState.snackbarHostState.showSnackbar(message = libraryDeleteState.message)
            }
        }
        is LibraryDeleteState.Deleted -> {
            val message = stringResource(R.string.library_deleted)
            LaunchedEffect(scaffoldState.snackbarHostState) {
                scaffoldState.snackbarHostState.showSnackbar(message = message)
            }
        }
    }

    DefaultSnackbar(snackbarHostState = scaffoldState.snackbarHostState)

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
    onLibraryClick: () -> Unit,
    onLibraryLongClick: () -> Unit,
    onPinLibraryClick: (Boolean) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .combinedClickable(
                onClick = onLibraryClick,
                onLongClick = onLibraryLongClick
            )
            .padding(
                end = SPACE_NORMAL.dp,
                top = SPACE_NORMAL.dp,
                bottom = SPACE_NORMAL.dp
            )
    ) {
        PinToggleButton(
            isPinned = library.isPinned(),
            onCheckedChange = { isPinned: Boolean ->
                onPinLibraryClick(isPinned)
            }
        )
        Column(modifier = Modifier.weight(1f)) {
            LibraryNameText(libraryName = library.name)
            LibraryUrlText(libraryUrl = library.url)
        }
        Text(text = library.version)
    }
}

@Composable
private fun PinToggleButton(isPinned: Boolean, onCheckedChange: (Boolean) -> Unit) {
    IconToggleButton(
        checked = isPinned,
        onCheckedChange = onCheckedChange
    ) {
        val pinImage = if (isPinned) {
            painterResource(R.drawable.ic_pin_filled)
        } else {
            painterResource(R.drawable.ic_pin_outline)
        }
        Icon(
            painter = pinImage,
            tint = MaterialTheme.colors.secondary,
            contentDescription = stringResource(R.string.pin_unpin_library)
        )
    }
}

@Composable
private fun LibraryNameText(libraryName: String) {
    EllipsisText(
        text = libraryName,
        style = MaterialTheme.typography.body1
    )
}

@Composable
private fun LibraryUrlText(libraryUrl: String) {
    EllipsisText(
        text = libraryUrl,
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

@ExperimentalAnimationApi
@Composable
private fun AddLibraryButton(clickListener: () -> Unit) {
    val tag = stringResource(R.string.test_tag_libraries_list_add_library_button)
    FloatingActionButton(
        onClick = clickListener,
        modifier = Modifier
            .padding(SPACE_NORMAL.dp)
            .semantics { testTag = tag },
    ) {
        Icon(
            imageVector = Icons.Filled.Add,
            contentDescription = stringResource(R.string.add_library)
        )
    }
}

@ExperimentalAnimationApi
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