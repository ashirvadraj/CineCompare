package com.cinecompare.app

import android.app.Application
import androidx.work.*
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import java.util.concurrent.TimeUnit

@HiltAndroidApp
class CineCompareApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        schedulePriceMonitor()
    }

    private fun schedulePriceMonitor() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        val workRequest = PeriodicWorkRequestBuilder<com.cinecompare.feature.profile.PriceMonitorWorker>(
            15, TimeUnit.MINUTES
        ).setConstraints(constraints).build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "price_monitor",
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }
}
