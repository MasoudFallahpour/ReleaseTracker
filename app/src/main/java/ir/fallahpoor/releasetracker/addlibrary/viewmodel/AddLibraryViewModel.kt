package ir.fallahpoor.releasetracker.addlibrary.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ir.fallahpoor.releasetracker.common.SingleLiveData
import ir.fallahpoor.releasetracker.common.ViewState
import ir.fallahpoor.releasetracker.data.entity.Library
import ir.fallahpoor.releasetracker.data.repository.LibraryRepository
import ir.fallahpoor.releasetracker.data.utils.ExceptionParser
import kotlinx.coroutines.launch

class AddLibraryViewModel
@ViewModelInject constructor(
    private val libraryRepository: LibraryRepository,
    private val exceptionParser: ExceptionParser
) : ViewModel() {

    private val _viewStateLiveData = SingleLiveData<ViewState<Unit>>()
    val viewState: LiveData<ViewState<Unit>> = _viewStateLiveData

    fun addLibrary(libraryName: String, libraryUrl: String) {

        _viewStateLiveData.value = ViewState.loading()

        viewModelScope.launch {

            try {
                val library: Library? = libraryRepository.getLibrary(libraryName)
                val libraryAlreadyExists = library != null
                if (libraryAlreadyExists) {
                    _viewStateLiveData.value = ViewState.error("Library already exists")
                } else {
                    val libraryVersion: String =
                        libraryRepository.getLibraryVersion(libraryName, libraryUrl)
                    libraryRepository.addLibrary(libraryName, libraryUrl, libraryVersion)
                    _viewStateLiveData.value = ViewState.success(Unit)
                }
            } catch (t: Throwable) {
                val message = exceptionParser.getMessage(t)
                _viewStateLiveData.value = ViewState.error(message)
            }

        }

    }

}