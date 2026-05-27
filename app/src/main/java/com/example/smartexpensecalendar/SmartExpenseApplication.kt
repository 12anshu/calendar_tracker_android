package com.example.smartexpensecalendar

import android.app.Application
import com.example.smartexpensecalendar.utils.NotificationHelper
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class SmartExpenseApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        NotificationHelper.createNotificationChannel(this)
    }
}
