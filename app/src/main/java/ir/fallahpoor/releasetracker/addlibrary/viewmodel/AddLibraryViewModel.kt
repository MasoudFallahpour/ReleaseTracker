package ir.fallahpoor.releasetracker.addlibrary.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ir.fallahpoor.releasetracker.addlibrary.view.State
import ir.fallahpoor.releasetracker.data.entity.Library
import ir.fallahpoor.releasetracker.data.repository.LibraryRepository
import ir.fallahpoor.releasetracker.data.utils.ExceptionParser
import kotlinx.coroutines.launch

class AddLibraryViewModel
@ViewModelInject constructor(
    private val libraryRepository: LibraryRepository,
    private val exceptionParser: ExceptionParser
) : ViewModel() {

    private val _state: MutableLiveData<State> = MutableLiveData()
    val state: LiveData<State> = _state
    var libraryName by mutableStateOf("")
    var libraryUrl by mutableStateOf("")

    fun addLibrary() {

        if (libraryName.isEmpty()) {
            _state.value = State.EmptyLibraryName
            return
        }

        if (libraryUrl.isEmpty()) {
            _state.value = State.EmptyLibraryUrl
            return
        }

        if (!isGithubUrl(libraryUrl)) {
            _state.value = State.InvalidLibraryUrl
            return
        }

        _state.value = State.Loading

        viewModelScope.launch {

            _state.value =
                try {
                    val library: Library? = libraryRepository.getLibrary(libraryName)
                    val libraryAlreadyExists = library != null
                    if (libraryAlreadyExists) {
                        State.Error("Library already exists")
                    } else {
                        val libraryVersion: String =
                            libraryRepository.getLibraryVersion(libraryName, libraryUrl)
                        libraryRepository.addLibrary(
                            libraryName,
                            libraryUrl,
                            libraryVersion
                        )
                        libraryName = ""
                        libraryUrl = ""
                        State.LibraryAdded
                    }
                } catch (t: Throwable) {
                    val message = exceptionParser.getMessage(t)
                    State.Error(message)
                }

        }

    }

    private fun isGithubUrl(url: String): Boolean {
        val githubRegex = Regex("([-.\\w]+)/([-.\\w]+)", RegexOption.IGNORE_CASE)
        return githubRegex.matches(url)
    }

}