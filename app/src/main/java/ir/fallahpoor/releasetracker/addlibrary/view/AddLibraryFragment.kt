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
import ir.fallahpoor.releasetracker.databinding.FragmentAddLibraryBinding

@AndroidEntryPoint
class AddLibraryFragment : Fragment() {

    private val addLibraryViewModel: AddLibraryViewModel by viewModels()
    private var _binding: FragmentAddLibraryBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddLibraryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        observeViewModel()
        setupViews()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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
        binding.addLibraryButton.isEnabled = false
    }

    private fun handleDataLoadedState() {
        hideLoading()
        DeviceUtils.closeKeyboard(requireContext(), requireView().rootView)
        binding.addLibraryButton.isEnabled = true
        binding.libraryNameEditText.setText("")
        binding.libraryNameEditText.requestFocus()
        binding.libraryUrlEditText.setText("")
        showSnackbar(R.string.library_added)
    }

    private fun handleErrorState(viewState: ViewState.ErrorState<Unit>) {
        hideLoading()
        binding.addLibraryButton.isEnabled = true
        showSnackbar(viewState.errorMessage)
    }

    private fun setupViews() {
        binding.addLibraryButton.setOnClickListener {
            if (inputsAreValid()) {
                val libraryName = binding.libraryNameEditText.text.toString()
                val libraryUrl = binding.libraryUrlEditText.text.toString()
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
        val libraryName = binding.libraryNameEditText.text.toString()
        val isValid = libraryName.isNotBlank()
        binding.libraryNameTextInputLayout.error =
            if (!isValid) {
                getString(R.string.library_name_empty)
            } else {
                ""
            }
        return isValid
    }

    private fun libraryUrlIsValid(): Boolean {

        val libraryUrl = binding.libraryUrlEditText.text.toString()

        return when {
            libraryUrl.isBlank() -> {
                binding.libraryUrlTextInputLayout.error = getString(R.string.library_url_empty)
                false
            }
            !isGithubUrl(libraryUrl) -> {
                binding.libraryUrlTextInputLayout.error = getString(R.string.library_url_invalid)
                false
            }
            else -> {
                binding.libraryUrlTextInputLayout.error = ""
                true
            }
        }

    }

    private fun isGithubUrl(url: String): Boolean {
        val githubRegex = Regex("([-.\\w]+)/([-.\\w]+)", RegexOption.IGNORE_CASE)
        return githubRegex.matches(url)
    }

    private fun showLoading() {
        binding.addLibraryProgressBar.isVisible = true
    }

    private fun hideLoading() {
        binding.addLibraryProgressBar.isVisible = false
    }

    private fun showSnackbar(@StringRes message: Int) {
        showSnackbar(getString(message))
    }

    private fun showSnackbar(message: String) {
        Snackbar.make(
            binding.addLibraryRootLayout,
            message,
            Snackbar.LENGTH_LONG
        ).setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE)
            .show()
    }

}