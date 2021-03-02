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
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import ir.fallahpoor.releasetracker.R
import ir.fallahpoor.releasetracker.addlibrary.viewmodel.AddLibraryViewModel
import ir.fallahpoor.releasetracker.common.SPACE_NORMAL
import ir.fallahpoor.releasetracker.common.SPACE_SMALL
import ir.fallahpoor.releasetracker.common.composables.DefaultSnackbar
import ir.fallahpoor.releasetracker.theme.ReleaseTrackerTheme
import kotlinx.coroutines.launch

// TODO: 'library name' TextField should have a prefix with text 'https://github.com/'.

@ExperimentalAnimationApi
@Composable
fun AddLibraryScreen(
    addLibraryViewModel: AddLibraryViewModel,
    isDarkTheme: Boolean,
    onBackClick: () -> Unit
) {
    ReleaseTrackerTheme(darkTheme = isDarkTheme) {
        val scaffoldState = rememberScaffoldState()
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(text = stringResource(R.string.add_library))
                    },
                    navigationIcon = {
                        BackButton(
                            onBackClick = onBackClick
                        )
                    }
                )
            },
            scaffoldState = scaffoldState,
            snackbarHost = {
                scaffoldState.snackbarHostState
            }
        ) {
            AddLibraryContent(
                scaffoldState = scaffoldState,
                addLibraryViewModel = addLibraryViewModel
            )
        }
    }
}

@Composable
private fun BackButton(onBackClick: () -> Unit) {
    IconButton(
        onClick = onBackClick
    ) {
        Icon(
            imageVector = Icons.Filled.ArrowBack,
            contentDescription = null
        )
    }
}

@ExperimentalAnimationApi
@Composable
private fun AddLibraryContent(
    scaffoldState: ScaffoldState,
    addLibraryViewModel: AddLibraryViewModel
) {

    val state: State by addLibraryViewModel.state.observeAsState(State.Fresh)

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        if (state is State.Loading) {
            ProgressIndicator()
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(SPACE_NORMAL.dp)
        ) {
            LibraryNameTextField(
                libraryName = addLibraryViewModel.libraryName,
                isError = state is State.EmptyLibraryName
            )
            LibraryNameErrorText(
                show = state is State.EmptyLibraryName
            )
            Spacer(
                modifier = Modifier.height(SPACE_SMALL.dp)
            )
            LibraryUrlTextField(
                libraryUrl = addLibraryViewModel.libraryUrl,
                isError = state is State.EmptyLibraryUrl || state is State.InvalidLibraryUrl,
                onDoneClick = {
                    addLibraryViewModel.addLibrary()
                }
            )
            LibraryUrlErrorText(
                state = state
            )
        }
        AddLibraryButton(
            isEnabled = state !is State.Loading,
            clickListener = {
                addLibraryViewModel.addLibrary()
            }
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
            .fillMaxWidth()
            .height(2.dp)
    )
}

@Composable
private fun LibraryNameTextField(libraryName: MutableState<String>, isError: Boolean) {
    val focusManager = LocalFocusManager.current
    TextFieldWithHint(
        text = libraryName.value,
        hint = stringResource(R.string.library_name),
        imeAction = ImeAction.Next,
        keyboardActions = KeyboardActions(
            onDone = {
                focusManager.moveFocus(focusDirection = FocusDirection.Down)
            }
        ),
        onTextChange = { libraryName.value = it },
        modifier = Modifier.fillMaxWidth(),
        isError = isError
    )
}

@ExperimentalAnimationApi
@Composable
private fun LibraryNameErrorText(show: Boolean) {
    AnimatedVisibility(
        visible = show
    ) {
        ErrorText(R.string.library_name_empty)
    }
}

@Composable
private fun LibraryUrlTextField(
    libraryUrl: MutableState<String>,
    isError: Boolean,
    onDoneClick: () -> Unit
) {
    val focusManager = LocalFocusManager.current
    TextFieldWithHint(
        text = libraryUrl.value,
        hint = stringResource(R.string.library_url),
        onTextChange = { libraryUrl.value = it },
        imeAction = ImeAction.Done,
        keyboardActions = KeyboardActions(
            onDone = {
                onDoneClick()
                focusManager.clearFocus()
            }
        ),
        modifier = Modifier.fillMaxWidth(),
        isError = isError
    )
}

@ExperimentalAnimationApi
@Composable
private fun LibraryUrlErrorText(state: State) {
    AnimatedVisibility(
        visible = state is State.EmptyLibraryUrl
    ) {
        ErrorText(R.string.library_url_empty)
    }
    AnimatedVisibility(
        visible = state is State.InvalidLibraryUrl
    ) {
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
        modifier = Modifier.padding(SPACE_NORMAL.dp),
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
private fun Snackbar(state: State, scaffoldState: ScaffoldState) {

    val coroutineScope = rememberCoroutineScope()

    if (state is State.Error) {
        val errorMessage = state.message
        coroutineScope.launch {
            scaffoldState.snackbarHostState.showSnackbar(message = errorMessage)
        }
    }

    if (state is State.LibraryAdded) {
        val message = stringResource(R.string.library_added)
        coroutineScope.launch {
            scaffoldState.snackbarHostState.showSnackbar(message = message)
        }
    }

    DefaultSnackbar(
        snackbarHostState = scaffoldState.snackbarHostState
    )

}

@Composable
private fun TextFieldWithHint(
    text: String,
    hint: String,
    onTextChange: (String) -> Unit,
    imeAction: ImeAction,
    keyboardActions: KeyboardActions = KeyboardActions(),
    isError: Boolean,
    modifier: Modifier
) {
    OutlinedTextField(
        value = text,
        label = {
            Text(text = hint)
        },
        onValueChange = onTextChange,
        keyboardOptions = KeyboardOptions(
            imeAction = imeAction
        ),
        keyboardActions = keyboardActions,
        singleLine = true,
        isError = isError,
        modifier = modifier
    )
}