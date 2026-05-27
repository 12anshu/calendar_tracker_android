package com.example.smartexpensecalendar.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.smartexpensecalendar.R

object NotificationHelper {
    private const val CHANNEL_ID = "expense_sync_channel"
    private const val CHANNEL_NAME = "Expense Sync Alerts"
    private const val CHANNEL_DESC = "Notifications for new expenses detected from SMS"

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                description = CHANNEL_DESC
            }
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun showExpenseNotification(context: Context, amount: Double, merchant: String?, category: String) {
        val merchantDisplay = merchant ?: "Unknown Merchant"
        val title = "New Expense Detected"
        val message = "Spent INR $amount at $merchantDisplay ($category)"

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground) // Using existing icon
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

        try {
            with(NotificationManagerCompat.from(context)) {
                notify(System.currentTimeMillis().toInt(), builder.build())
            }
        } catch (e: SecurityException) {
            // Permission not granted
        }
    }

    fun showSyncCompleteNotification(context: Context, monthYear: String, count: Int) {
        val title = "Sync Complete"
        val message = "Successfully synced $count expenses for $monthYear"

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

        try {
            with(NotificationManagerCompat.from(context)) {
                notify(2, builder.build())
            }
        } catch (e: SecurityException) {
            // Permission not granted
        }
    }
}
