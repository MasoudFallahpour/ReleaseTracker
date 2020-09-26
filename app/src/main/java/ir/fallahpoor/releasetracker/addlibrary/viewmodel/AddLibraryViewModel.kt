package ir.fallahpoor.releasetracker.addlibrary.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.viewModelScope
import ir.fallahpoor.releasetracker.common.BaseViewModel
import ir.fallahpoor.releasetracker.common.ExceptionParser
import ir.fallahpoor.releasetracker.common.ViewState
import ir.fallahpoor.releasetracker.data.repository.LibraryRepository
import kotlinx.coroutines.launch

class AddLibraryViewModel
@ViewModelInject constructor(
    private val libraryRepository: LibraryRepository,
    private val exceptionParser: ExceptionParser
) : BaseViewModel<Unit>() {

    fun addLibrary(libraryName: String, libraryUrl: String) {

        viewModelScope.launch {

            setViewState(ViewState.loading())

            try {
                libraryRepository.addLibrary(libraryName, libraryUrl)
                setViewState(ViewState.success(Unit))
            } catch (t: Throwable) {
                val message = exceptionParser.getMessage(t)
                setViewState(ViewState.error(message))
            }

        }

    }

}