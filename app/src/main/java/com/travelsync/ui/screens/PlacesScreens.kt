package com.travelsync.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.travelsync.core.domain.model.Place
import com.travelsync.core.domain.model.PlaceCategory
import com.travelsync.ui.components.CategoryChip
import com.travelsync.ui.components.GradientBackground
import com.travelsync.ui.components.PlaceMap
import com.travelsync.ui.components.TravelSyncTopBar
import com.travelsync.ui.util.dialPhone
import com.travelsync.ui.util.openExternalLink
import com.travelsync.ui.util.openPlaceInMaps

@Composable
fun PlacesScreen(
    places: List<Place>,
    tripDays: List<com.travelsync.core.domain.model.TripDay>,
    destination: String,
    onAddPlaceClick: () -> Unit,
    onDeletePlace: (String) -> Unit,
    onAddToItinerary: (Place, String) -> Unit,
    onBackClick: () -> Unit
) {
    var selectedCategory by remember { mutableStateOf<PlaceCategory?>(null) }
    var placeForDayPicker by remember { mutableStateOf<Place?>(null) }
    var showMap by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val filtered = remember(places, selectedCategory) {
        places.filter { place -> selectedCategory == null || place.category == selectedCategory }
    }

    GradientBackground {
        Scaffold(
            topBar = {
                TravelSyncTopBar(
                    title = "Places near $destination",
                    onBackClick = onBackClick,
                    actions = {
                        IconButton(onClick = { showMap = !showMap }) {
                            Icon(
                                imageVector = if (showMap) Icons.AutoMirrored.Filled.List else Icons.Default.Map,
                                contentDescription = if (showMap) "Show list" else "Show map"
                            )
                        }
                    }
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = onAddPlaceClick,
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White,
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add place")
                }
            },
            containerColor = Color.Transparent
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        FilterChip(
                            selected = selectedCategory == null,
                            onClick = { selectedCategory = null },
                            label = { Text("All") }
                        )
                    }
                    items(PlaceCategory.entries) { category ->
                        FilterChip(
                            selected = selectedCategory == category,
                            onClick = { selectedCategory = category },
                            label = { Text(category.name.lowercase().replaceFirstChar { it.uppercase() }) }
                        )
                    }
                }

                if (showMap) {
                    PlaceMap(places = filtered, modifier = Modifier.fillMaxSize())
                } else if (filtered.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No places yet. Add your own or use bundled suggestions.")
                    }
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(filtered, key = { it.id }) { place ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(text = place.name, style = MaterialTheme.typography.titleMedium)
                                        if (place.tripId != null) {
                                            IconButton(onClick = { onDeletePlace(place.id) }) {
                                                Icon(Icons.Default.Delete, contentDescription = "Delete")
                                            }
                                        }
                                    }
                                    if (place.description.isNotEmpty()) {
                                        Text(
                                            text = place.description,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        CategoryChip(
                                            label = place.category.name,
                                            backgroundColor = MaterialTheme.colorScheme.secondaryContainer,
                                            textColor = MaterialTheme.colorScheme.onSecondaryContainer
                                        )
                                        if (place.priceNote.isNotEmpty()) {
                                            Text(
                                                text = place.priceNote,
                                                style = MaterialTheme.typography.labelMedium,
                                                color = MaterialTheme.colorScheme.tertiary
                                            )
                                        }
                                        place.rating?.let { rating ->
                                            Text(
                                                text = "${rating} rating",
                                                style = MaterialTheme.typography.labelMedium
                                            )
                                        }
                                    }
                                    if (place.address.isNotEmpty()) {
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = place.address,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        OutlinedButton(onClick = { openPlaceInMaps(context, place) }) {
                                            Icon(Icons.Default.Map, contentDescription = null, modifier = Modifier.size(18.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Navigate")
                                        }
                                        if (tripDays.isNotEmpty()) {
                                            OutlinedButton(onClick = { placeForDayPicker = place }) {
                                                Icon(Icons.Default.Event, contentDescription = null, modifier = Modifier.size(18.dp))
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("Add to day")
                                            }
                                        }
                                        place.externalLink?.let { link ->
                                            OutlinedButton(onClick = { openExternalLink(context, link) }) {
                                                Icon(Icons.Default.OpenInNew, contentDescription = null, modifier = Modifier.size(18.dp))
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("Book / Info")
                                            }
                                        }
                                        place.phone?.let { phone ->
                                            OutlinedButton(onClick = { dialPhone(context, phone) }) {
                                                Icon(Icons.Default.Phone, contentDescription = null, modifier = Modifier.size(18.dp))
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    placeForDayPicker?.let { place ->
        AlertDialog(
            onDismissRequest = { placeForDayPicker = null },
            title = { Text("Add to itinerary") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Choose a day for ${place.name}")
                    tripDays.forEach { day ->
                        TextButton(onClick = {
                            onAddToItinerary(place, day.id)
                            placeForDayPicker = null
                        }) {
                            Text(day.label)
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { placeForDayPicker = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun AddPlaceScreen(
    tripId: String,
    destination: String,
    onPlaceSaved: (Place) -> Unit,
    onBackClick: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var priceNote by remember { mutableStateOf("") }
    var externalLink by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(PlaceCategory.ATTRACTION) }

    GradientBackground {
        Scaffold(
            topBar = {
                TravelSyncTopBar(title = "Add Place", onBackClick = onBackClick)
            },
            containerColor = Color.Transparent
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text("Address") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Notes") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    minLines = 2
                )
                OutlinedTextField(
                    value = priceNote,
                    onValueChange = { priceNote = it },
                    label = { Text("Price (e.g. Free, 1500 JPY)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                OutlinedTextField(
                    value = externalLink,
                    onValueChange = { externalLink = it },
                    label = { Text("Booking link (optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                Text("Category", style = MaterialTheme.typography.titleSmall)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(PlaceCategory.entries) { category ->
                        FilterChip(
                            selected = selectedCategory == category,
                            onClick = { selectedCategory = category },
                            label = { Text(category.name.lowercase().replaceFirstChar { it.uppercase() }) }
                        )
                    }
                }
                Spacer(modifier = Modifier.weight(1f))
                Button(
                    onClick = {
                        if (name.isNotBlank()) {
                            onPlaceSaved(
                                Place(
                                    tripId = tripId,
                                    destinationTag = destination.split(",").first().trim(),
                                    name = name.trim(),
                                    description = description.trim(),
                                    address = address.trim(),
                                    category = selectedCategory,
                                    priceNote = priceNote.trim(),
                                    externalLink = externalLink.trim().ifEmpty { null }
                                )
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(bottom = 16.dp)
                        .height(52.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Save Place")
                }
            }
        }
    }
}
