package ir.fallahpoor.releasetracker.libraries.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import ir.fallahpoor.releasetracker.R
import ir.fallahpoor.releasetracker.data.NightMode
import ir.fallahpoor.releasetracker.data.SortOrder

private enum class ToolbarMode {
    Normal,
    Search
}

@Composable
fun Toolbar(
    modifier: Modifier = Modifier,
    currentSortOrder: SortOrder,
    onSortOrderChange: (SortOrder) -> Unit,
    isNightModeSupported: Boolean,
    currentNightMode: NightMode,
    onNightModeChange: (NightMode) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onSearchQuerySubmit: (String) -> Unit
) {
    TopAppBar(modifier = modifier) {
        var toolbarMode by rememberSaveable { mutableStateOf(ToolbarMode.Normal) }
        Box(contentAlignment = Alignment.BottomCenter) {
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
                SearchButton {
                    toolbarMode = ToolbarMode.Search
                }
                if (isNightModeSupported) {
                    NightModeButton(
                        currentNightMode = currentNightMode,
                        onNightModeChange = onNightModeChange
                    )
                }
            }
            androidx.compose.animation.AnimatedVisibility(visible = toolbarMode == ToolbarMode.Search) {
                var searchQuery by rememberSaveable { mutableStateOf("") }
                SearchBar(
                    modifier = Modifier.fillMaxWidth(),
                    hint = stringResource(R.string.search),
                    query = searchQuery,
                    onQueryChange = {
                        searchQuery = it
                        onSearchQueryChange(it)
                    },
                    onQuerySubmit = onSearchQuerySubmit,
                    onClearClick = {
                        searchQuery = ""
                        onSearchQueryChange("")
                    },
                    onCloseClick = {
                        toolbarMode = ToolbarMode.Normal
                        searchQuery = ""
                        onSearchQueryChange("")
                    }
                )
            }
        }
    }
}

@Composable
private fun SortOrderButton(currentSortOrder: SortOrder, onSortOrderChange: (SortOrder) -> Unit) {
    var showSortOrderDialog by rememberSaveable { mutableStateOf(false) }
    IconButton(onClick = { showSortOrderDialog = true }) {
        Icon(
            painter = painterResource(R.drawable.ic_sort),
            contentDescription = stringResource(R.string.sort)
        )
    }
    if (showSortOrderDialog) {
        SingleSelectionDialog(
            title = stringResource(R.string.select_sort_order),
            items = SortOrder.values().toList(),
            labels = SortOrder.values().toList().map { stringResource(it.label) },
            selectedItem = currentSortOrder,
            onItemSelect = { sortOrder: SortOrder ->
                showSortOrderDialog = false
                onSortOrderChange(sortOrder)
            },
            onDismiss = { showSortOrderDialog = false }
        )
    }
}

@Composable
private fun SearchButton(onClick: () -> Unit) {
    IconButton(onClick = onClick) {
        Icon(
            imageVector = Icons.Filled.Search,
            contentDescription = stringResource(R.string.search)
        )
    }
}

@Composable
private fun NightModeButton(
    currentNightMode: NightMode,
    onNightModeChange: (NightMode) -> Unit
) {
    var showDropdownMenu by remember { mutableStateOf(false) }
    var showNightModeDialog by rememberSaveable { mutableStateOf(false) }
    Box {
        IconButton(onClick = { showDropdownMenu = !showDropdownMenu }) {
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
                Text(text = stringResource(R.string.night_mode))
            }
        }
    }
    if (showNightModeDialog) {
        SingleSelectionDialog(
            title = stringResource(R.string.select_night_mode),
            items = NightMode.values().toList(),
            labels = NightMode.values().toList().map { stringResource(it.label) },
            selectedItem = currentNightMode,
            onItemSelect = { nightMode: NightMode ->
                showNightModeDialog = false
                onNightModeChange(nightMode)
            },
            onDismiss = { showNightModeDialog = false }
        )
    }
}

@Preview
@Composable
private fun ToolbarPreview() {
    Toolbar(
        currentSortOrder = SortOrder.A_TO_Z,
        onSortOrderChange = {},
        isNightModeSupported = true,
        currentNightMode = NightMode.AUTO,
        onNightModeChange = {},
        onSearchQueryChange = {},
        onSearchQuerySubmit = {}
    )
}