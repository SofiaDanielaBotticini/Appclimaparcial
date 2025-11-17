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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.*
import com.appclimaparcial.myapplication.presentacion.Ciudades.CiudadesIntencion
import com.appclimaparcial.myapplication.presentacion.Ciudades.CiudadesPage
import com.appclimaparcial.myapplication.presentacion.Ciudades.CiudadesViewModel
import com.appclimaparcial.myapplication.presentacion.Ciudades.CiudadesViewModelFactory
import com.appclimaparcial.myapplication.presentacion.Clima.ClimaPage
import com.appclimaparcial.myapplication.repository.Repositorio
import com.appclimaparcial.myapplication.repository.RepositorioApi
import com.appclimaparcial.myapplication.repository.UserPreferences
import com.appclimaparcial.myapplication.router.Enrutador
import com.appclimaparcial.myapplication.router.Router
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class MainActivity : ComponentActivity() {

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted -> }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val userPrefs = UserPreferences(this)


        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(this)


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

                    val ciudadesViewModel: CiudadesViewModel = viewModel(
                        factory = CiudadesViewModelFactory(
                            repositorio = RepositorioApi(),   // ← agrega esto
                            router = Enrutador(navController), // ← agrega esto
                            userPrefs = userPrefs
                        )
                    )

                    CiudadesPage(
                        navHostController = navController,
                        userPrefs = userPrefs,
                        onRequestLocation = {

                            if (ContextCompat.checkSelfPermission(
                                    this@MainActivity,
                                    Manifest.permission.ACCESS_FINE_LOCATION
                                ) != PackageManager.PERMISSION_GRANTED
                            ) {
                                requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                            } else {
                                fusedLocationProviderClient.lastLocation
                                    .addOnSuccessListener { location ->
                                        if (location != null) {
                                            ciudadesViewModel.ejecutar(
                                                CiudadesIntencion.BuscarGeo(
                                                    lat = location.latitude.toFloat(),
                                                    lon = location.longitude.toFloat()
                                                )
                                            )
                                        }
                                    }
                            }
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
