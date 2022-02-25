@file:OptIn(ExperimentalComposeUiApi::class)

package ir.fallahpoor.releasetracker.addlibrary.ui

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
import ir.fallahpoor.releasetracker.addlibrary.AddLibraryScreenState
import ir.fallahpoor.releasetracker.addlibrary.AddLibraryViewModel
import ir.fallahpoor.releasetracker.addlibrary.Event
import ir.fallahpoor.releasetracker.theme.ReleaseTrackerTheme

@Composable
fun AddLibraryScreen(
    addLibraryViewModel: AddLibraryViewModel = hiltViewModel(),
    isDarkTheme: Boolean,
    onBackClick: () -> Unit
) {
    ReleaseTrackerTheme(darkTheme = isDarkTheme) {
        val scaffoldState = rememberScaffoldState()
        Scaffold(
            modifier = Modifier.testTag(AddLibraryTags.SCREEN),
            topBar = { AppBar(onBackClick) },
            scaffoldState = scaffoldState,
            snackbarHost = {
                scaffoldState.snackbarHostState
            }
        ) {
            val state: AddLibraryScreenState by addLibraryViewModel.state.collectAsState()
            val keyboardController = LocalSoftwareKeyboardController.current
            AddLibraryContent(
                modifier = Modifier.fillMaxSize(),
                snackbarHostState = scaffoldState.snackbarHostState,
                state = state.addLibraryState,
                libraryName = state.libraryName,
                onLibraryNameChange = { libraryName ->
                    addLibraryViewModel.handleEvent(Event.UpdateLibraryName(libraryName))
                },
                libraryUrlPath = state.libraryUrlPath,
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
                modifier = Modifier.testTag(AddLibraryTags.TITLE),
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
        modifier = Modifier.testTag(AddLibraryTags.BACK_BUTTON),
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