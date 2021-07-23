package ir.fallahpoor.releasetracker.addlibrary.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.fallahpoor.releasetracker.addlibrary.view.AddLibraryState
import ir.fallahpoor.releasetracker.common.GITHUB_BASE_URL
import ir.fallahpoor.releasetracker.common.SingleLiveData
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

    private val _state: SingleLiveData<AddLibraryState> = SingleLiveData()
    val state: LiveData<AddLibraryState> = _state
    var libraryName by mutableStateOf("")
    var libraryUrlPath by mutableStateOf("")

    fun addLibrary(libraryName: String, libraryUrlPath: String) {

        if (libraryName.isEmpty()) {
            _state.value = AddLibraryState.EmptyLibraryName
            return
        }

        if (libraryUrlPath.isEmpty()) {
            _state.value = AddLibraryState.EmptyLibraryUrl
            return
        }

        if (!isGithubUrlPath(libraryUrlPath)) {
            _state.value = AddLibraryState.InvalidLibraryUrl
            return
        }

        _state.value = AddLibraryState.InProgress

        viewModelScope.launch {

            _state.value =
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

        }

    }

    private fun isGithubUrlPath(url: String): Boolean {
        val githubRegex = Regex("([-.\\w]+)/([-.\\w]+)", RegexOption.IGNORE_CASE)
        return githubRegex.matches(url)
    }

}