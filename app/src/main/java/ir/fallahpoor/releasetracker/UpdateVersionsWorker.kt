package ir.fallahpoor.releasetracker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import ir.fallahpoor.releasetracker.common.NotificationManager
import ir.fallahpoor.releasetracker.data.entity.Library
import ir.fallahpoor.releasetracker.data.repository.LibraryRepository
import ir.fallahpoor.releasetracker.data.utils.NetworkUtils
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

    override suspend fun doWork(): Result {

        val updatedLibraries = mutableListOf<String>()
        val result: Result =
            if (!networkUtils.isNetworkReachable()) {
                Result.retry()
            } else {
                val libraries: List<Library> = libraryRepository.getLibraries()
                libraries.forEach { library: Library ->
                    val latestVersion: String? = getLatestVersion(library)
                    if (latestVersion != null) {
                        val libraryCopy = library.copy(version = latestVersion)
                        libraryRepository.updateLibrary(libraryCopy)
                        if (newVersionAvailable(latestVersion, library.version)) {
                            updatedLibraries.add("${library.name}: ${library.version} -> $latestVersion")
                        }
                    }
                }
                saveUpdateDate(libraries)
                Result.success()
            }

        if (result is Result.Success && updatedLibraries.isNotEmpty()) {
            showNotification(updatedLibraries)
        }

        return result

    }

    private suspend fun getLatestVersion(library: Library): String? {
        return try {
            val libraryVersion: String =
                libraryRepository.getLibraryVersion(library.name, library.url)
            Timber.d("Update SUCCESS (%s): %s", library.name, libraryVersion)
            libraryVersion
        } catch (t: Throwable) {
            Timber.d("Update FAILURE (%s): %s", library.name, t.message)
            null
        }
    }

    private fun newVersionAvailable(latestVersion: String?, currentVersion: String): Boolean {
        return latestVersion != null &&
                currentVersion != "N/A" &&
                latestVersion != currentVersion
    }

    private fun saveUpdateDate(libraries: List<Library>) {
        if (libraries.isNotEmpty()) {
            val simpleDateFormat = SimpleDateFormat("MMM dd HH:mm", Locale.US)
            libraryRepository.setLastUpdateCheck(simpleDateFormat.format(Date()))
        } else {
            libraryRepository.setLastUpdateCheck("N/A")
        }
    }

    private fun showNotification(updatedLibraries: List<String>) {
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