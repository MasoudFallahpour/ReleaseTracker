package ir.fallahpoor.releasetracker.data.repository

import ir.fallahpoor.releasetracker.data.utils.NightMode
import kotlinx.coroutines.flow.Flow

interface NightModeRepository {

    suspend fun setNightMode(nightMode: NightMode)

    fun getNightMode(): NightMode

    fun getNightModeAsFlow(): Flow<NightMode>

}