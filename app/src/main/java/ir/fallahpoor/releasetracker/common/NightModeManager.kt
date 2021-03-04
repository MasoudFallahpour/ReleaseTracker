package ir.fallahpoor.releasetracker.common

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.map
import ir.fallahpoor.releasetracker.data.utils.LocalStorage
import ir.fallahpoor.releasetracker.data.utils.NightMode
import javax.inject.Inject

class NightModeManager
@Inject constructor(
    private val context: Context,
    private val localStorage: LocalStorage
) {

    val isNightModeOn: LiveData<Boolean> = localStorage.getNightModeAsFlow()
        .asLiveData()
        .map { mode: NightMode ->
            when (mode) {
                NightMode.OFF -> false
                NightMode.ON -> true
                else -> isSystemInDarkTheme()
            }
        }

    val isNightModeSupported = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q

    fun setNightMode(nightMode: NightMode) {
        val currentMode = getNightMode()
        if (nightMode == currentMode) {
            return
        }
        setMode(nightMode)
    }

    fun getNightMode(): NightMode = localStorage.getNightMode()

    private fun setMode(nightMode: NightMode) {
        when (nightMode) {
            NightMode.ON -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            NightMode.OFF -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            NightMode.AUTO -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }
        localStorage.setNightMode(nightMode)
    }

    fun isNightModeOn(): Boolean {
        return when (getNightMode()) {
            NightMode.OFF -> false
            NightMode.ON -> true
            NightMode.AUTO -> isSystemInDarkTheme()
        }
    }

    private fun isSystemInDarkTheme(): Boolean {
        val uiMode = context.resources.configuration.uiMode
        return (uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
    }

}