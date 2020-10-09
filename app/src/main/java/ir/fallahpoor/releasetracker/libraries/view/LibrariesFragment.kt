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
import ir.fallahpoor.releasetracker.libraries.viewmodel.LibrariesViewModel
import kotlinx.android.synthetic.main.fragment_libraries.*

@AndroidEntryPoint
class LibrariesFragment : Fragment() {

    private val librariesViewModel: LibrariesViewModel by viewModels()
    private lateinit var selectionTracker: SelectionTracker<String>
    private lateinit var librariesAdapter: LibrariesAdapter
    private var currentSortingOrder: SortingOrderDialogFragment.SortingOrder? = null
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
    }

    private fun setupViews() {
        addLibraryButton.setOnClickListener {
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
            },
            longClickListener = {
                (activity as? AppCompatActivity)?.startSupportActionMode(ActionModeCallback())
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
                    is ViewState.DataLoadedState -> hideSecondaryLoading()
                    is ViewState.ErrorState -> handleErrorState(viewState)
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

    private fun showSnackbar(message: String) {
        Snackbar.make(
            librariesRootLayout,
            message,
            Snackbar.LENGTH_LONG
        ).setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE)
            .show()
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
        val sortingOrderDialog = SortingOrderDialogFragment()
        sortingOrderDialog.setListener { sortingOrder: SortingOrderDialogFragment.SortingOrder ->
            if (sortingOrder != currentSortingOrder) {
                currentSortingOrder = sortingOrder
                librariesViewModel.getLibraries(getSortingOrder(sortingOrder))
            }
        }
        showDialogFragment(sortingOrderDialog)
    }

    private fun getSortingOrder(
        sortingOrder: SortingOrderDialogFragment.SortingOrder
    ): LibrariesViewModel.SortingOrder {
        return when (sortingOrder) {
            SortingOrderDialogFragment.SortingOrder.A_TO_Z -> LibrariesViewModel.SortingOrder.A_TO_Z
            SortingOrderDialogFragment.SortingOrder.Z_TO_A -> LibrariesViewModel.SortingOrder.Z_TO_A
            SortingOrderDialogFragment.SortingOrder.PINNED_FIRST -> LibrariesViewModel.SortingOrder.PINNED_FIRST
        }
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
        val deleteConfirmationDialog = DeleteConfirmationDialog()
        deleteConfirmationDialog.setListener(object : DeleteConfirmationDialog.Listener {
            override fun cancelClicked() {
                deleteConfirmationDialog.dismiss()
            }

            override fun deleteClicked() {
                val libraryNames = selectionTracker.selection.map {
                    it
                }
                librariesViewModel.deleteLibraries(libraryNames)
                actionMode?.finish()
                actionMode = null
                deleteConfirmationDialog.dismiss()
            }
        })
        showDialogFragment(deleteConfirmationDialog)
    }

    private fun showDialogFragment(dialogFragment: DialogFragment) {
        dialogFragment.show(requireActivity().supportFragmentManager, null)
    }

}
