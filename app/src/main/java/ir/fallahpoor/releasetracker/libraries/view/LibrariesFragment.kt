package ir.fallahpoor.releasetracker.libraries.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
        with(librariesRecyclerView) {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            adapter = LibrariesAdapter(viewState.data) { library: Library, isFavourite: Boolean ->
                librariesViewModel.setFavourite(library, isFavourite)
            }
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        }
    }

    private fun <T> handleErrorState(viewState: ViewState.ErrorState<T>) {
        hidePrimaryLoading()
        showSnackbar(viewState.errorMessage)
    }

    private fun showSecondaryLoading() {
        librariesSecondaryProgressBar.isVisible = true
    }

    private fun setupViews() {
        addLibraryButton.setOnClickListener {
            findNavController().navigate(R.id.action_librariesFragment_to_addLibraryFragment)
        }
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

}