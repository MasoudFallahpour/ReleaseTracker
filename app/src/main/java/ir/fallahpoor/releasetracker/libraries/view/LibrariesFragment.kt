package ir.fallahpoor.releasetracker.libraries.view

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ActionMode
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
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
import ir.fallahpoor.releasetracker.databinding.FragmentLibrariesBinding
import ir.fallahpoor.releasetracker.libraries.view.dialogs.DeleteDialog
import ir.fallahpoor.releasetracker.libraries.view.dialogs.NightModeDialog
import ir.fallahpoor.releasetracker.libraries.view.dialogs.SortDialog
import ir.fallahpoor.releasetracker.libraries.view.selection.LibrariesItemDetailsLookup
import ir.fallahpoor.releasetracker.libraries.view.selection.LibrariesItemKeyProvider
import ir.fallahpoor.releasetracker.libraries.viewmodel.LibrariesViewModel

@AndroidEntryPoint
class LibrariesFragment : Fragment() {

    private val librariesViewModel: LibrariesViewModel by viewModels()
    private lateinit var selectionTracker: SelectionTracker<String>
    private lateinit var librariesAdapter: LibrariesAdapter
    private var currentOrder: SortDialog.Order? = null
    private var actionMode: ActionMode? = null
    private var _binding: FragmentLibrariesBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLibrariesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupViews()
        observeViewModel()
        librariesViewModel.getLibraries()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupViews() {
        binding.addLibraryButton.setOnClickListener {
            actionMode?.finish()
            findNavController().navigate(R.id.action_librariesFragment_to_addLibraryFragment)
        }
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        setupAdapter()
        with(binding.librariesRecyclerView) {
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
            clickListener = { library: Library ->
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse(library.url)
                }
                startActivity(intent)
            },
            pinClickListener = { library: Library, pinned: Boolean ->
                librariesViewModel.setPinned(library, pinned)
            }
        )
    }

    private fun createSelectionTracker(): SelectionTracker<String> {

        val selectionTracker = SelectionTracker.Builder(
            "LibrariesSelectionTracker",
            binding.librariesRecyclerView,
            LibrariesItemKeyProvider(librariesAdapter),
            LibrariesItemDetailsLookup(binding.librariesRecyclerView),
            StorageStrategy.createStringStorage()
        ).withSelectionPredicate(
            SelectionPredicates.createSelectAnything()
        ).build()

        selectionTracker.addObserver(object : SelectionTracker.SelectionObserver<Long>() {
            override fun onSelectionChanged() {
                val items = selectionTracker.selection
                when {
                    items.isEmpty -> actionMode?.finish()
                    items.size() == 1 -> {
                        startActionMode()
                        setActionModeTitle(items.size())
                    }
                    else -> setActionModeTitle(items.size())
                }
            }
        })

        return selectionTracker

    }

    private fun setActionModeTitle(numSelectedItems: Int) {
        actionMode?.title = if (numSelectedItems > 0) {
            getString(R.string.selected, numSelectedItems)
        } else {
            ""
        }
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
        binding.librariesPrimaryProgressBar.isVisible = true
    }

    private fun hideSecondaryLoading() {
        binding.librariesSecondaryProgressBar.isVisible = false
    }

    private fun handleLibrariesLoadedState(viewState: ViewState.DataLoadedState<List<Library>>) {
        hidePrimaryLoading()
        val libraries: List<Library> = viewState.data
        librariesAdapter.submitList(libraries)
        binding.noLibrariesTextView.isVisible = libraries.isEmpty()
    }

    private fun <T> handleErrorState(viewState: ViewState.ErrorState<T>) {
        hidePrimaryLoading()
        showSnackbar(viewState.errorMessage)
    }

    private fun showSecondaryLoading() {
        binding.librariesSecondaryProgressBar.isVisible = true
    }

    private fun hidePrimaryLoading() {
        binding.librariesPrimaryProgressBar.isVisible = false
    }

    private fun handleLibrariesDeletedState() {
        hideSecondaryLoading()
        showSnackbar(getString(R.string.libraries_deleted))
    }

    private fun handleUpdateDateLoadedState(viewState: ViewState.DataLoadedState<String>) {
        val lastCheckDate = viewState.data
        binding.lastCheckTextView.text = getString(R.string.last_check_for_updates, lastCheckDate)
    }

    private fun showSnackbar(message: String) {
        Snackbar.make(
            binding.addLibraryButton,
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
        librariesViewModel.isActionModeEnabled = actionMode != null
        librariesViewModel.numSelectedItems = selectionTracker.selection.size()
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        restoreState(savedInstanceState)
    }

    private fun restoreState(savedInstanceState: Bundle?) {
        selectionTracker.onRestoreInstanceState(savedInstanceState)
        val actionModeEnabled = librariesViewModel.isActionModeEnabled
        if (actionModeEnabled) {
            val numSelectedItems = librariesViewModel.numSelectedItems
            startActionMode(numSelectedItems)
        }
    }

    private fun startActionMode(numSelectedItems: Int = 0) {
        if (actionMode == null) {
            actionMode =
                (activity as? AppCompatActivity)?.startSupportActionMode(ActionModeCallback())
            setActionModeTitle(numSelectedItems)
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

        val searchItem = menu.findItem(R.id.action_search)
        setupSearchView(searchItem.actionView as SearchView)

    }

    private fun setupSearchView(searchView: SearchView) {
        val searchTextView =
            searchView.findViewById<TextView>(androidx.appcompat.R.id.search_src_text)
        searchTextView.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.white))
        searchTextView.setHintTextColor(
            ContextCompat.getColor(
                requireContext(),
                android.R.color.darker_gray
            )
        )
        searchView.queryHint = getString(R.string.hint_search)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return true
            }

            override fun onQueryTextChange(query: String): Boolean {
                librariesViewModel.getLibraries(searchTerm = query)
                return true
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_sort -> {
                showSortingOrderSelectionDialog()
                true
            }
            R.id.action_night_mode -> {
                showSelectNightModeDialog()
                true
            }
            else -> false
        }
    }

    private fun nightModeSupported(): Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q

    private fun showSortingOrderSelectionDialog() {
        val dialogFragment = SortDialog()
        dialogFragment.setListener(SortListener())
        showDialogFragment(dialogFragment, SortDialog.TAG)
    }

    private fun showSelectNightModeDialog() {
        if (nightModeSupported()) {
            val dialogFragment = NightModeDialog()
            showDialogFragment(dialogFragment, NightModeDialog.TAG)
        } else {
            showSnackbar(resources.getString(R.string.night_mode_not_supported))
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

    private inner class ActionModeCallback : ActionMode.Callback {

        override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
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

    private fun getSortingOrder(order: SortDialog.Order) = when (order) {
        SortDialog.Order.A_TO_Z -> LibrariesViewModel.Order.A_TO_Z
        SortDialog.Order.Z_TO_A -> LibrariesViewModel.Order.Z_TO_A
        SortDialog.Order.PINNED_FIRST -> LibrariesViewModel.Order.PINNED_FIRST
    }

}
