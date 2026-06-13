package com.travelsync.ui.navigation

sealed class Screen(val route: String) {
    data object Splash : Screen("splash")
    data object Auth : Screen("auth")
    data object Home : Screen("home")
    data object TripList : Screen("trip_list")
    data object CreateTrip : Screen("create_trip")

    data object EditTrip : Screen("edit_trip/{tripId}") {
        fun createRoute(tripId: String) = "edit_trip/$tripId"
    }

    data object TripDetails : Screen("trip_details/{tripId}") {
        fun createRoute(tripId: String) = "trip_details/$tripId"
    }

    data object Itinerary : Screen("itinerary/{tripId}") {
        fun createRoute(tripId: String) = "itinerary/$tripId"
    }

    data object ActivityEditor : Screen("activity_editor/{tripId}?activityId={activityId}&tripDayId={tripDayId}&placeId={placeId}") {
        fun createRoute(
            tripId: String,
            activityId: String? = null,
            tripDayId: String? = null,
            placeId: String? = null
        ): String {
            val base = "activity_editor/$tripId"
            val params = buildList {
                activityId?.let { add("activityId=$it") }
                tripDayId?.let { add("tripDayId=$it") }
                placeId?.let { add("placeId=$it") }
            }
            return if (params.isEmpty()) base else "$base?${params.joinToString("&")}"
        }
    }

    data object Places : Screen("places/{tripId}") {
        fun createRoute(tripId: String) = "places/$tripId"
    }

    data object AddPlace : Screen("add_place/{tripId}") {
        fun createRoute(tripId: String) = "add_place/$tripId"
    }

    data object Budget : Screen("budget/{tripId}") {
        fun createRoute(tripId: String) = "budget/$tripId"
    }

    data object AddExpense : Screen("add_expense/{tripId}") {
        fun createRoute(tripId: String) = "add_expense/$tripId"
    }

    data object Packing : Screen("packing/{tripId}") {
        fun createRoute(tripId: String) = "packing/$tripId"
    }

    data object Profile : Screen("profile")
    data object Settings : Screen("settings")
}
