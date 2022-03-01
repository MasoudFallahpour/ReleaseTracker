@file:OptIn(ExperimentalFoundationApi::class)

package ir.fallahpoor.releasetracker.libraries.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import ir.fallahpoor.releasetracker.R
import ir.fallahpoor.releasetracker.data.entity.Library
import ir.fallahpoor.releasetracker.theme.spacing

object LibrariesListTags {
    const val LIBRARIES_LIST = "librariesList"
    const val ADD_LIBRARY_BUTTON = "LibrariesListAddLibraryButton"
}

@Composable
fun LibrariesList(
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
private fun NoLibrariesText() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            modifier = Modifier.padding(MaterialTheme.spacing.normal),
            text = stringResource(R.string.no_libraries)
        )
    }
}

@Composable
private fun AddLibraryButton(clickListener: () -> Unit) {
    FloatingActionButton(
        modifier = Modifier
            .padding(MaterialTheme.spacing.normal)
            .testTag(LibrariesListTags.ADD_LIBRARY_BUTTON),
        onClick = clickListener
    ) {
        Icon(
            imageVector = Icons.Filled.Add,
            contentDescription = stringResource(R.string.add_library)
        )
    }
}