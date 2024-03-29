@file:OptIn(ExperimentalComposeUiApi::class)

package ir.fallahpoor.releasetracker.features.addlibrary.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import ir.fallahpoor.releasetracker.R
import ir.fallahpoor.releasetracker.features.addlibrary.AddLibraryScreenUiState
import ir.fallahpoor.releasetracker.features.addlibrary.AddLibraryViewModel
import ir.fallahpoor.releasetracker.features.addlibrary.Event
import ir.fallahpoor.releasetracker.theme.ReleaseTrackerTheme

object AddLibraryScreenTags {
    const val SCREEN = "screen"
    const val CONTENT = "content"
    const val TITLE = "title"
    const val BACK_BUTTON = "backButton"
}

@Composable
fun AddLibraryScreen(
    addLibraryViewModel: AddLibraryViewModel = hiltViewModel(),
    isDarkTheme: Boolean,
    onBackClick: () -> Unit
) {
    ReleaseTrackerTheme(darkTheme = isDarkTheme) {
        val scaffoldState = rememberScaffoldState()
        Scaffold(
            modifier = Modifier.testTag(AddLibraryScreenTags.SCREEN),
            topBar = { AppBar(onBackClick) },
            scaffoldState = scaffoldState,
            snackbarHost = {
                scaffoldState.snackbarHostState
            }
        ) {
            val uiState: AddLibraryScreenUiState by addLibraryViewModel.uiState.collectAsState()
            val keyboardController = LocalSoftwareKeyboardController.current
            AddLibraryContent(
                modifier = Modifier
                    .fillMaxSize()
                    .testTag(AddLibraryScreenTags.CONTENT),
                snackbarHostState = scaffoldState.snackbarHostState,
                state = uiState.addLibraryState,
                libraryName = uiState.libraryName,
                onLibraryNameChange = { libraryName ->
                    addLibraryViewModel.handleEvent(Event.UpdateLibraryName(libraryName))
                },
                libraryUrlPath = uiState.libraryUrlPath,
                onLibraryUrlPathChange = { libraryUrlPath ->
                    addLibraryViewModel.handleEvent(Event.UpdateLibraryUrlPath(libraryUrlPath))
                },
                onAddLibraryClick = { libraryName, libraryUrlPath ->
                    addLibraryViewModel.handleEvent(Event.AddLibrary(libraryName, libraryUrlPath))
                    keyboardController?.hide()
                },
                onErrorDismissed = { addLibraryViewModel.handleEvent(Event.ErrorDismissed) }
            )
        }
    }
}

@Composable
private fun AppBar(onBackClick: () -> Unit) {
    TopAppBar(
        title = {
            Text(
                modifier = Modifier.testTag(AddLibraryScreenTags.TITLE),
                text = stringResource(R.string.add_library)
            )
        },
        navigationIcon = {
            BackButton { onBackClick() }
        }
    )
}

@Composable
private fun BackButton(onBackClick: () -> Unit) {
    IconButton(
        modifier = Modifier.testTag(AddLibraryScreenTags.BACK_BUTTON),
        onClick = onBackClick
    ) {
        Icon(
            imageVector = Icons.Filled.ArrowBack,
            contentDescription = stringResource(R.string.back)
        )
    }
}

@Composable
@Preview
private fun AddLibraryScreenPreview() {
    ReleaseTrackerTheme {
        Surface {
            AddLibraryScreen(
                isDarkTheme = false,
                onBackClick = {}
            )
        }
    }
}