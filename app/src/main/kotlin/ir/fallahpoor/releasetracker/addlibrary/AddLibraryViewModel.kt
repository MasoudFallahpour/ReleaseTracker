package ir.fallahpoor.releasetracker.addlibrary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.fallahpoor.releasetracker.common.GITHUB_BASE_URL
import ir.fallahpoor.releasetracker.data.entity.Library
import ir.fallahpoor.releasetracker.data.repository.LibraryRepository
import ir.fallahpoor.releasetracker.data.utils.ExceptionParser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddLibraryViewModel
@Inject constructor(
    private val libraryRepository: LibraryRepository,
    private val exceptionParser: ExceptionParser
) : ViewModel() {

    private val GITHUB_URL_PATH_REGEX = Regex("([-.\\w]+)/([-.\\w]+)", RegexOption.IGNORE_CASE)

    private val _state = MutableStateFlow(AddLibraryScreenState())
    val state: StateFlow<AddLibraryScreenState> = _state

    fun handleEvent(event: Event) {
        when (event) {
            is Event.UpdateLibraryName -> updateLibraryName(event.libraryName)
            is Event.UpdateLibraryUrlPath -> updateLibraryUrlPath(event.libraryUrlPath)
            is Event.AddLibrary -> {
                addLibrary(event.libraryName, event.libraryUrlPath)
            }
            is Event.ErrorDismissed -> resetUiState()
        }
    }

    private fun updateLibraryName(libraryName: String) {
        _state.value = _state.value.copy(libraryName = libraryName)
    }

    private fun updateLibraryUrlPath(libraryUrlPath: String) {
        _state.value = _state.value.copy(libraryUrlPath = libraryUrlPath)
    }

    private fun resetUiState() {
        _state.value = AddLibraryScreenState()
    }

    private fun addLibrary(libraryName: String, libraryUrlPath: String) {

        if (libraryName.isEmpty()) {
            _state.value = createNewState(AddLibraryState.EmptyLibraryName)
            return
        }

        if (libraryUrlPath.isEmpty()) {
            _state.value = createNewState(AddLibraryState.EmptyLibraryUrl)
            return
        }

        if (!isGithubUrlPath(libraryUrlPath)) {
            _state.value = createNewState(AddLibraryState.InvalidLibraryUrl)
            return
        }

        _state.value = createNewState(AddLibraryState.InProgress)

        viewModelScope.launch {
            val state: AddLibraryScreenState = try {
                val library: Library? = libraryRepository.getLibrary(libraryName)
                val libraryAlreadyExists = library != null
                if (libraryAlreadyExists) {
                    createNewState(AddLibraryState.Error("Library already exists"))
                } else {
                    val libraryVersion: String =
                        libraryRepository.getLibraryVersion(libraryName, libraryUrlPath)
                    libraryRepository.addLibrary(
                        libraryName = libraryName,
                        libraryUrl = GITHUB_BASE_URL + libraryUrlPath,
                        libraryVersion = libraryVersion
                    )
                    _state.value.copy(
                        libraryName = "",
                        libraryUrlPath = "",
                        addLibraryState = AddLibraryState.LibraryAdded
                    )
                }
            } catch (t: Throwable) {
                val message = exceptionParser.getMessage(t)
                createNewState(AddLibraryState.Error(message))
            }
            _state.value = state
        }

    }

    private fun createNewState(addLibraryState: AddLibraryState): AddLibraryScreenState =
        _state.value.copy(addLibraryState = addLibraryState)

    private fun isGithubUrlPath(url: String): Boolean = GITHUB_URL_PATH_REGEX.matches(url)

}