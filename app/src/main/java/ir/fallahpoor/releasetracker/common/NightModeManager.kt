package ir.fallahpoor.releasetracker.common

import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import ir.fallahpoor.releasetracker.data.utils.LocalStorage
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class NightModeManager
@Inject constructor(
    private val localStorage: LocalStorage
) {

    enum class Mode {
        ON,
        OFF,
        AUTO
    }

    val nightMode: LiveData<String> =
        localStorage.getNightModeAsFlow()
            .map { it }
            .asLiveData()

    fun setNightMode(mode: Mode) {

        val currentMode = getCurrentNightMode()

        if (mode == currentMode) {
            return
        }

        setMode(mode)

    }

    fun setDefaultNightMode() {
        val currentMode = getCurrentNightMode()
        setMode(currentMode)
    }

    fun getCurrentNightMode(): Mode {
        return Mode.valueOf(localStorage.getNightMode() ?: Mode.AUTO.name)
    }

    private fun setMode(currentMode: Mode) {
        when (currentMode) {
            Mode.ON -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            Mode.OFF -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            Mode.AUTO -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }
        localStorage.setNightMode(currentMode.name)
    }

    @Composable
    fun isDarkTheme(): Boolean {
        return when (getCurrentNightMode()) {
            Mode.OFF -> false
            Mode.ON -> true
            Mode.AUTO -> isSystemInDarkTheme()
        }
    }

}