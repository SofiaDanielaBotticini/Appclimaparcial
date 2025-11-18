package com.appclimaparcial.myapplication.presentacion.Clima

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
import kotlinx.serialization.InternalSerializationApi
import androidx.compose.material3.Text
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(InternalSerializationApi::class)
@Composable
fun ClimaPage(
    navHostController: NavHostController,
    lat : Float,
    lon : Float,
    nombre: String,
    userPrefs: UserPreferences
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { TopBar() })
    { innerPadding ->

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

    Column (modifier = Modifier.padding(innerPadding)){
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
}
@InternalSerializationApi
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar() {
    CenterAlignedTopAppBar(
        title = {
            Text(
                "Aplicación del clima - Grupo 3",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 4.dp),
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    )
}