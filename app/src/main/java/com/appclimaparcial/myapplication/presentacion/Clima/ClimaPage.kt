package com.appclimaparcial.myapplication.presentacion.Clima

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.appclimaparcial.myapplication.presentacion.Clima.actual.ClimaView
import com.appclimaparcial.myapplication.presentacion.Clima.actual.ClimaViewModel
import com.appclimaparcial.myapplication.presentacion.Clima.actual.ClimaViewModelFactory
import com.appclimaparcial.myapplication.presentacion.Clima.pronostico.PronosticoView
import com.appclimaparcial.myapplication.presentacion.Clima.pronostico.PronosticoViewModel
import com.appclimaparcial.myapplication.presentacion.Clima.pronostico.PronosticoViewModelFactory
import com.appclimaparcial.myapplication.repository.RepositorioApi
import com.appclimaparcial.myapplication.repository.UserPreferences
import com.appclimaparcial.myapplication.router.Enrutador

@Composable
fun ClimaPage(
    navHostController: NavHostController,
    lat : Float,
    lon : Float,
    nombre: String,
    userPrefs: UserPreferences
) {
    val viewModel : ClimaViewModel = viewModel(
        factory = ClimaViewModelFactory(
            repositorio = RepositorioApi(),
            router = Enrutador(navHostController),
            lat = lat,
            lon = lon,
            nombre = nombre
        )
    )
    val pronosticoViewModel : PronosticoViewModel = viewModel(
        factory = PronosticoViewModelFactory(
            repositorio = RepositorioApi(),
            router = Enrutador(navHostController),
            nombre = nombre
        )
    )

    Column {
        ClimaView(
            state = viewModel.uiState,
            onAction = { intencion -> viewModel.ejecutar(intencion) },
            onChangeCity = {
            // al pulsar "Cambiar ciudad" navegamos a Ciudades
            Enrutador(navHostController).navegar(com.appclimaparcial.myapplication.router.Ruta.Ciudades)
        })
        PronosticoView(state = pronosticoViewModel.uiState,
            onAction = { intencion ->
                pronosticoViewModel.ejecutar(intencion)
            }
        )
    }
}
