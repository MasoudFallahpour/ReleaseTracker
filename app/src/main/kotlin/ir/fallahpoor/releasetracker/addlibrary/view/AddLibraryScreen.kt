package ir.fallahpoor.releasetracker.addlibrary.view

import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import ir.fallahpoor.releasetracker.R
import ir.fallahpoor.releasetracker.addlibrary.viewmodel.AddLibraryViewModel
import ir.fallahpoor.releasetracker.common.GITHUB_BASE_URL
import ir.fallahpoor.releasetracker.common.SPACE_NORMAL
import ir.fallahpoor.releasetracker.common.SPACE_SMALL
import ir.fallahpoor.releasetracker.common.composables.DefaultSnackbar
import ir.fallahpoor.releasetracker.common.composables.Screen
import ir.fallahpoor.releasetracker.theme.ReleaseTrackerTheme

object AddLibraryTags {
    const val ADD_LIBRARY_SCREEN = "addLibraryScreen"
    const val PROGRESS_INDICATOR = "addLibraryProgressIndicator"
    const val LIBRARY_NAME_TEXT_FIELD = "addLibraryLibraryNameTextField"
    const val LIBRARY_URL_TEXT_FIELD = "addLibraryLibraryUrlTextField"
    const val ADD_LIBRARY_BUTTON = "addLibraryAddLibraryButton"
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun AddLibraryScreen(
    addLibraryViewModel: AddLibraryViewModel = hiltViewModel(),
    isDarkTheme: Boolean,
    onBackClick: () -> Unit,
    scaffoldState: ScaffoldState = rememberScaffoldState()
) {
    Screen(
        modifier = Modifier.testTag(AddLibraryTags.ADD_LIBRARY_SCREEN),
        isDarkTheme = isDarkTheme,
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(R.string.add_library))
                },
                navigationIcon = {
                    BackButton(onBackClick = onBackClick)
                }
            )
        }
    ) {
        val addLibraryState: AddLibraryState by addLibraryViewModel.state.observeAsState(
            AddLibraryState.Fresh
        )
        val keyboardController: SoftwareKeyboardController? =
            LocalSoftwareKeyboardController.current
        AddLibraryContent(
            state = addLibraryState,
            scaffoldState = scaffoldState,
            libraryName = addLibraryViewModel.libraryName,
            onLibraryNameChange = { libraryName: String ->
                addLibraryViewModel.libraryName = libraryName
            },
            libraryUrlPath = addLibraryViewModel.libraryUrlPath,
            onLibraryUrlPathChange = { libraryUrlPath: String ->
                addLibraryViewModel.libraryUrlPath = libraryUrlPath
            },
            onAddLibrary = {
                addLibraryViewModel.addLibrary(
                    addLibraryViewModel.libraryName,
                    addLibraryViewModel.libraryUrlPath
                )
                keyboardController?.hide()
            }
        )
    }
}

@Composable
private fun BackButton(onBackClick: () -> Unit) {
    IconButton(onClick = onBackClick) {
        Icon(
            imageVector = Icons.Filled.ArrowBack,
            contentDescription = stringResource(R.string.back)
        )
    }
}

@Composable
private fun AddLibraryContent(
    state: AddLibraryState,
    scaffoldState: ScaffoldState,
    libraryName: String,
    onLibraryNameChange: (String) -> Unit,
    libraryUrlPath: String,
    onLibraryUrlPathChange: (String) -> Unit,
    onAddLibrary: () -> Unit,
) {

    Column(modifier = Modifier.fillMaxSize()) {
        if (state is AddLibraryState.InProgress) {
            ProgressIndicator()
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(SPACE_NORMAL.dp)
        ) {
            LibraryNameTextField(
                text = libraryName,
                onTextChange = { text: String ->
                    onLibraryNameChange(text)
                },
                isError = state is AddLibraryState.EmptyLibraryName
            )
            LibraryNameErrorText(show = state is AddLibraryState.EmptyLibraryName)
            Spacer(modifier = Modifier.height(SPACE_SMALL.dp))
            LibraryUrlPathTextField(
                text = libraryUrlPath,
                onTextChange = onLibraryUrlPathChange,
                isError = state is AddLibraryState.EmptyLibraryUrl || state is AddLibraryState.InvalidLibraryUrl,
                onDoneClick = onAddLibrary
            )
            LibraryUrlErrorText(state = state)
        }
        AddLibraryButton(
            isEnabled = state !is AddLibraryState.InProgress,
            clickListener = onAddLibrary
        )
        Snackbar(
            state = state,
            scaffoldState = scaffoldState
        )
    }

}

@Composable
private fun ProgressIndicator() {
    LinearProgressIndicator(
        modifier = Modifier
            .testTag(AddLibraryTags.PROGRESS_INDICATOR)
            .fillMaxWidth()
            .height(2.dp)
    )
}

@Composable
private fun LibraryNameTextField(
    text: String,
    onTextChange: (String) -> Unit,
    isError: Boolean
) {
    val focusManager = LocalFocusManager.current
    OutlinedTextField(
        modifier = Modifier
            .testTag(AddLibraryTags.LIBRARY_NAME_TEXT_FIELD)
            .fillMaxWidth(),
        value = text,
        label = {
            Text(text = stringResource(R.string.library_name))
        },
        onValueChange = onTextChange,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
        keyboardActions = KeyboardActions(
            onDone = {
                focusManager.moveFocus(focusDirection = FocusDirection.Down)
            }
        ),
        singleLine = true,
        isError = isError
    )
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun LibraryNameErrorText(show: Boolean) {
    AnimatedVisibility(visible = show) {
        ErrorText(R.string.library_name_empty)
    }
}

@Composable
private fun LibraryUrlPathTextField(
    text: String,
    onTextChange: (String) -> Unit,
    isError: Boolean,
    onDoneClick: () -> Unit
) {
    val focusManager = LocalFocusManager.current
    OutlinedTextFieldWithPrefix(
        modifier = Modifier
            .testTag(AddLibraryTags.LIBRARY_URL_TEXT_FIELD)
            .fillMaxWidth(),
        prefix = GITHUB_BASE_URL,
        hint = stringResource(R.string.library_url),
        text = text,
        onTextChange = onTextChange,
        imeAction = ImeAction.Done,
        keyboardActions = KeyboardActions(
            onDone = {
                onDoneClick()
                focusManager.clearFocus()
            }
        ),
        isError = isError
    )
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun LibraryUrlErrorText(state: AddLibraryState) {
    AnimatedVisibility(visible = state is AddLibraryState.EmptyLibraryUrl) {
        ErrorText(R.string.library_url_empty)
    }
    AnimatedVisibility(visible = state is AddLibraryState.InvalidLibraryUrl) {
        ErrorText(R.string.library_url_invalid)
    }
}

@Composable
private fun ErrorText(@StringRes textRes: Int) {
    Text(
        text = stringResource(textRes),
        style = TextStyle(color = MaterialTheme.colors.error)
    )
}

@Composable
private fun AddLibraryButton(isEnabled: Boolean, clickListener: () -> Unit) {
    Button(
        modifier = Modifier
            .testTag(AddLibraryTags.ADD_LIBRARY_BUTTON)
            .padding(SPACE_NORMAL.dp),
        onClick = clickListener,
        enabled = isEnabled
    ) {
        Text(
            text = stringResource(R.string.add_library),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun Snackbar(state: AddLibraryState, scaffoldState: ScaffoldState) {

    if (state is AddLibraryState.Error) {
        val message = state.message
        LaunchedEffect(scaffoldState.snackbarHostState) {
            scaffoldState.snackbarHostState.showSnackbar(message = message)
        }
    }

    if (state is AddLibraryState.LibraryAdded) {
        val message = stringResource(R.string.library_added)
        LaunchedEffect(scaffoldState.snackbarHostState) {
            scaffoldState.snackbarHostState.showSnackbar(message = message)
        }
    }

    DefaultSnackbar(snackbarHostState = scaffoldState.snackbarHostState)

}

@Composable
@Preview
private fun AddLibraryContentPreview() {
    ReleaseTrackerTheme {
        Surface {
            AddLibraryContent(
                state = AddLibraryState.Fresh,
                scaffoldState = rememberScaffoldState(),
                libraryName = "",
                onLibraryNameChange = {},
                libraryUrlPath = "",
                onLibraryUrlPathChange = {},
                onAddLibrary = {}
            )
        }
    }
}