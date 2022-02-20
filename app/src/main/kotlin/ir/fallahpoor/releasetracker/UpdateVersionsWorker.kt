package ir.fallahpoor.releasetracker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import ir.fallahpoor.releasetracker.common.managers.NotificationManager
import ir.fallahpoor.releasetracker.data.entity.Library
import ir.fallahpoor.releasetracker.data.repository.LibraryRepository
import ir.fallahpoor.releasetracker.data.utils.NetworkUtils
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*

@HiltWorker
class UpdateVersionsWorker
@AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workerParams: WorkerParameters,
    private val libraryRepository: LibraryRepository,
    private val networkUtils: NetworkUtils,
    private val notificationManager: NotificationManager
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result =
        if (!networkUtils.isNetworkReachable()) {
            Result.retry()
        } else {
            val updatedLibraries: List<String> = updateLibraries()
            saveUpdateDate()
            showNotification(updatedLibraries)
            Result.success()
        }

    private suspend fun updateLibraries(): List<String> {

        val updatedLibraries = mutableListOf<String>()
        val libraries: List<Library> = libraryRepository.getLibraries()

        coroutineScope {
            libraries.forEach { library: Library ->
                launch {
                    val latestVersion: String? = getLatestVersion(library)
                    latestVersion?.let {
                        val libraryCopy = library.copy(version = it)
                        libraryRepository.updateLibrary(libraryCopy)
                        if (newVersionAvailable(it, library.version)) {
                            updatedLibraries += "${library.name}: ${library.version} -> $it"
                        }
                    }
                }
            }
        }

        return updatedLibraries

    }

    private suspend fun getLatestVersion(library: Library): String? =
        try {
            val libraryVersion: String =
                libraryRepository.getLibraryVersion(library.name, library.url)
            Timber.d("Update SUCCESS (%s): %s", library.name, libraryVersion)
            libraryVersion
        } catch (t: Throwable) {
            Timber.d("Update FAILURE (%s): %s", library.name, t.message)
            null
        }

    private fun newVersionAvailable(latestVersion: String?, currentVersion: String): Boolean =
        latestVersion != null &&
                currentVersion != "N/A" &&
                latestVersion != currentVersion

    private fun saveUpdateDate() {
        val simpleDateFormat = SimpleDateFormat("MMM dd HH:mm", Locale.US)
        libraryRepository.setLastUpdateCheck(simpleDateFormat.format(Date()))
    }

    private fun showNotification(updatedLibraries: List<String>) {
        if (updatedLibraries.isNotEmpty()) {
            val notificationBody = context.getString(
                R.string.notification_body,
                updatedLibraries.joinToString(separator = "\n")
            )
            notificationManager.showNotification(
                title = context.getString(R.string.notification_title),
                body = notificationBody
            )
        }
    }

}