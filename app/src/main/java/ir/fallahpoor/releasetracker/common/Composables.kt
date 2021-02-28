package ir.fallahpoor.releasetracker.common

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import ir.fallahpoor.releasetracker.theme.ReleaseTrackerTheme

@Composable
fun DefaultSnackbar(
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier
) {
    SnackbarHost(
        hostState = snackbarHostState,
        snackbar = { snackbarData: SnackbarData ->
            Snackbar(
                snackbarData = snackbarData
            )
        },
        modifier = modifier
    )
}

@Composable
fun SearchBar(
    modifier: Modifier = Modifier,
    hint: String,
    query: String,
    onQueryChange: (String) -> Unit,
    onQuerySubmit: (String) -> Unit,
    onClearClick: () -> Unit,
) {
    TextField(
        modifier = modifier,
        value = query,
        onValueChange = onQueryChange,
        placeholder = {
            Text(
                text = hint
            )
        },
        singleLine = true,
        leadingIcon = {
            Icon(
                imageVector = Icons.Filled.Search,
                contentDescription = null
            )
        },
        trailingIcon = {
            IconButton(
                onClick = onClearClick
            ) {
                Icon(
                    imageVector = Icons.Filled.Clear,
                    contentDescription = null
                )
            }
        },
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(
            onSearch = {
                onQuerySubmit(query)
            }
        )
    )
}

@Composable
@Preview
private fun SearchBarPreview() {
    ReleaseTrackerTheme {
        Surface {
            SearchBar(
                hint = "Search",
                query = "Coil",
                onQueryChange = {},
                onClearClick = {},
                onQuerySubmit = {}
            )
        }
    }
}

//SearchBar(
//modifier = Modifier.fillMaxWidth(),
//hint = stringResource(R.string.search),
//query = searchQuery,
//onQueryChange = {
//    searchQuery = it
//    librariesViewModel.getLibraries(searchTerm = it)
//},
//onQuerySubmit = {
//    librariesViewModel.getLibraries(searchTerm = it)
//},
//onClearClick = {
//    searchQuery = ""
//    librariesViewModel.getLibraries(searchTerm = "")
//}
//)