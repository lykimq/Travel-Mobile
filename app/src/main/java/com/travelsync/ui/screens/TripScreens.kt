package com.travelsync.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.automirrored.filled.ListAlt
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.travelsync.core.domain.model.*
import com.travelsync.ui.components.CategoryChip
import com.travelsync.ui.components.GradientBackground
import com.travelsync.ui.components.TravelSyncTopBar
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun TripListScreen(
    trips: List<Trip>,
    onTripClick: (String) -> Unit,
    onCreateTripClick: () -> Unit,
    onDeleteTrip: (String) -> Unit,
    onBackClick: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val filteredTrips = trips.filter {
        it.title.contains(searchQuery, ignoreCase = true) ||
            it.destination.contains(searchQuery, ignoreCase = true)
    }

    GradientBackground {
        Scaffold(
            topBar = { TravelSyncTopBar(title = "My Trips", onBackClick = onBackClick) },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = onCreateTripClick,
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White,
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Create Trip")
                }
            },
            containerColor = Color.Transparent
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search trips...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    shape = RoundedCornerShape(16.dp)
                )

                if (filteredTrips.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No trips found.")
                    }
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(1),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(filteredTrips) { trip ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onTripClick(trip.id) },
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(56.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(MaterialTheme.colorScheme.primaryContainer),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(Icons.Default.FlightTakeoff, contentDescription = null)
                                    }
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(text = trip.title, style = MaterialTheme.typography.titleMedium)
                                        Text(
                                            text = trip.destination,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                        )
                                        CategoryChip(
                                            label = trip.type.name,
                                            backgroundColor = MaterialTheme.colorScheme.secondaryContainer,
                                            textColor = MaterialTheme.colorScheme.onSecondaryContainer
                                        )
                                    }
                                    IconButton(onClick = { onDeleteTrip(trip.id) }) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete trip")
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

@Composable
fun TripDetailsScreen(
    trip: Trip,
    onTabSelected: (String) -> Unit,
    onEditTrip: () -> Unit,
    onDeleteTrip: () -> Unit,
    onBackClick: () -> Unit
) {
    val sdf = SimpleDateFormat("dd MMM", Locale.getDefault())

    GradientBackground {
        Scaffold(
            topBar = {
                TravelSyncTopBar(
                    title = trip.title,
                    onBackClick = onBackClick,
                    actions = {
                        IconButton(onClick = onEditTrip) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit trip")
                        }
                        IconButton(onClick = onDeleteTrip) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete trip")
                        }
                    }
                )
            },
            containerColor = Color.Transparent
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = trip.destination,
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = "${sdf.format(trip.startDate)} - ${sdf.format(trip.endDate)}",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f)
                        )
                        trip.budgetAmount?.let { budget ->
                            Text(
                                text = "Budget: ${trip.currency} ${String.format(Locale.US, "%.0f", budget)}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                            )
                        }
                        if (trip.notes.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = trip.notes,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                            )
                        }
                    }
                }

                Text(
                    text = "Trip Tools",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                )

                val workspaceItems = listOf(
                    Triple("itinerary", Icons.Default.CalendarToday, "Daily Itinerary"),
                    Triple("places", Icons.Default.Place, "Places & POI"),
                    Triple("budget", Icons.Default.PriceCheck, "Trip Budget"),
                    Triple("packing", Icons.AutoMirrored.Filled.ListAlt, "Packing List")
                )

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(workspaceItems.size) { index ->
                        val (tab, icon, label) = workspaceItems[index]
                        WorkspaceToolCard(icon = icon, label = label) {
                            onTabSelected(tab)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun WorkspaceToolCard(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = label, style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
            Icon(
                Icons.AutoMirrored.Filled.ArrowForwardIos,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
fun DailyItineraryScreen(
    trip: Trip,
    tripDays: List<TripDay>,
    activities: List<Activity>,
    onActivityClick: (String) -> Unit,
    onAddActivityClick: (String) -> Unit,
    onBackClick: () -> Unit
) {
    val days = tripDays.ifEmpty { listOf(TripDay(tripId = trip.id, dayNumber = 1)) }
    var selectedDayId by remember(days) { mutableStateOf(days.first().id) }
    val filteredActivities = activities.filter { it.tripDayId == selectedDayId }
    val tf = SimpleDateFormat("hh:mm a", Locale.getDefault())

    GradientBackground {
        Scaffold(
            topBar = {
                TravelSyncTopBar(
                    title = "${trip.title} Itinerary",
                    onBackClick = onBackClick
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { onAddActivityClick(selectedDayId) },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White,
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Activity")
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
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(days, key = { it.id }) { day ->
                        val isSelected = selectedDayId == day.id
                        val containerColor = if (isSelected) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.surface
                        }
                        val contentColor = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface

                        Card(
                            onClick = { selectedDayId = day.id },
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = containerColor)
                        ) {
                            Text(
                                text = day.label,
                                style = MaterialTheme.typography.bodyLarge,
                                color = contentColor,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
                            )
                        }
                    }
                }

                if (filteredActivities.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No activities planned for this day yet.")
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(filteredActivities, key = { it.id }) { activity ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onActivityClick(activity.id) },
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp)
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier.width(68.dp)
                                    ) {
                                        Text(
                                            text = tf.format(activity.startTime),
                                            style = MaterialTheme.typography.titleMedium,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        Text(
                                            text = tf.format(activity.endTime),
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(text = activity.title, style = MaterialTheme.typography.titleMedium)
                                        Text(
                                            text = activity.locationName,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        CategoryChip(
                                            label = activity.category.name,
                                            backgroundColor = MaterialTheme.colorScheme.secondaryContainer,
                                            textColor = MaterialTheme.colorScheme.onSecondaryContainer
                                        )
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
