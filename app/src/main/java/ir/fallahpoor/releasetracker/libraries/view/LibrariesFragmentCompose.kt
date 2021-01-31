package ir.fallahpoor.releasetracker.libraries.view

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import ir.fallahpoor.releasetracker.R
import ir.fallahpoor.releasetracker.common.NightModeManager
import ir.fallahpoor.releasetracker.common.SPACE_NORMAL
import ir.fallahpoor.releasetracker.common.SnackbarState
import ir.fallahpoor.releasetracker.data.entity.Library
import ir.fallahpoor.releasetracker.libraries.view.dialogs.DeleteLibraryDialog
import ir.fallahpoor.releasetracker.libraries.view.dialogs.NightModeDialog
import ir.fallahpoor.releasetracker.libraries.view.dialogs.SortDialog
import ir.fallahpoor.releasetracker.libraries.view.states.LibrariesListState
import ir.fallahpoor.releasetracker.libraries.view.states.LibraryDeleteState
import ir.fallahpoor.releasetracker.libraries.viewmodel.LibrariesViewModel
import ir.fallahpoor.releasetracker.theme.ReleaseTrackerTheme
import javax.inject.Inject

@AndroidEntryPoint
class LibrariesFragmentCompose : Fragment() {

    @Inject
    lateinit var nightModeManager: NightModeManager
    private val librariesViewModel: LibrariesViewModel by viewModels()
    private var currentOrder: SortDialog.Order? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = ComposeView(requireContext()).apply {
        setContent {
            val nightMode by librariesViewModel.nightMode.observeAsState()
            val isNightMode = when (nightMode) {
                NightModeManager.Mode.OFF.name -> false
                NightModeManager.Mode.ON.name -> true
                else -> isSystemInDarkTheme()
            }
            ReleaseTrackerTheme(darkTheme = isNightMode) {
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = {
                                Text(text = stringResource(R.string.app_name))
                            },
                            actions = {
                                ActionButtons()
                            }
                        )
                    },
                    floatingActionButton = {
                        AddLibraryButton()
                    },
                    floatingActionButtonPosition = FabPosition.Center
                ) {
                    LibrariesListScreen()
                }
            }
        }
    }

    @Composable
    private fun ActionButtons() {

        IconButton(onClick = { showSortDialog() }) {
            Icon(imageVector = Icons.Filled.Sort)
        }

        IconButton(onClick = { /*TODO*/ }) {
            Icon(imageVector = Icons.Filled.Search)
        }

        val nightModeSupported = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q

        if (nightModeSupported) {
            var showMenu by remember { mutableStateOf(false) }
            DropdownMenu(
                toggle = {
                    IconButton(onClick = { showMenu = true }) {
                        Icon(Icons.Default.MoreVert)
                    }
                },
                expanded = showMenu,
                onDismissRequest = { showMenu = false })
            {
                DropdownMenuItem(
                    onClick = {
                        showMenu = false
                        showNightModeDialog()
                    }
                ) {
                    Text(text = stringResource(R.string.night_mode))
                }
            }
        }

    }

    private fun showSortDialog() {
        val dialogDialog = SortDialog()
        dialogDialog.setListener(SortListener())
        showDialogFragment(dialogDialog, SortDialog.TAG)
    }

    private inner class SortListener : SortDialog.SortListener {

        override fun orderSelected(order: SortDialog.Order) {
            if (order != currentOrder) {
                currentOrder = order
                librariesViewModel.getLibraries(getSortingOrder(order))
            }
        }

        private fun getSortingOrder(order: SortDialog.Order) = when (order) {
            SortDialog.Order.A_TO_Z -> LibrariesViewModel.Order.A_TO_Z
            SortDialog.Order.Z_TO_A -> LibrariesViewModel.Order.Z_TO_A
            SortDialog.Order.PINNED_FIRST -> LibrariesViewModel.Order.PINNED_FIRST
        }

    }

    private fun showNightModeDialog() {
        val nightModeDialog = NightModeDialog()
        showDialogFragment(nightModeDialog, NightModeDialog.TAG)
    }

    private fun showDialogFragment(dialogFragment: DialogFragment?, tag: String) {
        dialogFragment?.show(requireActivity().supportFragmentManager, tag)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        librariesViewModel.getLibraries()
    }

    @Composable
    private fun LibrariesListScreen() {

        val librariesListState: LibrariesListState by librariesViewModel.librariesListState.observeAsState(
            LibrariesListState.Fresh
        )
        val lastUpdateCheckState by librariesViewModel.lastUpdateCheckState.observeAsState("N/A")
        val libraryDeleteState by librariesViewModel.deleteState.observeAsState(LibraryDeleteState.Fresh)
        val snackbarState = SnackbarState()

        Column(
            modifier = Modifier.fillMaxHeight()
        ) {
            LastUpdateCheckText(lastUpdateCheckState)
            when (librariesListState) {
                is LibrariesListState.Loading -> {
                    CircularProgressIndicator()
                }
                is LibrariesListState.LibrariesLoaded -> {
                    val libraries: List<Library> =
                        (librariesListState as LibrariesListState.LibrariesLoaded).libraries
                    LibrariesList(
                        libraries = libraries,
                        libraryDeleteState = libraryDeleteState,
                        snackbarState = snackbarState,
                        clickListener = { library: Library ->
                            val intent = Intent(Intent.ACTION_VIEW).apply {
                                data = Uri.parse(library.url)
                            }
                            startActivity(intent)
                        },
                        longClickListener = { library: Library ->
                            showDeleteConfirmationDialog(library)
                        },
                        pinClickListener = { library: Library, pin: Boolean ->
                            librariesViewModel.setPinned(library, pin)
                        })
                }
                LibrariesListState.Fresh -> {
                }
            }
        }

    }

    private fun showDeleteConfirmationDialog(library: Library) {
        val dialogFragment = DeleteLibraryDialog()
        dialogFragment.setListener {
            librariesViewModel.deleteLibrary(library.name)
        }
        showDialogFragment(dialogFragment, DeleteLibraryDialog.TAG)
    }

    @Composable
    private fun LastUpdateCheckText(lastUpdateCheck: String) {
        Text(
            text = stringResource(R.string.last_check_for_updates, lastUpdateCheck),
            modifier = Modifier.padding(SPACE_NORMAL.dp)
        )
    }

    @Composable
    private fun LibrariesList(
        libraries: List<Library>,
        libraryDeleteState: LibraryDeleteState,
        snackbarState: SnackbarState,
        clickListener: (Library) -> Unit,
        longClickListener: (Library) -> Unit,
        pinClickListener: (Library, Boolean) -> Unit
    ) {
        if (libraries.isEmpty()) {
            NoLibrariesText()
        } else {
            if (libraryDeleteState is LibraryDeleteState.InProgress) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }
            Box(contentAlignment = Alignment.BottomStart, modifier = Modifier.fillMaxSize()) {
                LazyColumn {
                    itemsIndexed(libraries) { index: Int, library: Library ->
                        LibraryItem(
                            library = library,
                            clickListener = clickListener,
                            longClickListener = longClickListener,
                            pinClickListener = pinClickListener
                        )
                        if (index != libraries.lastIndex) {
                            Divider()
                        }
                    }
                }
                Snackbar(libraryDeleteState, snackbarState)
            }
        }
    }

    @Composable
    private fun NoLibrariesText() {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
            Text(
                text = stringResource(R.string.no_libraries),
                modifier = Modifier.padding(SPACE_NORMAL.dp)
            )
        }
    }

    @Composable
    private fun LibraryItem(
        library: Library,
        clickListener: (Library) -> Unit,
        longClickListener: (Library) -> Unit,
        pinClickListener: (Library, Boolean) -> Unit
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clickable(
                    onClick = {
                        clickListener.invoke(library)
                    },
                    onLongClick = {
                        longClickListener.invoke(library)
                    })
                .padding(
                    end = SPACE_NORMAL.dp,
                    top = SPACE_NORMAL.dp,
                    bottom = SPACE_NORMAL.dp
                )
        ) {
            PinToggleButton(library, pinClickListener)
            Column(modifier = Modifier.weight(1f)) {
                LibraryNameText(library)
                LibraryUrlText(library)
            }
            Text(text = library.version)
        }
    }

    @Composable
    private fun PinToggleButton(library: Library, onCheckedChange: (Library, Boolean) -> Unit) {
        IconToggleButton(
            checked = library.isPinned(),
            onCheckedChange = {
                onCheckedChange.invoke(library, it)
            }
        ) {
            val pinImage = if (library.isPinned()) Icons.Filled.PushPin else Icons.Outlined.PushPin
            Icon(
                imageVector = pinImage,
                tint = MaterialTheme.colors.secondary
            )
        }
    }

    @Composable
    private fun LibraryNameText(library: Library) {
        EllipsisText(text = library.name, style = MaterialTheme.typography.body1)
    }

    @Composable
    private fun LibraryUrlText(library: Library) {
        EllipsisText(
            text = library.url,
            style = MaterialTheme.typography.body2,
            modifier = Modifier.padding(end = SPACE_NORMAL.dp)
        )
    }

    @Composable
    private fun EllipsisText(text: String, style: TextStyle, modifier: Modifier = Modifier) {
        Text(
            text = text,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = style,
            modifier = modifier
        )
    }

    @Composable
    private fun AddLibraryButton() {
        FloatingActionButton(
            onClick = {
                findNavController().navigate(R.id.action_to_addLibraryFragment)
            }
        ) {
            Icon(imageVector = Icons.Filled.Add)
        }
    }

    @Composable
    private fun Snackbar(
        libraryDeleteState: LibraryDeleteState,
        snackbarState: SnackbarState
    ) {
        when (libraryDeleteState) {
            is LibraryDeleteState.Error -> {
                ir.fallahpoor.releasetracker.common.Snackbar(
                    snackbarState,
                    libraryDeleteState.message
                )
            }
            is LibraryDeleteState.Deleted -> {
                ir.fallahpoor.releasetracker.common.Snackbar(
                    snackbarState,
                    stringResource(R.string.library_deleted)
                )
            }
        }
    }

}