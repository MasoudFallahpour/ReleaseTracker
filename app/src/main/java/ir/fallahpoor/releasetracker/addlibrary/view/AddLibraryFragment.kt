package ir.fallahpoor.releasetracker.addlibrary.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import ir.fallahpoor.releasetracker.R
import ir.fallahpoor.releasetracker.addlibrary.viewmodel.AddLibraryViewModel
import ir.fallahpoor.releasetracker.common.DeviceUtils
import ir.fallahpoor.releasetracker.common.ViewState
import kotlinx.android.synthetic.main.fragment_add_library.*

@AndroidEntryPoint
class AddLibraryFragment : Fragment() {

    private val addLibraryViewModel: AddLibraryViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_add_library, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        observeViewModel()
        setupViews()
    }

    private fun observeViewModel() {
        addLibraryViewModel.viewState
            .observe(viewLifecycleOwner) { viewState: ViewState<Unit> ->
                when (viewState) {
                    is ViewState.LoadingState -> handleLoadingState()
                    is ViewState.DataLoadedState -> handleDataLoadedState()
                    is ViewState.ErrorState -> handleErrorState(viewState)
                }
            }
    }

    private fun handleLoadingState() {
        showLoading()
        addLibraryButton.isEnabled = false
    }

    private fun handleDataLoadedState() {
        hideLoading()
        DeviceUtils.closeKeyboard(requireContext(), requireView().rootView)
        addLibraryButton.isEnabled = true
        libraryNameEditText.setText("")
        libraryNameEditText.requestFocus()
        libraryUrlEditText.setText("")
        showSnackbar(R.string.library_added)
    }

    private fun handleErrorState(viewState: ViewState.ErrorState<Unit>) {
        hideLoading()
        addLibraryButton.isEnabled = true
        showSnackbar(viewState.errorMessage)
    }

    private fun setupViews() {
        addLibraryButton.setOnClickListener {
            if (inputsAreValid()) {
                val libraryName = libraryNameEditText.text.toString()
                val libraryUrl = libraryUrlEditText.text.toString()
                addLibraryViewModel.addLibrary(libraryName, "https://github.com/$libraryUrl")
            }
        }
    }

    private fun inputsAreValid(): Boolean {
        val libraryNameIsValid = libraryNameIsValid()
        val libraryUrlIsValid = libraryUrlIsValid()
        return libraryNameIsValid && libraryUrlIsValid
    }

    private fun libraryNameIsValid(): Boolean {
        val libraryName = libraryNameEditText.text.toString()
        val isValid = libraryName.isNotBlank()
        libraryNameTextInputLayout.error =
            if (!isValid) {
                getString(R.string.library_name_empty)
            } else {
                ""
            }
        return isValid
    }

    private fun libraryUrlIsValid(): Boolean {

        val libraryUrl = libraryUrlEditText.text.toString()

        return when {
            libraryUrl.isBlank() -> {
                libraryUrlTextInputLayout.error = getString(R.string.library_url_empty)
                false
            }
            !isGithubUrl(libraryUrl) -> {
                libraryUrlTextInputLayout.error = getString(R.string.library_url_invalid)
                false
            }
            else -> {
                libraryUrlTextInputLayout.error = ""
                true
            }
        }

    }

    private fun isGithubUrl(url: String): Boolean {
        val githubRegex = Regex("([-.\\w]+)/([-.\\w]+)", RegexOption.IGNORE_CASE)
        return githubRegex.matches(url)
    }

    private fun showLoading() {
        addLibraryProgressBar.isVisible = true
    }

    private fun hideLoading() {
        addLibraryProgressBar.isVisible = false
    }

    private fun showSnackbar(@StringRes message: Int) {
        showSnackbar(getString(message))
    }

    private fun showSnackbar(message: String) {
        Snackbar.make(
            addLibraryRootLayout,
            message,
            Snackbar.LENGTH_LONG
        ).setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE)
            .show()
    }

}