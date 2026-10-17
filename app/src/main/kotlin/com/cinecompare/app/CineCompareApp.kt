package com.cinecompare.app

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.*
import com.cinecompare.feature.compare.CompareScreen
import com.cinecompare.feature.home.HomeScreen
import com.cinecompare.feature.home.MovieDetailScreen
import com.cinecompare.feature.profile.ProfileScreen
import com.cinecompare.feature.search.SearchScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CineCompareApp() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val bottomNavItems = listOf(
        BottomNavItem("home", "Home", Icons.Default.Home),
        BottomNavItem("search", "Search", Icons.Default.Search),
        BottomNavItem("compare", "Compare", Icons.Default.CompareArrows),
        BottomNavItem("profile", "Profile", Icons.Default.Person)
    )

    Scaffold(
        bottomBar = {
            NavigationBar {
                bottomNavItems.forEach { item ->
                    NavigationBarItem(
                        icon = { Icon(item.icon, contentDescription = item.label) },
                        label = { Text(item.label) },
                        selected = currentRoute == item.route,
                        onClick = {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("home") {
                HomeScreen(
                    onMovieClick = { movieId ->
                        navController.navigate("movie_detail/$movieId")
                    }
                )
            }
            composable("movie_detail/{movieId}") { backStack ->
                val movieId = backStack.arguments?.getString("movieId") ?: ""
                MovieDetailScreen(
                    movieId = movieId,
                    onCompareClick = { navController.navigate("compare/$movieId") },
                    onBack = { navController.popBackStack() }
                )
            }
            composable("compare/{movieId}") { backStack ->
                val movieId = backStack.arguments?.getString("movieId") ?: ""
                CompareScreen(
                    movieId = movieId,
                    onBack = { navController.popBackStack() }
                )
            }
            composable("search") {
                SearchScreen(
                    onMovieClick = { movieId ->
                        navController.navigate("movie_detail/$movieId")
                    }
                )
            }
            composable("profile") {
                ProfileScreen(
                    onMovieClick = { movieId ->
                        navController.navigate("movie_detail/$movieId")
                    }
                )
            }
        }
    }
}

data class BottomNavItem(
    val route: String,
    val label: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)
