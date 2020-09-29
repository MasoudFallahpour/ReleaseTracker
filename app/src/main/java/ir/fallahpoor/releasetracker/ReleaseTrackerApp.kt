package ir.fallahpoor.releasetracker

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.*
import dagger.hilt.android.HiltAndroidApp
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltAndroidApp
class ReleaseTrackerApp : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override fun onCreate() {
        super.onCreate()
        startWorkManager()
    }

    private fun startWorkManager() {

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresBatteryNotLow(true)
            .build()

        val workRequest = PeriodicWorkRequestBuilder<UpdateVersionsWorker>(30, TimeUnit.SECONDS)
            .setConstraints(constraints)
            .setBackoffCriteria(
                BackoffPolicy.LINEAR,
                1,
                TimeUnit.HOURS
            )
            .setInitialDelay(30, TimeUnit.SECONDS)
            .addTag(getString(R.string.worker_tag))
            .build()

        WorkManager.getInstance(this)
            .enqueueUniquePeriodicWork(
                getString(R.string.worker_tag),
                ExistingPeriodicWorkPolicy.REPLACE,
                workRequest
            )

    }

    override fun getWorkManagerConfiguration(): Configuration =
        Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

}