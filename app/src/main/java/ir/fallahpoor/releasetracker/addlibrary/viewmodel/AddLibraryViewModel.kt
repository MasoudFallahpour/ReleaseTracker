package ir.fallahpoor.releasetracker.addlibrary.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.fallahpoor.releasetracker.addlibrary.view.State
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

    private val _state: MutableLiveData<State> = MutableLiveData()
    val state: LiveData<State> = _state
    var libraryName = mutableStateOf("")
    var libraryUrl = mutableStateOf("")

    fun addLibrary() {

        if (libraryName.value.isEmpty()) {
            _state.value = State.EmptyLibraryName
            return
        }

        if (libraryUrl.value.isEmpty()) {
            _state.value = State.EmptyLibraryUrl
            return
        }

        if (!isGithubUrl(libraryUrl.value)) {
            _state.value = State.InvalidLibraryUrl
            return
        }

        _state.value = State.Loading

        viewModelScope.launch {

            _state.value =
                try {
                    val library: Library? = libraryRepository.getLibrary(libraryName.value)
                    val libraryAlreadyExists = library != null
                    if (libraryAlreadyExists) {
                        State.Error("Library already exists")
                    } else {
                        val libraryVersion: String =
                            libraryRepository.getLibraryVersion(libraryName.value, libraryUrl.value)
                        libraryRepository.addLibrary(
                            libraryName.value,
                            libraryUrl.value,
                            libraryVersion
                        )
                        libraryName.value = ""
                        libraryUrl.value = ""
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