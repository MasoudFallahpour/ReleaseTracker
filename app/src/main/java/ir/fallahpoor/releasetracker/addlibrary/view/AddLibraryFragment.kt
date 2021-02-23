package ir.fallahpoor.releasetracker.addlibrary.view

import androidx.annotation.StringRes
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
import ir.fallahpoor.releasetracker.common.NightModeManager
import ir.fallahpoor.releasetracker.common.SPACE_NORMAL
import ir.fallahpoor.releasetracker.common.SPACE_SMALL
import ir.fallahpoor.releasetracker.theme.ReleaseTrackerTheme
import kotlinx.coroutines.launch

@ExperimentalMaterialApi
@Composable
fun AddLibraryScreen(
    navController: NavController,
    nightModeManager: NightModeManager,
    addLibraryViewModel: AddLibraryViewModel
) {

    ReleaseTrackerTheme(darkTheme = nightModeManager.isDarkTheme()) {
        val scaffoldState = rememberScaffoldState()
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(text = stringResource(R.string.add_library))
                    },
                    navigationIcon = {
                        BackButton(navController)
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
private fun BackButton(navController: NavController) {
    IconButton(
        onClick = {
            navController.popBackStack()
        }
    ) {
        Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = null)
    }
}

@Composable
private fun AddLibraryContent(
    scaffoldState: ScaffoldState,
    addLibraryViewModel: AddLibraryViewModel
) {

    val state: State by addLibraryViewModel.state.observeAsState(State.Fresh)

    Column(
        modifier = Modifier
            .fillMaxHeight()
    ) {
        if (state is State.Loading) {
            ProgressIndicator()
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(SPACE_NORMAL.dp)
        ) {
            LibraryNameTextField(addLibraryViewModel.libraryName, state)
            if (state is State.EmptyLibraryName) {
                ErrorText(R.string.library_name_empty)
            }
            Spacer(modifier = Modifier.height(SPACE_SMALL.dp))
            LibraryUrlTextField(addLibraryViewModel.libraryUrl, state)
            if (state is State.EmptyLibraryUrl) {
                ErrorText(R.string.library_url_empty)
            } else if (state is State.InvalidLibraryUrl) {
                ErrorText(R.string.library_url_invalid)
            }
        }
        AddLibraryButton(state) {
            addLibraryViewModel.addLibrary()
        }
        val coroutineScope = rememberCoroutineScope()
        if (state is State.Error) {
            val errorMessage = (state as State.Error).message
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
private fun LibraryNameTextField(libraryName: MutableState<String>, state: State) {
    TextFieldWithHint(
        text = libraryName.value,
        hint = stringResource(R.string.library_name),
        imeAction = ImeAction.Next,
        onTextChange = { libraryName.value = it },
        modifier = Modifier.fillMaxWidth(),
        isErrorValue = state is State.EmptyLibraryName
    )
}

@Composable
private fun LibraryUrlTextField(libraryUrl: MutableState<String>, state: State) {
    TextFieldWithHint(
        text = libraryUrl.value,
        hint = stringResource(R.string.library_url),
        onTextChange = { libraryUrl.value = it },
        imeAction = ImeAction.Done,
        modifier = Modifier.fillMaxWidth(),
        isErrorValue = state is State.EmptyLibraryUrl || state is State.InvalidLibraryUrl
    )
}

@Composable
private fun ErrorText(@StringRes textRes: Int) {
    Text(text = stringResource(textRes), style = TextStyle(color = MaterialTheme.colors.error))
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
private fun TextFieldWithHint(
    text: String,
    hint: String,
    onTextChange: (String) -> Unit,
    imeAction: ImeAction,
    isErrorValue: Boolean,
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
        isErrorValue = isErrorValue,
        modifier = modifier
    )
}