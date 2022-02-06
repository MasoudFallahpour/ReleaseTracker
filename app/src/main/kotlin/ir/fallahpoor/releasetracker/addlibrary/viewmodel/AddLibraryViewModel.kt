package ir.fallahpoor.releasetracker.addlibrary.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.fallahpoor.releasetracker.addlibrary.view.AddLibraryState
import ir.fallahpoor.releasetracker.common.GITHUB_BASE_URL
import ir.fallahpoor.releasetracker.data.entity.Library
import ir.fallahpoor.releasetracker.data.repository.LibraryRepository
import ir.fallahpoor.releasetracker.data.utils.ExceptionParser
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddLibraryViewModel
@Inject constructor(
    private val libraryRepository: LibraryRepository,
    private val exceptionParser: ExceptionParser
) : ViewModel() {

    private val _state = MutableLiveData<AddLibraryState>()
    val state: LiveData<AddLibraryState> = _state
    var libraryName by mutableStateOf("")
    var libraryUrlPath by mutableStateOf("")

    fun addLibrary(libraryName: String, libraryUrlPath: String) {

        if (libraryName.isEmpty()) {
            setState(AddLibraryState.EmptyLibraryName)
            return
        }

        if (libraryUrlPath.isEmpty()) {
            setState(AddLibraryState.EmptyLibraryUrl)
            return
        }

        if (!isGithubUrlPath(libraryUrlPath)) {
            setState(AddLibraryState.InvalidLibraryUrl)
            return
        }

        setState(AddLibraryState.InProgress)

        viewModelScope.launch {

            val state: AddLibraryState =
                try {
                    val library: Library? = libraryRepository.getLibrary(libraryName)
                    val libraryAlreadyExists = library != null
                    if (libraryAlreadyExists) {
                        AddLibraryState.Error("Library already exists")
                    } else {
                        val libraryVersion: String =
                            libraryRepository.getLibraryVersion(libraryName, libraryUrlPath)
                        libraryRepository.addLibrary(
                            libraryName,
                            GITHUB_BASE_URL + libraryUrlPath,
                            libraryVersion
                        )
                        this@AddLibraryViewModel.libraryName = ""
                        this@AddLibraryViewModel.libraryUrlPath = ""
                        AddLibraryState.LibraryAdded
                    }
                } catch (t: Throwable) {
                    val message = exceptionParser.getMessage(t)
                    AddLibraryState.Error(message)
                }
            setState(state)
        }

    }

    fun resetState() {
        setState(AddLibraryState.Initial)
    }

    private fun setState(state: AddLibraryState) {
        _state.value = state
    }

    private fun isGithubUrlPath(url: String): Boolean {
        val githubRegex = Regex("([-.\\w]+)/([-.\\w]+)", RegexOption.IGNORE_CASE)
        return githubRegex.matches(url)
    }

}