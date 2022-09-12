package ir.fallahpoor.releasetracker

import android.os.Build
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import ir.fallahpoor.releasetracker.data.NightMode
import ir.fallahpoor.releasetracker.data.repository.nightmode.NightModeRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

sealed class Event {
    data class ChangeNightMode(val nightMode: NightMode) : Event()
}

@HiltViewModel
class NightModeViewModel @Inject constructor(
    private val nightModeRepository: NightModeRepository
) : ViewModel() {

    val state: StateFlow<NightMode> = nightModeRepository.getNightModeAsFlow()
        .stateIn(viewModelScope, SharingStarted.Eagerly, nightModeRepository.getNightMode())
    val isNightModeSupported = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q

    fun handleEvent(event: Event) {
        if (event is Event.ChangeNightMode) {
            setNightMode(event.nightMode)
        }
    }

    private fun setNightMode(nightMode: NightMode) {
        if (nightMode == state.value) {
            return
        }
        viewModelScope.launch {
            try {
                nightModeRepository.setNightMode(nightMode)
                when (nightMode) {
                    NightMode.ON -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    NightMode.OFF -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    NightMode.AUTO -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                }
            } catch (t: Throwable) {
                Timber.d(t, "Failed to set the night mode")
            }
        }
    }

}