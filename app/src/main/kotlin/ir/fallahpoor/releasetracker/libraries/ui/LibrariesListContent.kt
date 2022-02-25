@file:OptIn(
    ExperimentalAnimationApi::class,
    ExperimentalFoundationApi::class,
    ExperimentalMaterialApi::class
)

package ir.fallahpoor.releasetracker.libraries.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import ir.fallahpoor.releasetracker.R
import ir.fallahpoor.releasetracker.common.SPACE_NORMAL
import ir.fallahpoor.releasetracker.common.SPACE_SMALL
import ir.fallahpoor.releasetracker.data.entity.Library
import ir.fallahpoor.releasetracker.libraries.LibrariesListState
import ir.fallahpoor.releasetracker.theme.ReleaseTrackerTheme

object LibrariesListTags {
    const val LAST_UPDATE_CHECK_TEXT = "lastUpdateCheckText"
    const val ADD_LIBRARY_BUTTON = "addLibraryButton"
    const val PROGRESS_INDICATOR = "progressIndicator"
    const val LIBRARIES_LIST = "librariesList"
    const val LIBRARY_ITEM = "libraryItem_"
}

@Composable
fun LibrariesListContent(
    modifier: Modifier = Modifier,
    librariesListState: LibrariesListState,
    lastUpdateCheck: String,
    onLibraryClick: (Library) -> Unit,
    onLibraryDismissed: (Library) -> Unit,
    onPinLibraryClick: (Library, Boolean) -> Unit,
    onAddLibraryClick: () -> Unit,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LastUpdateCheckText(lastUpdateCheck)
        when (librariesListState) {
            is LibrariesListState.Loading -> ProgressIndicator()
            is LibrariesListState.LibrariesLoaded -> {
                LibrariesList(
                    libraries = librariesListState.libraries,
                    onLibraryClick = onLibraryClick,
                    onLibraryDismissed = onLibraryDismissed,
                    onPinLibraryClick = onPinLibraryClick,
                    onAddLibraryClick = onAddLibraryClick
                )
            }
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
                .padding(SPACE_NORMAL.dp)
                .testTag(LibrariesListTags.LAST_UPDATE_CHECK_TEXT),
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
        modifier = Modifier
            .fillMaxSize()
            .testTag(LibrariesListTags.LIBRARIES_LIST),
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
                        modifier = Modifier
                            .fillMaxWidth()
                            .animateItemPlacement(),
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
    modifier: Modifier = Modifier,
    library: Library,
    onLibraryClick: (Library) -> Unit,
    onPinLibraryClick: (Library, Boolean) -> Unit,
    onLibraryDismissed: (Library) -> Unit,
) {
    val dismissState = rememberDismissState(
        confirmStateChange = {
            if (it == DismissValue.DismissedToEnd) {
                onLibraryDismissed(library)
            }
            true
        }
    )
    SwipeToDismiss(
        modifier = modifier.testTag(LibrariesListTags.LIBRARY_ITEM + library.name),
        state = dismissState,
        dismissThresholds = { FractionalThreshold(0.3f) },
        directions = setOf(DismissDirection.StartToEnd),
        dismissContent = {
            LibraryItemForeground(
                modifier = Modifier
                    .height(70.dp)
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
                    .height(70.dp),
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
                onPinnedChange = onPinLibraryClick
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
            targetValue = if (dismissState.targetValue == DismissValue.DismissedToEnd) {
                MaterialTheme.colors.onError
            } else {
                MaterialTheme.colors.onSurface
            },
            animationSpec = tween(),
        )
        val iconScale by animateFloatAsState(
            targetValue = if (dismissState.targetValue == DismissValue.DismissedToEnd) {
                1f
            } else {
                0.75f
            }
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