@file:OptIn(ExperimentalAnimationApi::class)

package ir.fallahpoor.releasetracker.libraries.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ir.fallahpoor.releasetracker.R
import ir.fallahpoor.releasetracker.common.SPACE_NORMAL
import ir.fallahpoor.releasetracker.data.entity.Library
import ir.fallahpoor.releasetracker.libraries.LibrariesListState
import ir.fallahpoor.releasetracker.theme.ReleaseTrackerTheme

object LibrariesListContentTags {
    const val LAST_UPDATE_CHECK_TEXT = "lastUpdateCheckText"
    const val PROGRESS_INDICATOR = "progressIndicator"
}

@Composable
fun LibrariesListContent(
    modifier: Modifier = Modifier,
    librariesListState: LibrariesListState,
    lastUpdateCheck: String,
    onLibraryClick: (Library) -> Unit,
    onLibraryDismissed: (Library) -> Unit,
    onPinLibraryClick: (Library, Boolean) -> Unit,
    onAddLibraryClick: () -> Unit
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
            .testTag(LibrariesListContentTags.PROGRESS_INDICATOR),
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
                .testTag(LibrariesListContentTags.LAST_UPDATE_CHECK_TEXT),
            text = stringResource(R.string.last_check_for_updates, lastUpdateCheck)
        )
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