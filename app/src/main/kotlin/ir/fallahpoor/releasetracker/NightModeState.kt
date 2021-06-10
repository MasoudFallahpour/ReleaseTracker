package ir.fallahpoor.releasetracker

import ir.fallahpoor.releasetracker.data.utils.NightMode

class NightModeState(
    val isNightModeSupported: Boolean,
    val isNightModeOn: Boolean,
    val currentNightMode: NightMode
)