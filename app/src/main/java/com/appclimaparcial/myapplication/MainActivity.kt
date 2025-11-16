package com.appclimaparcial.myapplication

import androidx.navigation.navArgument

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.navigation.NavType
import androidx.navigation.compose.*
import com.appclimaparcial.myapplication.presentacion.Ciudades.CiudadesPage
import com.appclimaparcial.myapplication.presentacion.Clima.ClimaPage
import com.appclimaparcial.myapplication.repository.UserPreferences

class MainActivity : ComponentActivity() {

    // Launcher para pedir permiso de ubicación
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted -> }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val userPrefs = UserPreferences(this)

        // Pedimos el permiso al iniciar la app
        requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)

        setContent {
            val navController = rememberNavController()
            val startDestination =
                if (userPrefs.getCiudadSeleccionada() != null) "clima" else "ciudades"

            NavHost(
                navController = navController,
                startDestination = startDestination
            ) {
                composable("ciudades") {
                    CiudadesPage(
                        navHostController = navController,
                        userPrefs = userPrefs,
                        onRequestLocation = {
                            // Si NO tiene permiso → lo pedimos
                            if (ContextCompat.checkSelfPermission(
                                    this@MainActivity,
                                    Manifest.permission.ACCESS_FINE_LOCATION
                                ) != PackageManager.PERMISSION_GRANTED
                            ) {
                                requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                            }
                            // Si tiene permiso → la vista obtiene la ubicación con FusedLocationProvider
                        }
                    )
                }

                composable(
                    route = "clima?lat={lat}&lon={lon}&nombre={nombre}",
                    arguments = listOf(
                        navArgument("lat") {
                            type = NavType.FloatType
                            defaultValue = userPrefs.getCiudadSeleccionada()?.lat ?: 0f
                        },
                        navArgument("lon") {
                            type = NavType.FloatType
                            defaultValue = userPrefs.getCiudadSeleccionada()?.lon ?: 0f
                        },
                        navArgument("nombre") {
                            type = NavType.StringType
                            defaultValue = userPrefs.getCiudadSeleccionada()?.name ?: ""
                        }
                    )
                ) { backStackEntry ->
                    val lat = backStackEntry.arguments?.getFloat("lat") ?: 0f
                    val lon = backStackEntry.arguments?.getFloat("lon") ?: 0f
                    val nombre = backStackEntry.arguments?.getString("nombre") ?: ""

                    ClimaPage(
                        navHostController = navController,
                        lat = lat,
                        lon = lon,
                        nombre = nombre,
                        userPrefs = userPrefs
                    )
                }
            }
        }
    }
}