package com.travelsync.ui.navigation

import android.widget.Toast
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.travelsync.core.domain.model.Activity
import com.travelsync.core.domain.model.Expense
import com.travelsync.core.domain.model.User
import com.travelsync.core.domain.repository.AuthRepository
import com.travelsync.core.domain.repository.DatabaseRepository
import com.travelsync.ui.screens.*
import com.travelsync.ui.viewmodel.*
import kotlinx.coroutines.launch
import java.util.Date

@Composable
fun TravelSyncNavGraph(
    navController: NavHostController,
    authRepository: AuthRepository,
    databaseRepository: DatabaseRepository,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val tripListViewModel: TripListViewModel = hiltViewModel()

    val currentUser by tripListViewModel.currentUser.collectAsState(initial = null)
    val trips by tripListViewModel.trips.collectAsState(initial = emptyList())

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route,
        modifier = modifier
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(
                authRepository = authRepository,
                onSplashFinished = { isLoggedIn ->
                    val destination = if (isLoggedIn) Screen.Home.route else Screen.Auth.route
                    navController.navigate(destination) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Auth.route) {
            AuthScreen(
                authRepository = authRepository,
                onAuthSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Auth.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Home.route) {
            HomeScreen(
                userName = currentUser?.displayName ?: "Traveler",
                upcomingTrips = trips,
                onTripClick = { navController.navigate(Screen.TripDetails.createRoute(it)) },
                onCreateTripClick = { navController.navigate(Screen.CreateTrip.route) },
                onQuickAction = { action ->
                    val tripId = trips.firstOrNull()?.id ?: return@HomeScreen
                    navigateTripTool(navController, action, tripId)
                }
            )
        }

        composable(Screen.TripList.route) {
            TripListScreen(
                trips = trips,
                onTripClick = { navController.navigate(Screen.TripDetails.createRoute(it)) },
                onCreateTripClick = { navController.navigate(Screen.CreateTrip.route) },
                onDeleteTrip = { tripId ->
                    tripListViewModel.deleteTrip(tripId) {
                        Toast.makeText(context, "Trip deleted", Toast.LENGTH_SHORT).show()
                    }
                },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Screen.CreateTrip.route) {
            TripFormScreen(
                title = "New Trip",
                initialTrip = null,
                onSubmit = { trip ->
                    val userId = currentUser?.id ?: return@TripFormScreen
                    tripListViewModel.createTrip(trip, userId) {
                        Toast.makeText(context, "Trip created", Toast.LENGTH_SHORT).show()
                        navController.popBackStack()
                    }
                },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.EditTrip.route,
            arguments = listOf(navArgument("tripId") { type = NavType.StringType })
        ) {
            val detailViewModel: TripDetailViewModel = hiltViewModel()
            val trip by detailViewModel.trip.collectAsState(initial = null)
            TripFormScreen(
                title = "Edit Trip",
                initialTrip = trip,
                onSubmit = { updated ->
                    tripListViewModel.updateTrip(updated) {
                        Toast.makeText(context, "Trip updated", Toast.LENGTH_SHORT).show()
                        navController.popBackStack()
                    }
                },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.TripDetails.route,
            arguments = listOf(navArgument("tripId") { type = NavType.StringType })
        ) {
            val detailViewModel: TripDetailViewModel = hiltViewModel()
            val trip by detailViewModel.trip.collectAsState(initial = null)
            trip?.let { currentTrip ->
                TripDetailsScreen(
                    trip = currentTrip,
                    onTabSelected = { tab -> navigateTripTool(navController, tab, currentTrip.id) },
                    onEditTrip = { navController.navigate(Screen.EditTrip.createRoute(currentTrip.id)) },
                    onDeleteTrip = {
                        tripListViewModel.deleteTrip(currentTrip.id) {
                            Toast.makeText(context, "Trip deleted", Toast.LENGTH_SHORT).show()
                            navController.popBackStack()
                        }
                    },
                    onBackClick = { navController.popBackStack() }
                )
            }
        }

        composable(
            route = Screen.Itinerary.route,
            arguments = listOf(navArgument("tripId") { type = NavType.StringType })
        ) {
            val viewModel: ItineraryViewModel = hiltViewModel()
            val trip by viewModel.trip.collectAsState(initial = null)
            val tripDays by viewModel.tripDays.collectAsState(initial = emptyList())
            val activities by viewModel.activities.collectAsState(initial = emptyList())
            trip?.let { currentTrip ->
                DailyItineraryScreen(
                    trip = currentTrip,
                    tripDays = tripDays,
                    activities = activities,
                    onActivityClick = { activityId ->
                        navController.navigate(
                            Screen.ActivityEditor.createRoute(currentTrip.id, activityId = activityId)
                        )
                    },
                    onAddActivityClick = { dayId ->
                        navController.navigate(
                            Screen.ActivityEditor.createRoute(currentTrip.id, tripDayId = dayId)
                        )
                    },
                    onBackClick = { navController.popBackStack() }
                )
            }
        }

        composable(
            route = Screen.ActivityEditor.route,
            arguments = listOf(
                navArgument("tripId") { type = NavType.StringType },
                navArgument("activityId") { type = NavType.StringType; nullable = true; defaultValue = null },
                navArgument("tripDayId") { type = NavType.StringType; nullable = true; defaultValue = null },
                navArgument("placeId") { type = NavType.StringType; nullable = true; defaultValue = null }
            )
        ) {
            val itineraryViewModel: ItineraryViewModel = hiltViewModel()
            val editorViewModel: ActivityEditorViewModel = hiltViewModel()
            val tripDays by itineraryViewModel.tripDays.collectAsState(initial = emptyList())
            val activityId = it.arguments?.getString("activityId")
            val tripDayId = it.arguments?.getString("tripDayId")
            val placeId = it.arguments?.getString("placeId")
            val tripId = checkNotNull(it.arguments?.getString("tripId"))
            val userId = currentUser?.id.orEmpty()

            val existingActivity by (
                if (activityId != null) {
                    editorViewModel.observeActivity(activityId)
                } else {
                    kotlinx.coroutines.flow.flowOf<Activity?>(null)
                }
                ).collectAsState(initial = null)

            var draft by remember(activityId, placeId, tripDayId) {
                mutableStateOf(
                    Activity(tripId = tripId, tripDayId = tripDayId.orEmpty())
                )
            }

            LaunchedEffect(existingActivity) {
                existingActivity?.let { draft = it }
            }

            LaunchedEffect(placeId, tripDayId, activityId) {
                if (activityId == null && placeId != null) {
                    val fromPlace = editorViewModel.activityFromPlace(
                        placeId,
                        tripId,
                        tripDayId.orEmpty(),
                        userId
                    )
                    if (fromPlace != null) draft = fromPlace
                }
            }

            ActivityEditorScreen(
                initialActivity = draft,
                tripDays = tripDays,
                onSave = { activity ->
                    editorViewModel.saveActivity(activity, userId) { result ->
                        result.onSuccess {
                            Toast.makeText(context, "Activity saved", Toast.LENGTH_SHORT).show()
                            navController.popBackStack()
                        }
                    }
                },
                onDelete = if (activityId != null) {
                    {
                        editorViewModel.deleteActivity(activityId) { result ->
                            result.onSuccess { navController.popBackStack() }
                        }
                    }
                } else {
                    null
                },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.Places.route,
            arguments = listOf(navArgument("tripId") { type = NavType.StringType })
        ) {
            val viewModel: PlaceViewModel = hiltViewModel()
            val trip by viewModel.trip.collectAsState(initial = null)
            val places by viewModel.places.collectAsState(initial = emptyList())
            val tripDays by viewModel.tripDays.collectAsState(initial = emptyList())
            trip?.let { currentTrip ->
                PlacesScreen(
                    places = places,
                    tripDays = tripDays,
                    destination = currentTrip.destination,
                    onAddPlaceClick = { navController.navigate(Screen.AddPlace.createRoute(currentTrip.id)) },
                    onDeletePlace = viewModel::deletePlace,
                    onAddToItinerary = { place, dayId ->
                        viewModel.addPlaceToItinerary(place, dayId, currentUser?.id.orEmpty()) {
                            Toast.makeText(context, "Added to itinerary", Toast.LENGTH_SHORT).show()
                        }
                    },
                    onBackClick = { navController.popBackStack() }
                )
            }
        }

        composable(
            route = Screen.AddPlace.route,
            arguments = listOf(navArgument("tripId") { type = NavType.StringType })
        ) {
            val viewModel: PlaceViewModel = hiltViewModel()
            val trip by viewModel.trip.collectAsState(initial = null)
            trip?.let { currentTrip ->
                AddPlaceScreen(
                    tripId = currentTrip.id,
                    destination = currentTrip.destination,
                    onPlaceSaved = { place ->
                        viewModel.addPlace(place) { navController.popBackStack() }
                    },
                    onBackClick = { navController.popBackStack() }
                )
            }
        }

        composable(
            route = Screen.Budget.route,
            arguments = listOf(navArgument("tripId") { type = NavType.StringType })
        ) {
            val viewModel: BudgetViewModel = hiltViewModel()
            val trip by viewModel.trip.collectAsState(initial = null)
            val expenses by viewModel.expenses.collectAsState(initial = emptyList())
            val summary by viewModel.summary.collectAsState(initial = null)
            trip?.let { currentTrip ->
                summary?.let { budgetSummary ->
                    BudgetDashboardScreen(
                        trip = currentTrip,
                        summary = budgetSummary,
                        expenses = expenses,
                        onAddExpenseClick = {
                            navController.navigate(Screen.AddExpense.createRoute(currentTrip.id))
                        },
                        onDeleteExpense = viewModel::deleteExpense,
                        onBackClick = { navController.popBackStack() }
                    )
                }
            }
        }

        composable(
            route = Screen.AddExpense.route,
            arguments = listOf(navArgument("tripId") { type = NavType.StringType })
        ) {
            val viewModel: BudgetViewModel = hiltViewModel()
            val trip by viewModel.trip.collectAsState(initial = null)
            trip?.let { currentTrip ->
                AddExpenseScreen(
                    currency = currentTrip.currency,
                    onExpenseSaved = { title, amount, category ->
                        viewModel.addExpense(
                            Expense(
                                title = title,
                                amount = amount,
                                currency = currentTrip.currency,
                                category = category,
                                expenseDate = Date()
                            ),
                            currentUser?.id.orEmpty()
                        ) {
                            Toast.makeText(context, "Expense saved", Toast.LENGTH_SHORT).show()
                            navController.popBackStack()
                        }
                    },
                    onBackClick = { navController.popBackStack() }
                )
            }
        }

        composable(
            route = Screen.Packing.route,
            arguments = listOf(navArgument("tripId") { type = NavType.StringType })
        ) {
            val viewModel: PackingViewModel = hiltViewModel()
            val trip by viewModel.trip.collectAsState(initial = null)
            val items by viewModel.items.collectAsState(initial = emptyList())
            trip?.let { currentTrip ->
                PackingChecklistScreen(
                    trip = currentTrip,
                    packingItems = items,
                    onItemToggle = viewModel::toggleItem,
                    onAddItem = viewModel::addItem,
                    onDeleteItem = viewModel::deleteItem,
                    onBackClick = { navController.popBackStack() }
                )
            }
        }

        composable(Screen.Profile.route) {
            ProfileScreen(
                user = currentUser ?: User(),
                joinedTrips = trips,
                onSettingsClick = { navController.navigate(Screen.Settings.route) },
                onLogoutClick = {
                    scope.launch {
                        authRepository.logout()
                        navController.navigate(Screen.Auth.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                }
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                onBackClick = { navController.popBackStack() },
                onWipeLocalData = {
                    scope.launch {
                        databaseRepository.clearAllData()
                        Toast.makeText(context, "All local data cleared", Toast.LENGTH_SHORT).show()
                        navController.navigate(Screen.Auth.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                }
            )
        }
    }
}

private fun navigateTripTool(navController: NavHostController, tab: String, tripId: String) {
    when (tab) {
        "itinerary" -> navController.navigate(Screen.Itinerary.createRoute(tripId))
        "places" -> navController.navigate(Screen.Places.createRoute(tripId))
        "budget" -> navController.navigate(Screen.Budget.createRoute(tripId))
        "packing" -> navController.navigate(Screen.Packing.createRoute(tripId))
    }
}
