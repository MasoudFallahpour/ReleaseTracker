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
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ir.fallahpoor.releasetracker.R
import ir.fallahpoor.releasetracker.common.SPACE_NORMAL
import ir.fallahpoor.releasetracker.common.SPACE_SMALL
import ir.fallahpoor.releasetracker.common.composables.DefaultSnackbar
import ir.fallahpoor.releasetracker.common.composables.OutlinedTextFieldWithPrefix
import ir.fallahpoor.releasetracker.theme.ReleaseTrackerTheme

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun AddLibraryScreen(
    isDarkTheme: Boolean,
    addLibraryState: AddLibraryState,
    libraryName: String,
    onLibraryNameChange: (String) -> Unit,
    libraryUrlPath: String,
    onLibraryUrlPathChange: (String) -> Unit,
    onBackClick: () -> Unit,
    onAddLibrary: () -> Unit,
    scaffoldState: ScaffoldState = rememberScaffoldState()
) {
    ReleaseTrackerTheme(darkTheme = isDarkTheme) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(text = stringResource(R.string.add_library))
                    },
                    navigationIcon = {
                        BackButton(onBackClick = onBackClick)
                    }
                )
            },
            scaffoldState = scaffoldState,
            snackbarHost = {
                scaffoldState.snackbarHostState
            }
        ) {

            val keyboardController: SoftwareKeyboardController? =
                LocalSoftwareKeyboardController.current
            AddLibraryContent(
                state = addLibraryState,
                scaffoldState = scaffoldState,
                libraryName = libraryName,
                onLibraryNameChange = onLibraryNameChange,
                libraryUrlPath = libraryUrlPath,
                onLibraryUrlPathChange = onLibraryUrlPathChange,
                onAddLibrary = {
                    onAddLibrary()
                    keyboardController?.hide()
                }
            )
        }
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
    val tag = stringResource(R.string.test_tag_add_library_progress_indicator)
    LinearProgressIndicator(
        modifier = Modifier
            .semantics { testTag = tag }
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
    val tag = stringResource(R.string.test_tag_add_library_library_name_text_field)
    OutlinedTextField(
        modifier = Modifier
            .semantics { testTag = tag }
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
    val tag = stringResource(R.string.test_tag_add_library_library_url_text_field)
    OutlinedTextFieldWithPrefix(
        modifier = Modifier
            .semantics { testTag = tag }
            .fillMaxWidth(),
        prefix = stringResource(R.string.github_base_url),
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
    val tag = stringResource(R.string.test_tag_add_library_add_library_button)
    Button(
        modifier = Modifier
            .semantics { testTag = tag }
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