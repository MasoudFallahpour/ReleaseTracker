package ir.fallahpoor.releasetracker.common.composables

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sort
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import ir.fallahpoor.releasetracker.R
import ir.fallahpoor.releasetracker.common.NightModeManager
import ir.fallahpoor.releasetracker.libraries.view.dialogs.NightModeDialog
import ir.fallahpoor.releasetracker.libraries.view.dialogs.SortOrder
import ir.fallahpoor.releasetracker.libraries.view.dialogs.SortOrderDialog

enum class ToolbarMode {
    Normal,
    Search
}

@ExperimentalAnimationApi
@Composable
fun Toolbar(
    toolbarMode: ToolbarMode,
    onToolbarModeChange: (ToolbarMode) -> Unit,
    currentSortOrder: SortOrder,
    onSortOrderChange: (SortOrder) -> Unit,
    isNightModeSupported: Boolean,
    currentNightMode: NightModeManager.Mode,
    onNightModeChange: (NightModeManager.Mode) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onSearchQuerySubmit: (String) -> Unit,
    onSearchQueryClear: () -> Unit
) {
    TopAppBar {

        var searchQuery by rememberSaveable { mutableStateOf("") }

        Box(
            contentAlignment = Alignment.BottomCenter
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier.weight(1f),
                    text = stringResource(R.string.app_name),
                    style = MaterialTheme.typography.h6
                )
                SortOrderButton(
                    currentSortOrder = currentSortOrder,
                    onSortOrderChange = onSortOrderChange
                )
                SearchButton(
                    onClick = {
                        onToolbarModeChange(ToolbarMode.Search)
                    }
                )
                if (isNightModeSupported) {
                    NightModeButton(
                        currentNightMode = currentNightMode,
                        onNightModeChange = onNightModeChange
                    )
                }
            }
            androidx.compose.animation.AnimatedVisibility(
                visible = toolbarMode == ToolbarMode.Search
            ) {
                SearchBar(
                    modifier = Modifier
                        .fillMaxWidth(),
                    hint = stringResource(R.string.search),
                    query = searchQuery,
                    onQueryChange = {
                        searchQuery = it
                        onSearchQueryChange(it)
                    },
                    onQuerySubmit = onSearchQuerySubmit,
                    onClearClick = {
                        searchQuery = ""
                        onSearchQueryClear()
                    },
                    onCloseClick = {
                        onToolbarModeChange(ToolbarMode.Normal)
                        searchQuery = ""
                    }
                )
            }
        }
    }
}

@Composable
private fun SortOrderButton(currentSortOrder: SortOrder, onSortOrderChange: (SortOrder) -> Unit) {

    var showSortOrderDialog by rememberSaveable { mutableStateOf(false) }

    IconButton(
        onClick = {
            showSortOrderDialog = true
        }
    ) {
        Icon(
            imageVector = Icons.Filled.Sort,
            contentDescription = stringResource(R.string.sort)
        )
    }
    if (showSortOrderDialog) {
        SortOrderDialog(
            currentSortOrder = currentSortOrder,
            onSortOrderClick = { sortOrder: SortOrder ->
                showSortOrderDialog = false
                onSortOrderChange(sortOrder)
            },
            onDismiss = {
                showSortOrderDialog = false
            }
        )
    }

}

@Composable
private fun SearchButton(onClick: () -> Unit) {
    IconButton(
        onClick = onClick
    ) {
        Icon(
            imageVector = Icons.Filled.Search,
            contentDescription = stringResource(R.string.search)
        )
    }
}

@Composable
private fun NightModeButton(
    currentNightMode: NightModeManager.Mode,
    onNightModeChange: (NightModeManager.Mode) -> Unit
) {

    var showDropdownMenu by remember { mutableStateOf(false) }
    var showNightModeDialog by rememberSaveable { mutableStateOf(false) }

    IconButton(
        onClick = {
            showDropdownMenu = !showDropdownMenu
        }
    ) {
        Icon(
            imageVector = Icons.Default.MoreVert,
            contentDescription = stringResource(R.string.more_options)
        )
    }

    DropdownMenu(
        expanded = showDropdownMenu,
        onDismissRequest = { showDropdownMenu = false })
    {
        DropdownMenuItem(
            onClick = {
                showDropdownMenu = false
                showNightModeDialog = true
            }
        ) {
            Text(
                text = stringResource(R.string.night_mode)
            )
        }
    }

    if (showNightModeDialog) {
        NightModeDialog(
            defaultNightMode = currentNightMode,
            onNightModeClick = { nightMode: NightModeManager.Mode ->
                onNightModeChange(nightMode)
                showNightModeDialog = false
            },
            onDismiss = {
                showNightModeDialog = false
            }
        )
    }

}