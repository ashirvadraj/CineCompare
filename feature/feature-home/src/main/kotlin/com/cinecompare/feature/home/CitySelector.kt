package com.cinecompare.feature.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.cinecompare.aggregator.base.models.City

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CitySelector(
    cities: List<City>,
    onCitySelected: (City) -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "Select Your City",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            LazyColumn {
                items(cities) { city ->
                    ListItem(
                        headlineContent = { Text(city.name) },
                        supportingContent = { Text(city.state) },
                        leadingContent = {
                            Icon(Icons.Default.LocationCity, null,
                                tint = MaterialTheme.colorScheme.primary)
                        },
                        modifier = Modifier.clickable { onCitySelected(city) }
                    )
                    HorizontalDivider()
                }
            }
            Spacer(Modifier.height(32.dp))
        }
    }
}
