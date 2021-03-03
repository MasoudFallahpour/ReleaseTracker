package ir.fallahpoor.releasetracker.common

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.map
import ir.fallahpoor.releasetracker.data.utils.LocalStorage
import javax.inject.Inject

class NightModeManager
@Inject constructor(
    private val context: Context,
    private val localStorage: LocalStorage
) {

    enum class Mode(val value: String) {
        ON("On"),
        OFF("Off"),
        AUTO("Auto")
    }

    val isNightModeOn: LiveData<Boolean> = localStorage.getNightModeAsFlow()
        .asLiveData()
        .map { mode: String ->
            Mode.valueOf(if (mode.isNotBlank()) mode else Mode.AUTO.name)
        }
        .map { mode: Mode ->
            when (mode) {
                Mode.OFF -> false
                Mode.ON -> true
                else -> isSystemInDarkTheme()
            }
        }

    val isNightModeSupported = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q

    fun setNightMode(mode: Mode) {

        val currentMode = getNightMode()

        if (mode == currentMode) {
            return
        }

        setMode(mode)

    }

    fun getNightMode(): Mode {
        return Mode.valueOf(localStorage.getNightMode() ?: Mode.AUTO.name)
    }

    private fun setMode(mode: Mode) {
        when (mode) {
            Mode.ON -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            Mode.OFF -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            Mode.AUTO -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }
        localStorage.setNightMode(mode.name)
    }

    fun isNightModeOn(): Boolean {
        return when (getNightMode()) {
            Mode.OFF -> false
            Mode.ON -> true
            Mode.AUTO -> isSystemInDarkTheme()
        }
    }

    private fun isSystemInDarkTheme(): Boolean {
        val uiMode = context.resources.configuration.uiMode
        return (uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
    }

}