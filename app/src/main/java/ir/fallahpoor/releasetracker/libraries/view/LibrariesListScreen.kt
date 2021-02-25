package ir.fallahpoor.releasetracker.libraries.view

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.Icons.Default
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.navigate
import ir.fallahpoor.releasetracker.R
import ir.fallahpoor.releasetracker.common.DefaultSnackbar
import ir.fallahpoor.releasetracker.common.NightModeManager
import ir.fallahpoor.releasetracker.common.SPACE_NORMAL
import ir.fallahpoor.releasetracker.common.Screen
import ir.fallahpoor.releasetracker.data.entity.Library
import ir.fallahpoor.releasetracker.data.utils.LocalStorage
import ir.fallahpoor.releasetracker.libraries.view.dialogs.DeleteLibraryDialog
import ir.fallahpoor.releasetracker.libraries.view.dialogs.NightModeDialog
import ir.fallahpoor.releasetracker.libraries.view.dialogs.SortOrder
import ir.fallahpoor.releasetracker.libraries.view.dialogs.SortOrderDialog
import ir.fallahpoor.releasetracker.libraries.view.states.LibrariesListState
import ir.fallahpoor.releasetracker.libraries.view.states.LibraryDeleteState
import ir.fallahpoor.releasetracker.libraries.viewmodel.LibrariesViewModel
import ir.fallahpoor.releasetracker.theme.ReleaseTrackerTheme
import kotlinx.coroutines.launch

@ExperimentalFoundationApi
@Composable
fun LibrariesListScreen(
    librariesViewModel: LibrariesViewModel,
    nightModeManager: NightModeManager,
    localStorage: LocalStorage,
    navController: NavController,
) {

    librariesViewModel.getLibraries()

    val nightMode by nightModeManager.nightMode.observeAsState()
    val isNightModeOn = when (nightMode) {
        NightModeManager.Mode.OFF -> false
        NightModeManager.Mode.ON -> true
        else -> isSystemInDarkTheme()
    }
    val scaffoldState = rememberScaffoldState()

    ReleaseTrackerTheme(
        darkTheme = isNightModeOn
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = stringResource(R.string.app_name)
                        )
                    },
                    actions = {
                        ActionButtons(
                            librariesViewModel = librariesViewModel,
                            nightModeManager = nightModeManager,
                            localStorage = localStorage
                        )
                    }
                )
            },
            scaffoldState = scaffoldState,
            snackbarHost = {
                scaffoldState.snackbarHostState
            }
        ) {
            LibrariesListContent(
                librariesViewModel = librariesViewModel,
                navController = navController,
                scaffoldState = scaffoldState
            )
        }
    }

}

@Composable
private fun ActionButtons(
    librariesViewModel: LibrariesViewModel,
    nightModeManager: NightModeManager,
    localStorage: LocalStorage
) {
    SortOrderButton(localStorage, librariesViewModel)
    SearchButton()
    NightModeButton(nightModeManager)
}

@Composable
private fun SortOrderButton(
    localStorage: LocalStorage,
    librariesViewModel: LibrariesViewModel
) {
    var showSortOrderDialog by remember { mutableStateOf(false) }
    IconButton(
        onClick = {
            showSortOrderDialog = true
        }
    ) {
        Icon(
            imageVector = Icons.Filled.Sort,
            contentDescription = stringResource(R.string.sort)
        )
    }
    if (showSortOrderDialog) {
        SortOrderDialog(
            currentSortOrder = getCurrentSortOrder(localStorage),
            onSortOrderClick = { sortOrder: SortOrder ->
                showSortOrderDialog = false
                librariesViewModel.getLibraries(mapSortOrder(sortOrder))
            },
            onDismiss = {
                showSortOrderDialog = false
            }
        )
    }
}

private fun getCurrentSortOrder(localStorage: LocalStorage): SortOrder {
    val sortingOrder = localStorage.getOrder()
    return if (sortingOrder != null) {
        SortOrder.valueOf(sortingOrder)
    } else {
        SortOrder.A_TO_Z
    }
}

private fun mapSortOrder(order: SortOrder) = when (order) {
    SortOrder.A_TO_Z -> LibrariesViewModel.SortOrder.A_TO_Z
    SortOrder.Z_TO_A -> LibrariesViewModel.SortOrder.Z_TO_A
    SortOrder.PINNED_FIRST -> LibrariesViewModel.SortOrder.PINNED_FIRST
}

@Composable
private fun SearchButton() {
    IconButton(
        onClick = {
        }
    ) {
        Icon(
            imageVector = Icons.Filled.Search,
            contentDescription = stringResource(R.string.search)
        )
    }
}

@Composable
private fun NightModeButton(nightModeManager: NightModeManager) {
    if (nightModeManager.isNightModeSupported) {
        var showDropdownMenu by remember { mutableStateOf(false) }
        var showNightModeDialog by remember { mutableStateOf(false) }
        IconButton(
            onClick = {
                showDropdownMenu = !showDropdownMenu
            }
        ) {
            Icon(
                imageVector = Default.MoreVert,
                contentDescription = stringResource(R.string.more_options)
            )
        }
        DropdownMenu(
            expanded = showDropdownMenu,
            onDismissRequest = { showDropdownMenu = false })
        {
            DropdownMenuItem(
                onClick = {
                    showDropdownMenu = false
                    showNightModeDialog = true
                }
            ) {
                Text(
                    text = stringResource(R.string.night_mode)
                )
            }
        }
        if (showNightModeDialog) {
            NightModeDialog(
                currentNightMode = nightModeManager.getCurrentNightMode(),
                onNightModeClick = { nightMode: NightModeManager.Mode ->
                    nightModeManager.setNightMode(nightMode)
                    showNightModeDialog = false
                },
                onDismiss = {
                    showDropdownMenu = false
                }
            )
        }
    }
}

@ExperimentalFoundationApi
@Composable
private fun LibrariesListContent(
    librariesViewModel: LibrariesViewModel,
    navController: NavController,
    scaffoldState: ScaffoldState
) {

    val librariesListState: LibrariesListState by librariesViewModel.librariesListState.observeAsState(
        LibrariesListState.Fresh
    )
    val lastUpdateCheckState by librariesViewModel.lastUpdateCheckState.observeAsState("N/A")
    val libraryDeleteState by librariesViewModel.deleteState.observeAsState(LibraryDeleteState.Fresh)

    Column(
        modifier = Modifier.fillMaxHeight()
    ) {
        LastUpdateCheckText(lastUpdateCheckState)
        when (librariesListState) {
            is LibrariesListState.Loading -> {
                CircularProgressIndicator()
            }
            is LibrariesListState.LibrariesLoaded -> {
                val libraries: List<Library> =
                    (librariesListState as LibrariesListState.LibrariesLoaded).libraries
                var showDeleteLibraryDialog by remember { mutableStateOf(false) }
                LibrariesList(
                    navController = navController,
                    scaffoldState = scaffoldState,
                    libraries = libraries,
                    libraryDeleteState = libraryDeleteState,
                    clickListener = { library: Library ->
                        val intent = Intent(Intent.ACTION_VIEW).apply {
                            data = Uri.parse(library.url)
                        }
//                        LocalContext.current.startActivity(intent)
                    },
                    longClickListener = { library: Library ->
                        librariesViewModel.libraryToDelete = library
                        showDeleteLibraryDialog = true
                    },
                    pinClickListener = { library: Library, pin: Boolean ->
                        librariesViewModel.setPinned(library, pin)
                    }
                )
                if (showDeleteLibraryDialog) {
                    DeleteLibraryDialog(
                        onDeleteClicked = {
                            showDeleteLibraryDialog = false
                            librariesViewModel.libraryToDelete?.let {
                                librariesViewModel.deleteLibrary(it.name)
                            }
                        },
                        onDismiss = {
                            showDeleteLibraryDialog = false
                        }
                    )
                }
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
        modifier = Modifier.padding(SPACE_NORMAL.dp)
    )
}

@ExperimentalFoundationApi
@Composable
private fun LibrariesList(
    navController: NavController,
    scaffoldState: ScaffoldState,
    libraries: List<Library>,
    libraryDeleteState: LibraryDeleteState,
    clickListener: (Library) -> Unit,
    longClickListener: (Library) -> Unit,
    pinClickListener: (Library, Boolean) -> Unit
) {
    if (libraries.isEmpty()) {
        NoLibrariesText()
    } else {
        if (libraryDeleteState is LibraryDeleteState.InProgress) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }
        Box(contentAlignment = Alignment.BottomStart, modifier = Modifier.fillMaxSize()) {
            LazyColumn {
                itemsIndexed(libraries) { index: Int, library: Library ->
                    LibraryItem(
                        library = library,
                        clickListener = clickListener,
                        longClickListener = longClickListener,
                        pinClickListener = pinClickListener
                    )
                    if (index != libraries.lastIndex) {
                        Divider()
                    }
                }
            }
            AddLibraryButton(
                clickListener = {
                    navController.navigate(Screen.AddLibrary.route)
                }
            )
            Snackbar(libraryDeleteState, scaffoldState)
        }
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
    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
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
    clickListener: (Library) -> Unit,
    longClickListener: (Library) -> Unit,
    pinClickListener: (Library, Boolean) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .combinedClickable(
                onClick = {
                    clickListener(library)
                },
                onLongClick = {
                    longClickListener(library)
                }
            )
            .padding(
                end = SPACE_NORMAL.dp,
                top = SPACE_NORMAL.dp,
                bottom = SPACE_NORMAL.dp
            )
    ) {
        PinToggleButton(library, pinClickListener)
        Column(modifier = Modifier.weight(1f)) {
            LibraryNameText(library)
            LibraryUrlText(library)
        }
        Text(text = library.version)
    }
}

@Composable
private fun PinToggleButton(library: Library, onCheckedChange: (Library, Boolean) -> Unit) {
    IconToggleButton(
        checked = library.isPinned(),
        onCheckedChange = {
            onCheckedChange(library, it)
        }
    ) {
        val pinImage = if (library.isPinned()) Icons.Filled.PushPin else Icons.Outlined.PushPin
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