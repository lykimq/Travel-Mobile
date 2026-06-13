package com.travelsync.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.runtime.collectAsState
import com.travelsync.core.domain.model.*
import com.travelsync.ui.components.CategoryChip
import com.travelsync.ui.components.GradientBackground
import com.travelsync.ui.components.ReferenceDropdownField
import com.travelsync.ui.components.TravelSyncTopBar
import com.travelsync.ui.viewmodel.TripFormViewModel
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun TripFormScreen(
    title: String,
    initialTrip: Trip?,
    onSubmit: (Trip) -> Unit,
    onBackClick: () -> Unit,
    viewModel: TripFormViewModel = hiltViewModel()
) {
    var tripTitle by remember(initialTrip) { mutableStateOf(initialTrip?.title ?: "") }
    var notes by remember(initialTrip) { mutableStateOf(initialTrip?.notes ?: "") }
    var budgetText by remember(initialTrip) {
        mutableStateOf(initialTrip?.budgetAmount?.toString() ?: "")
    }
    var selectedType by remember(initialTrip) {
        mutableStateOf(initialTrip?.type ?: TripType.LEISURE)
    }
    val startDate = remember(initialTrip) {
        mutableStateOf(initialTrip?.startDate ?: Date())
    }
    val endDate = remember(initialTrip) {
        mutableStateOf(initialTrip?.endDate ?: Date(System.currentTimeMillis() + 86400000 * 7))
    }

    val regions by viewModel.regions.collectAsState()
    val countries by viewModel.countries.collectAsState()
    val cities by viewModel.cities.collectAsState()
    val currencies by viewModel.currencies.collectAsState()
    val selectedRegionCode by viewModel.selectedRegionCode.collectAsState()
    val selectedCountryCode by viewModel.selectedCountryCode.collectAsState()
    val selectedCityId by viewModel.selectedCityId.collectAsState()
    val selectedCurrencyCode by viewModel.selectedCurrencyCode.collectAsState()

    LaunchedEffect(initialTrip?.id) {
        viewModel.initializeFromTrip(initialTrip)
    }

    val scope = rememberCoroutineScope()
    val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    val regionNames = regions.map { it.name }
    val regionCodes = regions.map { it.code }
    val countryNames = countries.map { it.name }
    val countryCodes = countries.map { it.code }
    val cityNames = cities.map { it.name }
    val cityIds = cities.map { it.id }
    val currencyCodes = currencies.map { it.code }
    val currencyLabels = currencies.map { it.label }

    GradientBackground {
        Scaffold(
            topBar = { TravelSyncTopBar(title = title, onBackClick = onBackClick) },
            containerColor = Color.Transparent
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = tripTitle,
                    onValueChange = { tripTitle = it },
                    label = { Text("Trip title") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                ReferenceDropdownField(
                    label = "Region",
                    value = selectedRegionCode.orEmpty(),
                    options = regionCodes,
                    optionLabels = regionNames,
                    onSelected = viewModel::onRegionSelected
                )
                ReferenceDropdownField(
                    label = "Country",
                    value = selectedCountryCode.orEmpty(),
                    options = countryCodes,
                    optionLabels = countryNames,
                    enabled = selectedRegionCode != null && countries.isNotEmpty(),
                    onSelected = viewModel::onCountrySelected
                )
                ReferenceDropdownField(
                    label = "City",
                    value = selectedCityId.orEmpty(),
                    options = cityIds,
                    optionLabels = cityNames,
                    enabled = selectedCountryCode != null && cities.isNotEmpty(),
                    onSelected = { cityId ->
                        cities.firstOrNull { it.id == cityId }?.let(viewModel::onCitySelected)
                    }
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Card(modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp)) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("Starts", style = MaterialTheme.typography.bodySmall)
                            Text(sdf.format(startDate.value), style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                    Card(modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp)) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("Ends", style = MaterialTheme.typography.bodySmall)
                            Text(sdf.format(endDate.value), style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
                ReferenceDropdownField(
                    label = "Currency",
                    value = selectedCurrencyCode,
                    options = currencyCodes,
                    optionLabels = currencyLabels,
                    onSelected = viewModel::onCurrencySelected
                )
                OutlinedTextField(
                    value = budgetText,
                    onValueChange = { budgetText = it },
                    label = { Text("Budget (optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notes") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    minLines = 3
                )
                Button(
                    onClick = {
                        if (tripTitle.isBlank()) return@Button
                        scope.launch {
                            val location = viewModel.resolveSelection() ?: return@launch
                            onSubmit(
                                (initialTrip ?: Trip()).copy(
                                    title = tripTitle.trim(),
                                    cityId = location.cityId,
                                    destination = location.destination,
                                    destinationCountry = location.destinationCountry,
                                    startDate = startDate.value,
                                    endDate = endDate.value,
                                    currency = location.currency,
                                    budgetAmount = budgetText.toDoubleOrNull(),
                                    notes = notes.trim(),
                                    type = selectedType
                                )
                            )
                        }
                    },
                    enabled = tripTitle.isNotBlank() && selectedRegionCode != null &&
                        selectedCountryCode != null && selectedCityId != null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(if (initialTrip == null) "Create Trip" else "Save Changes")
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun ActivityEditorScreen(
    initialActivity: Activity,
    tripDays: List<TripDay>,
    onSave: (Activity) -> Unit,
    onDelete: (() -> Unit)?,
    onBackClick: () -> Unit
) {
    var title by remember(initialActivity.id) { mutableStateOf(initialActivity.title) }
    var locationName by remember(initialActivity.id) { mutableStateOf(initialActivity.locationName) }
    var description by remember(initialActivity.id) { mutableStateOf(initialActivity.description) }
    var notes by remember(initialActivity.id) { mutableStateOf(initialActivity.notes) }
    var costText by remember(initialActivity.id) {
        mutableStateOf(
            if (initialActivity.estimatedCost > 0) initialActivity.estimatedCost.toString() else ""
        )
    }
    var selectedDayId by remember(initialActivity.id) {
        mutableStateOf(initialActivity.tripDayId.ifEmpty { tripDays.firstOrNull()?.id.orEmpty() })
    }
    var category by remember(initialActivity.id) { mutableStateOf(initialActivity.category) }
    var status by remember(initialActivity.id) { mutableStateOf(initialActivity.status) }

    GradientBackground {
        Scaffold(
            topBar = {
                TravelSyncTopBar(
                    title = if (initialActivity.id.isEmpty()) "New Activity" else "Edit Activity",
                    onBackClick = onBackClick,
                    actions = {
                        if (onDelete != null) {
                            IconButton(onClick = onDelete) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete activity")
                            }
                        }
                    }
                )
            },
            containerColor = Color.Transparent
        ) { padding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Title") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
                item {
                    OutlinedTextField(
                        value = locationName,
                        onValueChange = { locationName = it },
                        label = { Text("Location") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
                item {
                    Text("Day", style = MaterialTheme.typography.titleSmall)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        tripDays.forEach { day ->
                            FilterChip(
                                selected = selectedDayId == day.id,
                                onClick = { selectedDayId = day.id },
                                label = { Text(day.label) }
                            )
                        }
                    }
                }
                item {
                    Text("Category", style = MaterialTheme.typography.titleSmall)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        ActivityCategory.entries.take(4).forEach { item ->
                            FilterChip(
                                selected = category == item,
                                onClick = { category = item },
                                label = { Text(item.name.lowercase().replaceFirstChar { c -> c.uppercase() }) }
                            )
                        }
                    }
                }
                item {
                    OutlinedTextField(
                        value = costText,
                        onValueChange = { costText = it },
                        label = { Text("Estimated cost") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
                item {
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Description") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        minLines = 2
                    )
                }
                item {
                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text("Notes") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        minLines = 2
                    )
                }
                item {
                    Button(
                        onClick = {
                            if (title.isNotBlank() && selectedDayId.isNotBlank()) {
                                onSave(
                                    initialActivity.copy(
                                        title = title.trim(),
                                        locationName = locationName.trim(),
                                        description = description.trim(),
                                        notes = notes.trim(),
                                        tripDayId = selectedDayId,
                                        category = category,
                                        status = status,
                                        estimatedCost = costText.toDoubleOrNull() ?: 0.0
                                    )
                                )
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Save Activity")
                    }
                }
            }
        }
    }
}

@Composable
fun ActivityDetailsScreen(
    activity: Activity,
    onEditClick: () -> Unit,
    onBackClick: () -> Unit
) {
    GradientBackground {
        Scaffold(
            topBar = {
                TravelSyncTopBar(
                    title = "Activity Details",
                    onBackClick = onBackClick,
                    actions = {
                        TextButton(onClick = onEditClick) {
                            Text("Edit")
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
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                CategoryChip(
                    label = activity.category.name,
                    backgroundColor = MaterialTheme.colorScheme.primaryContainer,
                    textColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(text = activity.title, style = MaterialTheme.typography.headlineMedium)
                Text(
                    text = activity.locationName,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
                Text(text = activity.dayLabel, style = MaterialTheme.typography.titleMedium)
                if (activity.description.isNotEmpty()) {
                    Text(text = activity.description, style = MaterialTheme.typography.bodyMedium)
                }
                if (activity.notes.isNotEmpty()) {
                    Text(text = "Notes", style = MaterialTheme.typography.titleSmall)
                    Text(text = activity.notes, style = MaterialTheme.typography.bodyMedium)
                }
                if (activity.estimatedCost > 0) {
                    Text(
                        text = "Est. cost: ${activity.estimatedCost}",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                }
            }
        }
    }
}
