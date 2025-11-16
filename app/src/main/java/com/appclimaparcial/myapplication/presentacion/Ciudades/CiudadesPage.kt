package com.appclimaparcial.myapplication.presentacion.Ciudades

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.appclimaparcial.myapplication.repository.RepositorioApi
import com.appclimaparcial.myapplication.repository.UserPreferences
import com.appclimaparcial.myapplication.router.Enrutador

@Composable
fun CiudadesPage(
    navHostController: NavHostController, userPrefs: UserPreferences, onRequestLocation: ()->Unit
) {
    val viewModel : CiudadesViewModel = viewModel(
        factory = CiudadesViewModelFactory(
            repositorio = RepositorioApi(),
            router = Enrutador(navHostController),
            userPrefs = userPrefs // para que se guarde la configuración (por eso también el OnRequestLocation)
        )
    )

    CiudadesView(
        state = viewModel.uiState,
        onAction = { intencion ->
            viewModel.ejecutar(intencion) },
        onRequestLocation = onRequestLocation
    )
}
