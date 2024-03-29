package ir.fallahpoor.releasetracker.data.repository.nightmode

import ir.fallahpoor.releasetracker.data.NightMode
import kotlinx.coroutines.flow.Flow

interface NightModeRepository {

    suspend fun setNightMode(nightMode: NightMode)

    fun getNightMode(): NightMode

    fun getNightModeAsFlow(): Flow<NightMode>

}