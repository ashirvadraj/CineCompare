package com.cinecompare.feature.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieDetailScreen(
    movieId: String,
    onCompareClick: (String) -> Unit,
    onBack: () -> Unit,
    viewModel: MovieDetailViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(movieId) { viewModel.loadMovie(movieId) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(state.movie?.title ?: "Movie Detail") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.toggleWatchlist(movieId) }) {
                        Icon(
                            if (state.isWatchlisted) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = "Watchlist",
                            tint = if (state.isWatchlisted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            )
        },
        bottomBar = {
            val movie = state.movie
            if (movie != null) {
                Surface(shadowElevation = 8.dp) {
                    Button(
                        onClick = { onCompareClick(movieId) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .height(52.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Icon(Icons.Default.CompareArrows, null)
                        Spacer(Modifier.width(8.dp))
                        Text("Compare Prices & Book", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            val movie = state.movie
            if (movie != null) {
                // Poster
                Box {
                    AsyncImage(
                        model = movie.bannerUrl.ifEmpty { movie.posterUrl },
                        contentDescription = movie.title,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp),
                        contentScale = ContentScale.Crop
                    )
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .height(250.dp)
                    )
                }

                Column(Modifier.padding(16.dp)) {
                    Text(
                        movie.title,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(4.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Star,
                            null,
                            tint = Color(0xFFFFD700),
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            " ${movie.rating}/10  · ${movie.certification}  · ${movie.durationMinutes} min",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    Spacer(Modifier.height(8.dp))
                    Row {
                        for (genre in movie.genres.take(3)) {
                            AssistChip(
                                onClick = {},
                                label = { Text(genre) },
                                modifier = Modifier.padding(end = 6.dp)
                            )
                        }
                    }

                    Spacer(Modifier.height(12.dp))
                    Text(
                        "About the Film",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        movie.synopsis.ifEmpty { "An exciting movie that you won't want to miss!" },
                        style = MaterialTheme.typography.bodyMedium
                    )

                    if (movie.languages.isNotEmpty()) {
                        Spacer(Modifier.height(12.dp))
                        Text(
                            "Languages: ${movie.languages.joinToString(", ")}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    if (movie.formats.isNotEmpty()) {
                        Spacer(Modifier.height(4.dp))
                        Row {
                            for (fmt in movie.formats) {
                                SuggestionChip(
                                    onClick = {},
                                    label = { Text(fmt) },
                                    modifier = Modifier.padding(end = 6.dp)
                                )
                            }
                        }
                    }
                }
            } else if (state.isLoading) {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(300.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}
