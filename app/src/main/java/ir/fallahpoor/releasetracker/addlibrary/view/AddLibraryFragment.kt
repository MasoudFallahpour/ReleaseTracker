package ir.fallahpoor.releasetracker.addlibrary.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.annotation.StringRes
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import ir.fallahpoor.releasetracker.R
import ir.fallahpoor.releasetracker.addlibrary.viewmodel.AddLibraryViewModel
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
        addLibraryViewModel.getViewState()
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
        setupLibraryHostTextView()
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
        val libraryHostIsValid = libraryHostIsValid()
        val libraryUrlIsValid = libraryUrlIsValid()
        return libraryNameIsValid && libraryHostIsValid && libraryUrlIsValid
    }

    private fun setupLibraryHostTextView() {
        val hosts = resources.getStringArray(R.array.hosts)
        val adapter = ArrayAdapter(requireContext(), R.layout.list_item_library_host, hosts)
        libraryHostTextView.setAdapter(adapter)
        libraryHostTextView.setOnItemClickListener { _, _, position, _ ->
            if (position == 0) {
                libraryUrlTextInputLayout.prefixText = getString(R.string.github_base_url)
            } else {
                libraryUrlTextInputLayout.prefixText = getString(R.string.jcenter_base_url)
            }
        }
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

    private fun libraryHostIsValid(): Boolean {
        val libraryHost = libraryHostTextView.text
        val isValid = libraryHost.isNotBlank()
        libraryHostTextInputLayout.error =
            if (!isValid) {
                getString(R.string.library_host_empty)
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