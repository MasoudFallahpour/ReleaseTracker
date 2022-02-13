@file:OptIn(ExperimentalMaterialApi::class, ExperimentalAnimationApi::class)

package ir.fallahpoor.releasetracker.libraries.view

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import ir.fallahpoor.releasetracker.R
import ir.fallahpoor.releasetracker.common.SPACE_NORMAL
import ir.fallahpoor.releasetracker.common.SPACE_SMALL
import ir.fallahpoor.releasetracker.common.managers.NightModeManager
import ir.fallahpoor.releasetracker.data.entity.Library
import ir.fallahpoor.releasetracker.data.utils.NightMode
import ir.fallahpoor.releasetracker.data.utils.SortOrder
import ir.fallahpoor.releasetracker.libraries.view.composables.Toolbar
import ir.fallahpoor.releasetracker.libraries.viewmodel.LibrariesViewModel
import ir.fallahpoor.releasetracker.theme.ReleaseTrackerTheme

object LibrariesListTags {
    const val ADD_LIBRARY_BUTTON = "addLibraryButton"
    const val PROGRESS_INDICATOR = "progressIndicator"
    const val LIBRARY_ITEM = "libraryItem"
}

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
    val librariesListState: LibrariesListState by librariesViewModel.librariesListState.observeAsState(
        LibrariesListState.Loading
    )
    val lastUpdateCheck: String by librariesViewModel.lastUpdateCheckState.observeAsState("N/A")
    val getLibraries = {
        librariesViewModel.getLibraries(
            sortOrder = librariesViewModel.sortOrder,
            searchQuery = librariesViewModel.searchQuery
        )
    }

    LaunchedEffect(true) {
        getLibraries()
    }

    ReleaseTrackerTheme(darkTheme = isNightModeOn) {
        Scaffold(
            scaffoldState = scaffoldState,
            snackbarHost = { scaffoldState.snackbarHostState },
            topBar = {
                Toolbar(
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
            LibrariesListContent(
                librariesListState = librariesListState,
                lastUpdateCheck = lastUpdateCheck,
                onLibraryClick = onLibraryClick,
                onLibraryDismissed = { library: Library ->
                    librariesViewModel.deleteLibrary(library)
                },
                onPinLibraryClick = { library: Library, pinned: Boolean ->
                    librariesViewModel.pinLibrary(library, pinned)
                },
                onAddLibraryClick = onAddLibraryClick,
            )
        }
    }
}

@Composable
private fun LibrariesListContent(
    librariesListState: LibrariesListState,
    lastUpdateCheck: String,
    onLibraryClick: (Library) -> Unit,
    onLibraryDismissed: (Library) -> Unit,
    onPinLibraryClick: (Library, Boolean) -> Unit,
    onAddLibraryClick: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LastUpdateCheckText(lastUpdateCheck)
        when (librariesListState) {
            is LibrariesListState.Loading -> ProgressIndicator()
            is LibrariesListState.LibrariesLoaded -> {
                val libraries: List<Library> = librariesListState.libraries
                LibrariesList(
                    libraries = libraries,
                    onLibraryClick = onLibraryClick,
                    onLibraryDismissed = onLibraryDismissed,
                    onPinLibraryClick = onPinLibraryClick,
                    onAddLibraryClick = onAddLibraryClick
                )
            }
            LibrariesListState.Fresh -> {}
        }
    }
}

@Composable
private fun ProgressIndicator() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .testTag(LibrariesListTags.PROGRESS_INDICATOR),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun LastUpdateCheckText(lastUpdateCheck: String) {
    AnimatedContent(targetState = lastUpdateCheck) { lastUpdateCheck: String ->
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SPACE_NORMAL.dp),
            text = stringResource(R.string.last_check_for_updates, lastUpdateCheck)
        )
    }
}

@Composable
private fun LibrariesList(
    libraries: List<Library>,
    onLibraryClick: (Library) -> Unit,
    onLibraryDismissed: (Library) -> Unit,
    onPinLibraryClick: (Library, Boolean) -> Unit,
    onAddLibraryClick: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomStart
    ) {
        if (libraries.isEmpty()) {
            NoLibrariesText()
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(
                    items = libraries,
                    key = { library: Library -> library.name }
                ) { library: Library ->
                    LibraryItem(
                        library = library,
                        onLibraryClick = onLibraryClick,
                        onPinLibraryClick = onPinLibraryClick,
                        onLibraryDismissed = onLibraryDismissed
                    )
                    Divider()
                }
            }
        }
        AddLibraryButton(clickListener = onAddLibraryClick)
    }
}

@Composable
private fun LibraryItem(
    library: Library,
    onLibraryClick: (Library) -> Unit,
    onPinLibraryClick: (Library, Boolean) -> Unit,
    onLibraryDismissed: (Library) -> Unit,
) {
    var libraryIsDismissed by remember { mutableStateOf(false) }
    val dismissState = rememberDismissState(
        confirmStateChange = {
            if (it == DismissValue.DismissedToEnd) {
                libraryIsDismissed = true
            }
            true
        }
    )
    val libraryItemHeight by animateDpAsState(
        targetValue = if (libraryIsDismissed) 0.dp else 70.dp,
        animationSpec = tween(delayMillis = 200),
        finishedListener = { onLibraryDismissed(library) }
    )
    SwipeToDismiss(
        modifier = Modifier.testTag(LibrariesListTags.LIBRARY_ITEM),
        state = dismissState,
        dismissThresholds = { FractionalThreshold(0.3f) },
        directions = setOf(DismissDirection.StartToEnd),
        dismissContent = {
            LibraryItemForeground(
                modifier = Modifier
                    .height(libraryItemHeight)
                    .fillMaxWidth(),
                dismissState = dismissState,
                library = library,
                onLibraryClick = { onLibraryClick(library) },
                onPinLibraryClick = { pin: Boolean ->
                    onPinLibraryClick(library, pin)
                }
            )
        },
        background = {
            LibraryItemBackground(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(libraryItemHeight),
                dismissState = dismissState
            )
        }
    )
}

@Composable
private fun NoLibrariesText() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            modifier = Modifier.padding(SPACE_NORMAL.dp),
            text = stringResource(R.string.no_libraries)
        )
    }
}

@Composable
private fun LibraryItemForeground(
    modifier: Modifier = Modifier,
    dismissState: DismissState,
    library: Library,
    onLibraryClick: () -> Unit,
    onPinLibraryClick: (Boolean) -> Unit
) {
    val cardElevation = animateDpAsState(
        if (dismissState.dismissDirection != null) 4.dp else 0.dp
    ).value
    Card(
        modifier = modifier.clickable(onClick = onLibraryClick),
        elevation = cardElevation
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            PinToggleButton(
                isPinned = library.isPinned(),
                onPinnedChange = { isPinned: Boolean ->
                    onPinLibraryClick(isPinned)
                }
            )
            Column(modifier = Modifier.weight(1f)) {
                LibraryNameText(libraryName = library.name)
                LibraryUrlText(libraryUrl = library.url)
            }
            AnimatedContent(targetState = library.version) { version: String ->
                Text(
                    modifier = Modifier.padding(horizontal = SPACE_NORMAL.dp),
                    text = version
                )
            }
        }
    }
}

@Composable
private fun PinToggleButton(isPinned: Boolean, onPinnedChange: (Boolean) -> Unit) {
    IconToggleButton(
        checked = isPinned,
        onCheckedChange = onPinnedChange
    ) {
        val pinImage: Painter
        val contentDescription: String
        if (isPinned) {
            pinImage = painterResource(R.drawable.ic_pin_filled)
            contentDescription = stringResource(R.string.unpin_library)
        } else {
            pinImage = painterResource(R.drawable.ic_pin_outline)
            contentDescription = stringResource(R.string.pin_library)
        }
        Icon(
            painter = pinImage,
            tint = MaterialTheme.colors.secondary,
            contentDescription = contentDescription
        )
    }
}

@Composable
private fun LibraryNameText(libraryName: String) {
    EllipsisText(
        modifier = Modifier.fillMaxWidth(),
        text = libraryName,
        style = MaterialTheme.typography.body1.copy(fontWeight = FontWeight.Black)
    )
}

@Composable
private fun LibraryUrlText(libraryUrl: String) {
    EllipsisText(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = SPACE_SMALL.dp),
        text = libraryUrl,
        style = MaterialTheme.typography.body2
    )
}

@Composable
private fun EllipsisText(modifier: Modifier = Modifier, text: String, style: TextStyle) {
    Text(
        modifier = modifier,
        text = text,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        style = style
    )
}

@Composable
private fun LibraryItemBackground(
    modifier: Modifier = Modifier,
    dismissState: DismissState
) {
    val backgroundColor by animateColorAsState(
        targetValue = when (dismissState.targetValue) {
            DismissValue.DismissedToEnd -> MaterialTheme.colors.error
            else -> MaterialTheme.colors.background
        },
        animationSpec = tween()
    )
    Box(
        modifier = modifier
            .background(backgroundColor)
            .padding(horizontal = SPACE_NORMAL.dp)
    ) {
        val iconColor by animateColorAsState(
            targetValue = if (dismissState.targetValue == DismissValue.DismissedToEnd) MaterialTheme.colors.onError else MaterialTheme.colors.onSurface,
            animationSpec = tween(),
        )
        val iconScale by animateFloatAsState(
            targetValue = if (dismissState.targetValue == DismissValue.DismissedToEnd) 1f else 0.75f
        )
        if (dismissState.currentValue == DismissValue.Default) {
            Icon(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .scale(iconScale),
                imageVector = Icons.Default.Delete,
                contentDescription = stringResource(R.string.delete_library),
                tint = iconColor
            )
        }
    }
}

@Composable
private fun AddLibraryButton(clickListener: () -> Unit) {
    FloatingActionButton(
        modifier = Modifier
            .padding(SPACE_NORMAL.dp)
            .testTag(LibrariesListTags.ADD_LIBRARY_BUTTON),
        onClick = clickListener
    ) {
        Icon(
            imageVector = Icons.Filled.Add,
            contentDescription = stringResource(R.string.add_library)
        )
    }
}

@Preview
@Composable
private fun LibraryItemForegroundPreview() {
    ReleaseTrackerTheme {
        Surface {
            LibraryItemForeground(
                library = Library(
                    name = "Release Tracker",
                    url = "https://github.com/masoodfallahpoor/ReleaseTracker",
                    version = "1.0",
                    pinned = 0
                ),
                onLibraryClick = {},
                onPinLibraryClick = {},
                dismissState = rememberDismissState()
            )
        }
    }
}

@Preview
@Composable
private fun LibrariesListContentPreview() {
    ReleaseTrackerTheme {
        Surface {
            LibrariesListContent(
                librariesListState = LibrariesListState.Loading,
                lastUpdateCheck = "N/A",
                onLibraryClick = {},
                onLibraryDismissed = {},
                onPinLibraryClick = { _, _ -> },
                onAddLibraryClick = {}
            )
        }
    }
}