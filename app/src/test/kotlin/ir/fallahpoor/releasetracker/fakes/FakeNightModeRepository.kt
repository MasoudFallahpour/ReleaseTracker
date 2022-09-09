package ir.fallahpoor.releasetracker.fakes

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asFlow
import ir.fallahpoor.releasetracker.data.repository.nightmode.NightModeRepository
import ir.fallahpoor.releasetracker.data.utils.NightMode
import kotlinx.coroutines.flow.Flow

class FakeNightModeRepository : NightModeRepository {

    private val nightModeLiveData = MutableLiveData(NightMode.AUTO)

    override fun getNightModeAsFlow(): Flow<NightMode> = nightModeLiveData.asFlow()

    override fun getNightMode() = nightModeLiveData.value!!

    override suspend fun setNightMode(nightMode: NightMode) {
        nightModeLiveData.value = nightMode
    }

}