package ir.fallahpoor.releasetracker.libraries.view

import android.os.Bundle
import android.view.*
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
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
    private val librariesAdapter = LibrariesAdapter { library: Library, isFavourite: Boolean ->
        librariesViewModel.setPinned(library, isFavourite)
    }
    private var currentSortingOrder: SortingOrderDialogFragment.SortingOrder? = null

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
        with(librariesRecyclerView) {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            adapter = librariesAdapter
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
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
        librariesViewModel.favouriteViewState
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
        librariesAdapter.setLibraries(viewState.data)
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
        sortingOrderDialog.show(requireActivity().supportFragmentManager, null)
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

}