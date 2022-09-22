@file:OptIn(ExperimentalMaterialApi::class, ExperimentalAnimationApi::class)

package ir.fallahpoor.releasetracker.features.libraries.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.material.DismissDirection
import androidx.compose.material.DismissState
import androidx.compose.material.DismissValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FractionalThreshold
import androidx.compose.material.Icon
import androidx.compose.material.IconToggleButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.rememberDismissState
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
import ir.fallahpoor.releasetracker.data.repository.library.models.Library
import ir.fallahpoor.releasetracker.theme.ReleaseTrackerTheme
import ir.fallahpoor.releasetracker.theme.spacing

object LibraryItemTags {
    const val LIBRARY_ITEM = "LibraryItem_"
    const val LIBRARY_NAME = "LibraryItemLibraryName"
    const val LIBRARY_URL = "LibraryItemLibraryUrl"
    const val LIBRARY_VERSION = "LibraryItemLibraryVersion"
    const val PIN_BUTTON = "LibraryItemPinButton_"
}

@Composable
fun LibraryItem(
    modifier: Modifier = Modifier,
    library: Library,
    onLibraryClick: (Library) -> Unit,
    onPinLibraryClick: (Library, Boolean) -> Unit,
    onLibraryDismissed: (Library) -> Unit
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
        modifier = modifier.testTag(LibraryItemTags.LIBRARY_ITEM + library.name),
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
                modifier = Modifier.testTag(LibraryItemTags.PIN_BUTTON + library.name),
                isPinned = library.isPinned,
                onPinnedChange = onPinLibraryClick
            )
            Column(modifier = Modifier.weight(1f)) {
                LibraryNameText(libraryName = library.name)
                LibraryUrlText(libraryUrl = library.url)
            }
            LibraryVersion(libraryVersion = library.version)
        }
    }
}

@Composable
private fun LibraryVersion(libraryVersion: String) {
    AnimatedContent(targetState = libraryVersion) { version: String ->
        Text(
            modifier = Modifier
                .padding(horizontal = MaterialTheme.spacing.normal)
                .testTag(LibraryItemTags.LIBRARY_VERSION),
            text = version
        )
    }
}

@Composable
private fun PinToggleButton(
    modifier: Modifier = Modifier,
    isPinned: Boolean,
    onPinnedChange: (Boolean) -> Unit
) {
    IconToggleButton(
        modifier = modifier,
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
        modifier = Modifier
            .fillMaxWidth()
            .testTag(LibraryItemTags.LIBRARY_NAME),
        text = libraryName,
        style = MaterialTheme.typography.body1.copy(fontWeight = FontWeight.Black)
    )
}

@Composable
private fun LibraryUrlText(libraryUrl: String) {
    EllipsisText(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = MaterialTheme.spacing.small)
            .testTag(LibraryItemTags.LIBRARY_URL),
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
            .padding(horizontal = MaterialTheme.spacing.normal)
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
                    isPinned = false
                ),
                onLibraryClick = {},
                onPinLibraryClick = {},
                dismissState = rememberDismissState()
            )
        }
    }
}