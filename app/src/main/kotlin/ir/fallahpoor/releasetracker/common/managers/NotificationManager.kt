package ir.fallahpoor.releasetracker.common.managers

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import ir.fallahpoor.releasetracker.MainActivity
import ir.fallahpoor.releasetracker.R
import javax.inject.Inject

@ExperimentalAnimationApi
class NotificationManager
@Inject constructor(
    private val context: Context
) {

    companion object {
        private const val CHANNEL_ID = "general_channel"
    }

    fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelName = context.getString(R.string.notification_channel_name)
            val channelDescription = context.getString(R.string.notification_channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val notificationChannel =
                NotificationChannel(CHANNEL_ID, channelName, importance).apply {
                    description = channelDescription
                }
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    fun showNotification(title: String, body: String) {
        val notification: Notification = createNotification(title, body)
        NotificationManagerCompat.from(context)
            .notify(0, notification)
    }

    private fun createNotification(title: String, content: String): Notification =
        NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(content)
            .setStyle(NotificationCompat.BigTextStyle().bigText(content))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(createContentIntent())
            .setAutoCancel(true)
            .build()

    private fun createContentIntent(): PendingIntent {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        return PendingIntent.getActivity(context, 0, intent, 0)
    }

}