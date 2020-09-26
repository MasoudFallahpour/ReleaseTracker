package ir.fallahpoor.releasetracker.common

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

open class BaseViewModel<T> : ViewModel() {

    private val viewStateLiveData = MutableLiveData<ViewState<T>>()

    protected fun setViewState(viewState: ViewState<T>) {
        viewStateLiveData.value = viewState
    }

    fun getViewState(): LiveData<ViewState<T>> = viewStateLiveData

}