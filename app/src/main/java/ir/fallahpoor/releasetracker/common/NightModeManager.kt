package ir.fallahpoor.releasetracker.common

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.map
import ir.fallahpoor.releasetracker.data.utils.NightMode
import ir.fallahpoor.releasetracker.data.utils.storage.Storage
import javax.inject.Inject

class NightModeManager
@Inject constructor(
    private val context: Context,
    private val storage: Storage
) {

    val isNightModeOnLiveData: LiveData<Boolean> = storage.getNightModeAsFlow()
        .asLiveData()
        .map { mode: NightMode ->
            when (mode) {
                NightMode.OFF -> false
                NightMode.ON -> true
                else -> isSystemInDarkTheme()
            }
        }

    val isNightModeOn: Boolean
        get() = when (currentNightMode) {
            NightMode.OFF -> false
            NightMode.ON -> true
            NightMode.AUTO -> isSystemInDarkTheme()
        }

    val nightModeLiveData: LiveData<NightMode> = storage.getNightModeAsFlow().asLiveData()

    val currentNightMode: NightMode
        get() = storage.getNightMode()

    val isNightModeSupported = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q

    fun setNightMode(nightMode: NightMode) {
        if (nightMode != currentNightMode) {
            setMode(nightMode)
        }
    }

    private fun setMode(nightMode: NightMode) {
        when (nightMode) {
            NightMode.ON -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            NightMode.OFF -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            NightMode.AUTO -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }
        storage.setNightMode(nightMode)
    }

    private fun isSystemInDarkTheme(): Boolean {
        val uiMode = context.resources.configuration.uiMode
        return (uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
    }

}