package com.cinecompare.feature.profile

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.*
import com.cinecompare.aggregator.base.MovieAggregator
import com.cinecompare.aggregator.base.models.POPULAR_CITIES
import com.cinecompare.core.database.dao.MovieDao
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.time.LocalDate

@HiltWorker
class PriceMonitorWorker @AssistedInject constructor(
    @Assisted private val appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val aggregators: Set<@JvmSuppressWildcards MovieAggregator>,
    private val movieDao: MovieDao
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val alertMovies = movieDao.getPriceAlertMovies()
        val defaultCity = POPULAR_CITIES.first()

        alertMovies.forEach { movie ->
            aggregators.forEach { aggregator ->
                try {
                    val listings = aggregator.getShowListings(
                        movie.id, defaultCity, LocalDate.now()
                    ).getOrNull() ?: return@forEach

                    val cheapest = listings.minByOrNull { it.priceRange.minPricePaise }
                    if (cheapest != null) {
                        sendPriceNotification(
                            title = movie.title,
                            price = cheapest.priceRange.displayString,
                            platform = cheapest.platform.displayName
                        )
                    }
                } catch (e: Exception) { /* ignore per-platform network errors */ }
            }
        }
        return Result.success()
    }

    private fun sendPriceNotification(title: String, price: String, platform: String) {
        val manager = appContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "price_alerts"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            manager.createNotificationChannel(
                NotificationChannel(channelId, "Price Alerts", NotificationManager.IMPORTANCE_DEFAULT)
            )
        }

        val notification = NotificationCompat.Builder(appContext, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("🎫 Price Alert: $title")
            .setContentText("Tickets from $price on $platform")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        manager.notify(title.hashCode(), notification)
    }
}
