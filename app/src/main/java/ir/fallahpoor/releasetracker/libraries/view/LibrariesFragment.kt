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
        librariesViewModel.getViewState()
            .observe(viewLifecycleOwner) { viewState: ViewState<List<Library>> ->
                when (viewState) {
                    is ViewState.LoadingState -> handleLoadingState()
                    is ViewState.DataLoadedState -> handleDataLoadedState(viewState)
                    is ViewState.ErrorState -> handleErrorState(viewState)
                }
            }
    }

    private fun handleLoadingState() {
        showLoading()
    }

    private fun handleDataLoadedState(viewState: ViewState.DataLoadedState<List<Library>>) {
        hideLoading()
        with(librariesRecyclerView) {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            adapter = LibrariesAdapter(viewState.data)
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        }
    }

    private fun handleErrorState(viewState: ViewState.ErrorState<List<Library>>) {
        hideLoading()
        showSnackbar(viewState.errorMessage)
    }

    private fun setupViews() {
        addLibraryButton.setOnClickListener {
            findNavController().navigate(R.id.action_librariesFragment_to_addLibraryFragment)
        }
    }

    private fun showLoading() {
        librariesProgressBar.isVisible = true
    }

    private fun hideLoading() {
        librariesProgressBar.isVisible = false
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