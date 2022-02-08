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
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
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
    const val WHOLE_SCREEN = "wholeScreen"
    const val TITLE = "title"
    const val BACK_BUTTON = "backButton"
    const val PROGRESS_INDICATOR = "progressIndicator"
    const val LIBRARY_NAME_TEXT_FIELD = "libraryNameTextField"
    const val LIBRARY_URL_TEXT_FIELD = "libraryUrlTextField"
    const val ADD_LIBRARY_BUTTON = "addLibraryButton"
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
        modifier = Modifier.testTag(AddLibraryTags.WHOLE_SCREEN),
        isDarkTheme = isDarkTheme,
        scaffoldState = scaffoldState,
        topBar = { AppBar(onBackClick) }
    ) {
        val state: AddLibraryState by addLibraryViewModel.state.observeAsState(
            AddLibraryState.Initial
        )
        val keyboard = LocalSoftwareKeyboardController.current
        val addLibrary = {
            addLibraryViewModel.addLibrary(
                addLibraryViewModel.libraryName,
                addLibraryViewModel.libraryUrlPath
            )
            keyboard?.hide()
        }
        Column(modifier = Modifier.fillMaxSize()) {
            if (state is AddLibraryState.InProgress) {
                ProgressIndicator()
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(SPACE_NORMAL.dp)
            ) {
                val focusRequester = remember { FocusRequester() }
                LibraryNameInput(
                    libraryName = addLibraryViewModel.libraryName,
                    onLibraryNameChange = { addLibraryViewModel.libraryName = it },
                    showError = state is AddLibraryState.EmptyLibraryName,
                    onNextClick = { focusRequester.requestFocus() }
                )
                Spacer(modifier = Modifier.height(SPACE_SMALL.dp))
                LibraryUrlInput(
                    libraryUrlPath = addLibraryViewModel.libraryUrlPath,
                    onLibraryUrlPathChange = { addLibraryViewModel.libraryUrlPath = it },
                    state = state,
                    onDoneClick = { addLibrary() },
                    focusRequester = focusRequester
                )
            }
            AddLibraryButton(
                isEnabled = state !is AddLibraryState.InProgress,
                onAddLibraryClick = { addLibrary() }
            )
            if (state is AddLibraryState.Error) {
                val error = state as AddLibraryState.Error
                Snackbar(
                    snackbarHostState = scaffoldState.snackbarHostState,
                    message = error.message
                ) {
                    addLibraryViewModel.resetState()
                }
            }
            if (state is AddLibraryState.LibraryAdded) {
                Snackbar(
                    snackbarHostState = scaffoldState.snackbarHostState,
                    message = stringResource(R.string.library_added)
                )
            }
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
private fun ProgressIndicator() {
    LinearProgressIndicator(
        modifier = Modifier
            .testTag(AddLibraryTags.PROGRESS_INDICATOR)
            .fillMaxWidth()
            .height(2.dp)
    )
}

@Composable
private fun LibraryNameInput(
    libraryName: String,
    onLibraryNameChange: (String) -> Unit,
    onNextClick: () -> Unit,
    showError: Boolean
) {
    LibraryNameTextField(
        libraryName = libraryName,
        onLibraryNameChange = onLibraryNameChange,
        isError = showError,
        onNextClick = onNextClick
    )
    LibraryNameErrorText(show = showError)
}

@Composable
private fun LibraryUrlInput(
    libraryUrlPath: String,
    onLibraryUrlPathChange: (String) -> Unit,
    state: AddLibraryState,
    onDoneClick: () -> Unit,
    focusRequester: FocusRequester
) {
    LibraryUrlPathTextField(
        libraryUrlPath = libraryUrlPath,
        onLibraryUrlPathChange = onLibraryUrlPathChange,
        isError = state is AddLibraryState.EmptyLibraryUrl || state is AddLibraryState.InvalidLibraryUrl,
        onDoneClick = onDoneClick,
        focusRequester = focusRequester
    )
    LibraryUrlErrorText(state = state)
}

@Composable
private fun LibraryNameTextField(
    libraryName: String,
    onLibraryNameChange: (String) -> Unit,
    isError: Boolean,
    onNextClick: () -> Unit
) {
    OutlinedTextField(
        modifier = Modifier
            .testTag(AddLibraryTags.LIBRARY_NAME_TEXT_FIELD)
            .fillMaxWidth(),
        value = libraryName,
        label = {
            Text(text = stringResource(R.string.library_name))
        },
        onValueChange = onLibraryNameChange,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
        keyboardActions = KeyboardActions(
            onNext = { onNextClick() }
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
    libraryUrlPath: String,
    onLibraryUrlPathChange: (String) -> Unit,
    isError: Boolean,
    onDoneClick: () -> Unit,
    focusRequester: FocusRequester
) {
    OutlinedTextFieldWithPrefix(
        modifier = Modifier
            .testTag(AddLibraryTags.LIBRARY_URL_TEXT_FIELD)
            .fillMaxWidth()
            .focusRequester(focusRequester),
        prefix = GITHUB_BASE_URL,
        hint = stringResource(R.string.library_url),
        text = libraryUrlPath,
        onTextChange = onLibraryUrlPathChange,
        imeAction = ImeAction.Done,
        keyboardActions = KeyboardActions(
            onDone = { onDoneClick() }
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
private fun AddLibraryButton(isEnabled: Boolean, onAddLibraryClick: () -> Unit) {
    Button(
        modifier = Modifier
            .testTag(AddLibraryTags.ADD_LIBRARY_BUTTON)
            .padding(SPACE_NORMAL.dp),
        onClick = onAddLibraryClick,
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
private fun Snackbar(
    snackbarHostState: SnackbarHostState,
    message: String,
    onDismiss: () -> Unit = {}
) {
    LaunchedEffect(snackbarHostState) {
        val snackbarResult: SnackbarResult = snackbarHostState.showSnackbar(message = message)
        if (snackbarResult == SnackbarResult.Dismissed) {
            onDismiss()
        }
    }
    DefaultSnackbar(snackbarHostState = snackbarHostState)
}

@Composable
@Preview
private fun AddLibraryScreenPreview() {
    ReleaseTrackerTheme {
        Surface {
            AddLibraryScreen(
                isDarkTheme = false,
                onBackClick = {},
                scaffoldState = rememberScaffoldState()
            )
        }
    }
}