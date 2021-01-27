//package ir.fallahpoor.releasetracker.libraries.view
//
//import android.os.Build
//import android.os.Bundle
//import android.view.*
//import android.widget.TextView
//import androidx.appcompat.app.AppCompatActivity
//import androidx.appcompat.view.ActionMode
//import androidx.appcompat.widget.SearchView
//import androidx.core.content.ContextCompat
//import androidx.fragment.app.DialogFragment
//import androidx.fragment.app.Fragment
//import androidx.fragment.app.viewModels
//import androidx.recyclerview.selection.SelectionPredicates
//import androidx.recyclerview.selection.SelectionTracker
//import androidx.recyclerview.selection.StorageStrategy
//import com.google.android.material.snackbar.Snackbar
//import dagger.hilt.android.AndroidEntryPoint
//import ir.fallahpoor.releasetracker.R
//import ir.fallahpoor.releasetracker.databinding.FragmentLibrariesBinding
//import ir.fallahpoor.releasetracker.libraries.view.dialogs.DeleteDialog
//import ir.fallahpoor.releasetracker.libraries.view.selection.LibrariesItemDetailsLookup
//import ir.fallahpoor.releasetracker.libraries.view.selection.LibrariesItemKeyProvider
//import ir.fallahpoor.releasetracker.libraries.viewmodel.LibrariesViewModel
//
//@AndroidEntryPoint
//class LibrariesFragment : Fragment() {
//
//    private val librariesViewModel: LibrariesViewModel by viewModels()
//    private lateinit var selectionTracker: SelectionTracker<String>
//    private lateinit var librariesAdapter: LibrariesAdapter
//    private var actionMode: ActionMode? = null
//    private var _binding: FragmentLibrariesBinding? = null
//    private val binding get() = _binding!!
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setHasOptionsMenu(true)
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        setupViews()
//    }
//
//    private fun setupViews() {
//        setupRecyclerView()
//    }
//
//    private fun setupRecyclerView() {
//        selectionTracker = createSelectionTracker()
//        librariesAdapter.selectionTracker = selectionTracker
//    }
//
//    private fun createSelectionTracker(): SelectionTracker<String> {
//
//        val selectionTracker = SelectionTracker.Builder(
//            "LibrariesSelectionTracker",
//            binding.librariesRecyclerView,
//            LibrariesItemKeyProvider(librariesAdapter),
//            LibrariesItemDetailsLookup(binding.librariesRecyclerView),
//            StorageStrategy.createStringStorage()
//        ).withSelectionPredicate(
//            SelectionPredicates.createSelectAnything()
//        ).build()
//
//        selectionTracker.addObserver(object : SelectionTracker.SelectionObserver<Long>() {
//            override fun onSelectionChanged() {
//                val items = selectionTracker.selection
//                when {
//                    items.isEmpty -> actionMode?.finish()
//                    items.size() == 1 -> {
//                        startActionMode()
//                        setActionModeTitle(items.size())
//                    }
//                    else -> setActionModeTitle(items.size())
//                }
//            }
//        })
//
//        return selectionTracker
//
//    }
//
//    private fun setActionModeTitle(numSelectedItems: Int) {
//        actionMode?.title = if (numSelectedItems > 0) {
//            getString(R.string.selected, numSelectedItems)
//        } else {
//            ""
//        }
//    }
//
//    private fun showSnackbar(message: String) {
//        Snackbar.make(
//            binding.librariesRootLayout,
//            message,
//            Snackbar.LENGTH_LONG
//        ).setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE)
//            .show()
//    }
//
//    override fun onSaveInstanceState(outState: Bundle) {
//        super.onSaveInstanceState(outState)
//        saveState(outState)
//    }
//
//    private fun saveState(outState: Bundle) {
//        selectionTracker.onSaveInstanceState(outState)
//        librariesViewModel.isActionModeEnabled = actionMode != null
//        librariesViewModel.numSelectedItems = selectionTracker.selection.size()
//    }
//
//    override fun onViewStateRestored(savedInstanceState: Bundle?) {
//        super.onViewStateRestored(savedInstanceState)
//        restoreState(savedInstanceState)
//    }
//
//    private fun restoreState(savedInstanceState: Bundle?) {
//        selectionTracker.onRestoreInstanceState(savedInstanceState)
//        val actionModeEnabled = librariesViewModel.isActionModeEnabled
//        if (actionModeEnabled) {
//            val numSelectedItems = librariesViewModel.numSelectedItems
//            startActionMode(numSelectedItems)
//        }
//    }
//
//    private fun startActionMode(numSelectedItems: Int = 0) {
//        if (actionMode == null) {
//            actionMode =
//                (activity as? AppCompatActivity)?.startSupportActionMode(ActionModeCallback())
//            setActionModeTitle(numSelectedItems)
//        }
//    }
//
//    override fun onResume() {
//        super.onResume()
//        restoreDeleteDialogIfPresent()
//        restoreSortDialogIfPresent()
//    }
//
//    private fun restoreDeleteDialogIfPresent() {
//        val deleteDialog = requireActivity()
//            .supportFragmentManager
//            .findFragmentByTag(DeleteDialog.TAG)
//        deleteDialog?.let {
//            (it as DeleteDialog).setListener(DeleteListener())
//        }
//    }
//
//    private fun restoreSortDialogIfPresent() {
//        val sortDialog = requireActivity()
//            .supportFragmentManager
//            .findFragmentByTag(SortDialog.TAG)
//        sortDialog?.let {
//            (it as SortDialog).setListener(object : SortDialog.SortListener {
//                override fun orderSelected(order: SortDialog.Order) {
//                    TODO("Not yet implemented")
//                }
//            })
//        }
//    }
//
//    private fun setupSearchView(searchView: SearchView) {
//        val searchTextView =
//            searchView.findViewById<TextView>(androidx.appcompat.R.id.search_src_text)
//        searchTextView.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.white))
//        searchTextView.setHintTextColor(
//            ContextCompat.getColor(
//                requireContext(),
//                android.R.color.darker_gray
//            )
//        )
//        searchView.queryHint = getString(R.string.hint_search)
//        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
//            override fun onQueryTextSubmit(query: String): Boolean {
//                return true
//            }
//
//            override fun onQueryTextChange(query: String): Boolean {
//                librariesViewModel.getLibraries(searchTerm = query)
//                return true
//            }
//        })
//    }
//
//    private fun showDeleteConfirmationDialog() {
//        val dialogFragment = DeleteDialog()
//        dialogFragment.setListener(DeleteListener())
//        showDialogFragment(dialogFragment, DeleteDialog.TAG)
//    }
//
//    private fun showDialogFragment(dialogFragment: DialogFragment?, tag: String) {
//        dialogFragment?.show(requireActivity().supportFragmentManager, tag)
//    }
//
//    private inner class ActionModeCallback : ActionMode.Callback {
//
//        override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
//            mode.menuInflater.inflate(R.menu.menu_libraries_context, menu)
//            return true
//        }
//
//        override fun onPrepareActionMode(mode: ActionMode, menu: Menu) = false
//
//        override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {
//            return if (item.itemId == R.id.action_delete) {
//                showDeleteConfirmationDialog()
//                true
//            } else {
//                false
//            }
//        }
//
//        override fun onDestroyActionMode(mode: ActionMode) {
//            selectionTracker.clearSelection()
//            actionMode = null
//        }
//
//    }
//
//    private inner class DeleteListener : DeleteDialog.DeleteListener {
//
//        override fun cancelClicked(dialogFragment: DialogFragment) {
//            dialogFragment.dismiss()
//        }
//
//        override fun deleteClicked(dialogFragment: DialogFragment) {
//            val libraryNames = selectionTracker.selection.map {
//                it
//            }
//            librariesViewModel.deleteLibraries(libraryNames)
//            actionMode?.finish()
//            dialogFragment.dismiss()
//        }
//
//    }
//
//}
