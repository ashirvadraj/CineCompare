package com.cinecompare.feature.compare

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.cinecompare.aggregator.base.models.*
import com.cinecompare.aggregator.base.ComparisonResult
import com.cinecompare.core.common.toTimeString
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompareScreen(
    movieId: String,
    onBack: () -> Unit,
    viewModel: CompareViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(movieId) { viewModel.loadForMovie(movieId) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Compare & Book", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Back") }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Date selector
            val dates = (0..6).map { LocalDate.now().plusDays(it.toLong()) }
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(dates) { date ->
                    val isSelected = state.selectedDate == date
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.selectDate(date) },
                        label = {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    date.format(DateTimeFormatter.ofPattern("EEE")),
                                    fontSize = 11.sp
                                )
                                Text(
                                    date.format(DateTimeFormatter.ofPattern("d MMM")),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    )
                }
            }

            // Format filter
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                val formats = listOf("All", "2D", "3D", "IMAX", "4DX")
                items(formats) { fmt ->
                    FilterChip(
                        selected = state.selectedFormat == fmt,
                        onClick = { viewModel.selectFormat(fmt) },
                        label = { Text(fmt) }
                    )
                }
            }

            if (state.isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                        Spacer(Modifier.height(16.dp))
                        Text("Fetching live prices from all platforms...", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            } else if (state.comparisons.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(32.dp)) {
                        Icon(
                            Icons.Default.EventBusy, null, modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(Modifier.height(16.dp))
                        Text("No shows found", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "Try selecting a different date or format",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(Modifier.height(24.dp))
                        Button(onClick = { viewModel.refresh(movieId) }) { Text("Refresh") }
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(state.comparisons) { comparison ->
                        CinemaComparisonCard(
                            comparison = comparison,
                            onBookClick = { listing ->
                                openBookingDeepLink(context, listing)
                            }
                        )
                    }
                    item { Spacer(Modifier.height(16.dp)) }
                }
            }
        }
    }
}

@Composable
fun CinemaComparisonCard(
    comparison: ComparisonResult,
    onBookClick: (ShowListing) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Cinema header
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Theaters, null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(8.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        comparison.cinema.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        comparison.cinema.address,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1
                    )
                }
                // Format badge
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = MaterialTheme.colorScheme.secondaryContainer
                ) {
                    Text(
                        comparison.format,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.AccessTime, null, modifier = Modifier.size(14.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    comparison.showTime.toTimeString(),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    "  ·  ${comparison.language}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(Modifier.height(12.dp))
            HorizontalDivider()
            Spacer(Modifier.height(12.dp))

            // Savings banner
            if (comparison.hasDifference && comparison.savingsAmount > 0) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFF1B5E20)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Savings, null,
                            tint = Color(0xFF69F0AE), modifier = Modifier.size(16.dp)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            "₹${comparison.savingsAmount / 100} cheaper on ${comparison.cheapestListing?.platform?.displayName}",
                            color = Color(0xFF69F0AE),
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
                Spacer(Modifier.height(10.dp))
            }

            // Platform rows
            comparison.listings.sortedBy { it.priceRange.minPricePaise }.forEach { listing ->
                PlatformPriceRow(
                    listing = listing,
                    isCheapest = listing == comparison.cheapestListing,
                    onBook = { onBookClick(listing) }
                )
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun PlatformPriceRow(
    listing: ShowListing,
    isCheapest: Boolean,
    onBook: () -> Unit
) {
    val platformColor = Color(listing.platform.primaryColor)
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        color = if (isCheapest)
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
        else
            MaterialTheme.colorScheme.surfaceVariant
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Platform indicator bar
            Surface(
                modifier = Modifier.size(8.dp, 32.dp),
                shape = RoundedCornerShape(4.dp),
                color = platformColor
            ) {}

            Spacer(Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        listing.platform.displayName,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    if (isCheapest) {
                        Spacer(Modifier.width(6.dp))
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = Color(0xFFFFD700)
                        ) {
                            Text(
                                "✓ Cheapest",
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                style = MaterialTheme.typography.labelMedium,
                                color = Color.Black,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
                // Seat availability
                val availText = when (listing.availability) {
                    SeatAvailability.HOUSEFULL -> "🔴 Housefull"
                    SeatAvailability.ALMOST_FULL -> "🟠 Almost Full"
                    SeatAvailability.FILLING_FAST -> "🟡 Filling Fast!"
                    SeatAvailability.AVAILABLE -> "🟢 Available"
                }
                Text(
                    availText,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Price
            Text(
                listing.priceRange.displayString,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(Modifier.width(10.dp))

            // Book Button
            if (listing.availability != SeatAvailability.HOUSEFULL) {
                Button(
                    onClick = onBook,
                    colors = ButtonDefaults.buttonColors(containerColor = platformColor),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Text("Book", color = Color.White, style = MaterialTheme.typography.labelMedium)
                }
            } else {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.errorContainer
                ) {
                    Text(
                        "Full",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }
        }
    }
}

fun openBookingDeepLink(context: Context, listing: ShowListing) {
    try {
        val appIntent = Intent(Intent.ACTION_VIEW, Uri.parse(listing.deepLinkUrl)).apply {
            setPackage(listing.platform.packageName)
        }
        context.startActivity(appIntent)
    } catch (e: ActivityNotFoundException) {
        // Fallback to browser
        try {
            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(listing.deepLinkUrl)))
        } catch (e2: Exception) { /* ignore */ }
    }
}
