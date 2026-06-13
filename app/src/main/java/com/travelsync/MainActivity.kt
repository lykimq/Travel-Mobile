package com.travelsync

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.travelsync.core.domain.repository.AuthRepository
import com.travelsync.core.domain.repository.DatabaseRepository
import com.travelsync.ui.components.BottomNavBar
import com.travelsync.ui.navigation.Screen
import com.travelsync.ui.navigation.TravelSyncNavGraph
import com.travelsync.ui.theme.TravelSyncTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject lateinit var authRepository: AuthRepository
    @Inject lateinit var databaseRepository: DatabaseRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            TravelSyncTheme {
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route ?: Screen.Splash.route

                val showBottomBar = currentRoute in listOf(
                    Screen.Home.route,
                    Screen.TripList.route,
                    Screen.Profile.route
                )

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        if (showBottomBar) {
                            BottomNavBar(
                                selectedRoute = currentRoute,
                                onNavigate = { route ->
                                    navController.navigate(route) {
                                        popUpTo(navController.graph.startDestinationId) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                },
                                items = listOf(
                                    Screen.Home.route to Icons.Default.Explore,
                                    Screen.TripList.route to Icons.Default.CalendarMonth,
                                    Screen.Profile.route to Icons.Default.Person
                                )
                            )
                        }
                    }
                ) { innerPadding ->
                    TravelSyncNavGraph(
                        navController = navController,
                        authRepository = authRepository,
                        databaseRepository = databaseRepository,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    )
                }
            }
        }
    }
}
