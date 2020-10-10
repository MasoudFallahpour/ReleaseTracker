package ir.fallahpoor.releasetracker.libraries.view

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ActionMode
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.selection.SelectionPredicates
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.StorageStrategy
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import ir.fallahpoor.releasetracker.R
import ir.fallahpoor.releasetracker.common.ViewState
import ir.fallahpoor.releasetracker.data.entity.Library
import ir.fallahpoor.releasetracker.libraries.view.dialogs.DeleteDialog
import ir.fallahpoor.releasetracker.libraries.view.dialogs.SortDialog
import ir.fallahpoor.releasetracker.libraries.view.selection.LibrariesItemDetailsLookup
import ir.fallahpoor.releasetracker.libraries.view.selection.LibrariesItemKeyProvider
import ir.fallahpoor.releasetracker.libraries.viewmodel.LibrariesViewModel
import kotlinx.android.synthetic.main.fragment_libraries.*

@AndroidEntryPoint
class LibrariesFragment : Fragment() {

    companion object {
        private const val KEY_ACTION_MODE_ENABLED = "action_mode_enabled"
    }

    private val librariesViewModel: LibrariesViewModel by viewModels()
    private lateinit var selectionTracker: SelectionTracker<String>
    private lateinit var librariesAdapter: LibrariesAdapter
    private var currentOrder: SortDialog.Order? = null
    private var actionMode: ActionMode? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_libraries, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupViews()
        observeViewModel()
        librariesViewModel.getLibraries()
        librariesViewModel.getLastUpdateCheck()
    }

    private fun setupViews() {
        addLibraryButton.setOnClickListener {
            actionMode?.finish()
            findNavController().navigate(R.id.action_librariesFragment_to_addLibraryFragment)
        }
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        setupAdapter()
        with(librariesRecyclerView) {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            adapter = librariesAdapter
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        }
        selectionTracker = createSelectionTracker()
        librariesAdapter.selectionTracker = selectionTracker
    }

    private fun setupAdapter() {
        librariesAdapter = LibrariesAdapter(
            pinClickListener = { library: Library, pinned: Boolean ->
                librariesViewModel.setPinned(library, pinned)
            }
        )
    }

    private fun createSelectionTracker(): SelectionTracker<String> {

        val selectionTracker = SelectionTracker.Builder(
            "LibrariesSelectionTracker",
            librariesRecyclerView,
            LibrariesItemKeyProvider(librariesAdapter),
            LibrariesItemDetailsLookup(librariesRecyclerView),
            StorageStrategy.createStringStorage()
        ).withSelectionPredicate(
            SelectionPredicates.createSelectAnything()
        ).build()

        selectionTracker.addObserver(object : SelectionTracker.SelectionObserver<Long>() {
            override fun onSelectionChanged() {
                val items = selectionTracker.selection
                if (items.isEmpty) {
                    actionMode?.finish()
                } else if (items.size() == 1) {
                    startActionMode()
                }
            }
        })

        return selectionTracker

    }

    private fun observeViewModel() {
        librariesViewModel.librariesViewState
            .observe(viewLifecycleOwner) { viewState: ViewState<List<Library>> ->
                when (viewState) {
                    is ViewState.LoadingState -> showPrimaryLoading()
                    is ViewState.DataLoadedState -> handleLibrariesLoadedState(viewState)
                    is ViewState.ErrorState -> handleErrorState(viewState)
                }
            }
        librariesViewModel.pinViewState
            .observe(viewLifecycleOwner) { viewState: ViewState<Unit> ->
                when (viewState) {
                    is ViewState.LoadingState -> showSecondaryLoading()
                    is ViewState.DataLoadedState -> hideSecondaryLoading()
                    is ViewState.ErrorState -> handleErrorState(viewState)
                }
            }
        librariesViewModel.deleteViewState
            .observe(viewLifecycleOwner) { viewState: ViewState<Unit> ->
                when (viewState) {
                    is ViewState.LoadingState -> showSecondaryLoading()
                    is ViewState.DataLoadedState -> handleLibrariesDeletedState()
                    is ViewState.ErrorState -> handleErrorState(viewState)
                }
            }
        librariesViewModel.lastUpdateCheckViewState
            .observe(viewLifecycleOwner) { viewState: ViewState<String> ->
                if (viewState is ViewState.DataLoadedState) {
                    handleUpdateDateLoadedState(viewState)
                }
            }
    }

    private fun showPrimaryLoading() {
        librariesPrimaryProgressBar.isVisible = true
    }

    private fun hideSecondaryLoading() {
        librariesSecondaryProgressBar.isVisible = false
    }

    private fun handleLibrariesLoadedState(viewState: ViewState.DataLoadedState<List<Library>>) {
        hidePrimaryLoading()
        librariesAdapter.submitList(viewState.data)
    }

    private fun <T> handleErrorState(viewState: ViewState.ErrorState<T>) {
        hidePrimaryLoading()
        showSnackbar(viewState.errorMessage)
    }

    private fun showSecondaryLoading() {
        librariesSecondaryProgressBar.isVisible = true
    }

    private fun hidePrimaryLoading() {
        librariesPrimaryProgressBar.isVisible = false
    }

    private fun handleLibrariesDeletedState() {
        hideSecondaryLoading()
        showSnackbar(getString(R.string.libraries_deleted))
    }

    private fun handleUpdateDateLoadedState(viewState: ViewState.DataLoadedState<String>) {
        val lastCheckDate = viewState.data
        lastCheckTextView.text = getString(R.string.last_check_for_updates, lastCheckDate)
    }

    private fun showSnackbar(message: String) {
        Snackbar.make(
            addLibraryButton,
            message,
            Snackbar.LENGTH_LONG
        ).setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE)
            .show()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        saveState(outState)
    }

    private fun saveState(outState: Bundle) {
        selectionTracker.onSaveInstanceState(outState)
        outState.putBoolean(KEY_ACTION_MODE_ENABLED, actionMode != null)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        restoreState(savedInstanceState)
    }

    private fun restoreState(savedInstanceState: Bundle?) {
        selectionTracker.onRestoreInstanceState(savedInstanceState)
        savedInstanceState?.let {
            val actionModeEnabled = it.getBoolean(KEY_ACTION_MODE_ENABLED)
            if (actionModeEnabled) {
                startActionMode()
            }
        }
    }

    private fun startActionMode() {
        if (actionMode == null) {
            (activity as? AppCompatActivity)?.startSupportActionMode(ActionModeCallback())
        }
    }

    override fun onResume() {
        super.onResume()
        restoreDeleteDialogIfPresent()
        restoreSortDialogIfPresent()
    }

    private fun restoreDeleteDialogIfPresent() {
        val deleteDialog = requireActivity()
            .supportFragmentManager
            .findFragmentByTag(DeleteDialog.TAG)
        deleteDialog?.let {
            (it as DeleteDialog).setListener(DeleteListener())
        }
    }

    private fun restoreSortDialogIfPresent() {
        val sortDialog = requireActivity()
            .supportFragmentManager
            .findFragmentByTag(SortDialog.TAG)
        sortDialog?.let {
            (it as SortDialog).setListener(SortListener())
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_libraries, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == R.id.action_sort) {
            showSortingOrderSelectionDialog()
            true
        } else {
            false
        }
    }

    private fun showSortingOrderSelectionDialog() {
        val dialogFragment = SortDialog()
        dialogFragment.setListener(SortListener())
        showDialogFragment(dialogFragment, SortDialog.TAG)
    }

    inner class ActionModeCallback : ActionMode.Callback {

        override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
            actionMode = mode
            mode.menuInflater.inflate(R.menu.menu_libraries_context, menu)
            return true
        }

        override fun onPrepareActionMode(mode: ActionMode, menu: Menu) = false

        override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {
            return if (item.itemId == R.id.action_delete) {
                showDeleteConfirmationDialog()
                true
            } else {
                false
            }
        }

        override fun onDestroyActionMode(mode: ActionMode) {
            selectionTracker.clearSelection()
            actionMode = null
        }

    }

    private fun showDeleteConfirmationDialog() {
        val dialogFragment = DeleteDialog()
        dialogFragment.setListener(DeleteListener())
        showDialogFragment(dialogFragment, DeleteDialog.TAG)
    }

    private fun showDialogFragment(dialogFragment: DialogFragment?, tag: String) {
        dialogFragment?.show(requireActivity().supportFragmentManager, tag)
    }

    private inner class DeleteListener : DeleteDialog.DeleteListener {

        override fun cancelClicked(dialogFragment: DialogFragment) {
            dialogFragment.dismiss()
        }

        override fun deleteClicked(dialogFragment: DialogFragment) {
            val libraryNames = selectionTracker.selection.map {
                it
            }
            librariesViewModel.deleteLibraries(libraryNames)
            actionMode?.finish()
            dialogFragment.dismiss()
        }

    }

    private inner class SortListener : SortDialog.SortListener {

        override fun orderSelected(order: SortDialog.Order) {
            if (order != currentOrder) {
                currentOrder = order
                librariesViewModel.getLibraries(getSortingOrder(order))
            }
        }

    }

    private fun getSortingOrder(order: SortDialog.Order): LibrariesViewModel.Order {
        return when (order) {
            SortDialog.Order.A_TO_Z -> LibrariesViewModel.Order.A_TO_Z
            SortDialog.Order.Z_TO_A -> LibrariesViewModel.Order.Z_TO_A
            SortDialog.Order.PINNED_FIRST -> LibrariesViewModel.Order.PINNED_FIRST
        }
    }

}
