package ir.fallahpoor.releasetracker.addlibrary.view

import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import ir.fallahpoor.releasetracker.R
import ir.fallahpoor.releasetracker.addlibrary.viewmodel.AddLibraryViewModel
import ir.fallahpoor.releasetracker.common.DefaultSnackbar
import ir.fallahpoor.releasetracker.common.SPACE_NORMAL
import ir.fallahpoor.releasetracker.common.SPACE_SMALL
import ir.fallahpoor.releasetracker.theme.ReleaseTrackerTheme
import kotlinx.coroutines.launch

// TODO: When 'library name' TextField has focus, clicking the 'next' button of the keyboard should
//  move the focus to the 'library URL' TextField.
// TODO When 'library URL' TextField has focus, clicking the 'done' button of the keyboard should
//  add the library to the database and close the keyboard.
// TODO: 'library name' TextField should have a prefix with text 'https://github.com/'.

@ExperimentalAnimationApi
@ExperimentalMaterialApi
@Composable
fun AddLibraryScreen(
    addLibraryViewModel: AddLibraryViewModel,
    navController: NavController,
    isDarkTheme: Boolean
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
                        BackButton {
                            navController.popBackStack()
                        }
                    }
                )
            },
            scaffoldState = scaffoldState,
            snackbarHost = {
                scaffoldState.snackbarHostState
            }
        ) {
            AddLibraryContent(scaffoldState, addLibraryViewModel)
        }
    }
}

@Composable
private fun BackButton(onBackClicked: () -> Unit) {
    IconButton(
        onClick = onBackClicked
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

    Column(modifier = Modifier.fillMaxHeight()) {
        ProgressIndicator(state)
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(SPACE_NORMAL.dp)
        ) {
            LibraryNameTextField(addLibraryViewModel.libraryName, state)
            LibraryNameErrorText(state)
            Spacer(modifier = Modifier.height(SPACE_SMALL.dp))
            LibraryUrlTextField(addLibraryViewModel.libraryUrl, state)
            LibraryUrlErrorText(state)
        }
        AddLibraryButton(state) {
            addLibraryViewModel.addLibrary()
        }
        Snackbar(state, scaffoldState)
    }

}

@Composable
private fun ProgressIndicator(state: State) {
    if (state is State.Loading) {
        LinearProgressIndicator(
            modifier = Modifier
                .fillMaxWidth()
                .height(2.dp)
        )
    }
}

@Composable
private fun LibraryNameTextField(libraryName: MutableState<String>, state: State) {
    TextFieldWithHint(
        text = libraryName.value,
        hint = stringResource(R.string.library_name),
        imeAction = ImeAction.Next,
        onTextChange = { libraryName.value = it },
        modifier = Modifier.fillMaxWidth(),
        isError = state is State.EmptyLibraryName
    )
}

@ExperimentalAnimationApi
@Composable
private fun LibraryNameErrorText(state: State) {
    AnimatedVisibility(visible = state is State.EmptyLibraryName) {
        ErrorText(R.string.library_name_empty)
    }
}

@Composable
private fun LibraryUrlTextField(libraryUrl: MutableState<String>, state: State) {
    TextFieldWithHint(
        text = libraryUrl.value,
        hint = stringResource(R.string.library_url),
        onTextChange = { libraryUrl.value = it },
        imeAction = ImeAction.Done,
        modifier = Modifier.fillMaxWidth(),
        isError = state is State.EmptyLibraryUrl || state is State.InvalidLibraryUrl
    )
}

@ExperimentalAnimationApi
@Composable
private fun LibraryUrlErrorText(state: State) {
    AnimatedVisibility(visible = state is State.EmptyLibraryUrl) {
        ErrorText(R.string.library_url_empty)
    }
    AnimatedVisibility(visible = state is State.InvalidLibraryUrl) {
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
private fun AddLibraryButton(state: State, clickListener: () -> Unit) {
    Button(
        modifier = Modifier.padding(SPACE_NORMAL.dp),
        onClick = clickListener,
        enabled = state !is State.Loading
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
            keyboardType = KeyboardType.Text,
            imeAction = imeAction
        ),
        singleLine = true,
        isError = isError,
        modifier = modifier
    )
}