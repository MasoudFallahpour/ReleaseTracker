package ir.fallahpoor.releasetracker.addlibrary.ui

import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ir.fallahpoor.releasetracker.R
import ir.fallahpoor.releasetracker.addlibrary.AddLibraryState
import ir.fallahpoor.releasetracker.common.GITHUB_BASE_URL
import ir.fallahpoor.releasetracker.common.SPACE_NORMAL
import ir.fallahpoor.releasetracker.common.SPACE_SMALL
import ir.fallahpoor.releasetracker.theme.ReleaseTrackerTheme

@Composable
fun AddLibraryContent(
    snackbarHostState: SnackbarHostState = SnackbarHostState(),
    state: AddLibraryState,
    libraryName: String,
    onLibraryNameChange: (String) -> Unit,
    libraryUrlPath: String,
    onLibraryUrlPathChange: (String) -> Unit,
    onAddLibraryClick: (String, String) -> Unit,
    onErrorDismissed: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .testTag(AddLibraryTags.CONTENT)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(SPACE_NORMAL.dp)
            ) {
                val focusRequester = remember { FocusRequester() }
                LibraryNameInput(
                    libraryName = libraryName,
                    onLibraryNameChange = onLibraryNameChange,
                    showError = state is AddLibraryState.EmptyLibraryName,
                    onNextClick = { focusRequester.requestFocus() }
                )
                Spacer(modifier = Modifier.height(SPACE_SMALL.dp))
                LibraryUrlInput(
                    libraryUrlPath = libraryUrlPath,
                    onLibraryUrlPathChange = onLibraryUrlPathChange,
                    state = state,
                    onDoneClick = { onAddLibraryClick(libraryName, libraryUrlPath) },
                    focusRequester = focusRequester
                )
            }
            AddLibraryButton(
                state = state,
                onAddLibraryClick = { onAddLibraryClick(libraryName, libraryUrlPath) }
            )
        }
        if (state is AddLibraryState.Error) {
            Snackbar(
                modifier = Modifier.align(Alignment.BottomCenter),
                snackbarHostState = snackbarHostState,
                message = state.message
            ) {
                onErrorDismissed()
            }
        }
        if (state is AddLibraryState.LibraryAdded) {
            Snackbar(
                modifier = Modifier.align(Alignment.BottomCenter),
                snackbarHostState = snackbarHostState,
                message = stringResource(R.string.library_added)
            )
        }
    }
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
private fun AddLibraryButton(state: AddLibraryState, onAddLibraryClick: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Button(
            modifier = Modifier
                .testTag(AddLibraryTags.ADD_LIBRARY_BUTTON)
                .padding(SPACE_NORMAL.dp),
            onClick = onAddLibraryClick,
            enabled = state !is AddLibraryState.InProgress
        ) {
            if (state is AddLibraryState.InProgress) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(30.dp)
                        .testTag(AddLibraryTags.PROGRESS_INDICATOR),
                    strokeWidth = 3.dp
                )
            } else {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag(AddLibraryTags.ADD_LIBRARY_BUTTON_TEXT),
                    text = stringResource(R.string.add_library),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun Snackbar(
    modifier: Modifier = Modifier,
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
    SnackbarHost(
        modifier = modifier,
        hostState = snackbarHostState,
        snackbar = { snackbarData: SnackbarData ->
            Snackbar(snackbarData = snackbarData)
        }
    )
}

@Composable
@Preview
private fun AddLibraryContentPreview() {
    ReleaseTrackerTheme {
        Surface {
            AddLibraryContent(
                state = AddLibraryState.InProgress,
                libraryName = "Coil",
                onLibraryNameChange = {},
                libraryUrlPath = "coil-kt/coil",
                onLibraryUrlPathChange = {},
                onAddLibraryClick = { _, _ -> },
                onErrorDismissed = {}
            )
        }
    }
}