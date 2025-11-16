package com.appclimaparcial.myapplication.presentacion.Ciudades

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.appclimaparcial.myapplication.repository.Repositorio
import com.appclimaparcial.myapplication.repository.UserPreferences
import com.appclimaparcial.myapplication.repository.modelos.Ciudad
import com.appclimaparcial.myapplication.router.Router
import com.appclimaparcial.myapplication.router.Ruta
import kotlinx.coroutines.launch

class CiudadesViewModel(
    val repositorio: Repositorio,
    val router: Router, val userPrefs: UserPreferences
) : ViewModel() {
    var uiState by mutableStateOf<CiudadesEstado>(CiudadesEstado.Vacio)
    var ciudades : List<Ciudad> = emptyList()

    fun ejecutar(intencion: CiudadesIntencion){
        when(intencion){
            is CiudadesIntencion.Buscar -> buscar(intencion.nombre)
            is CiudadesIntencion.Seleccionar -> seleccionar(intencion.ciudad)
            is CiudadesIntencion.BuscarGeo -> buscarPorGeolocalizacion(intencion.lat, intencion.lon) // la nueva función que agregamos
        }
    }

    private fun buscar( nombre: String){

        uiState = CiudadesEstado.Cargando
        viewModelScope.launch {
            try {
                ciudades = repositorio.buscarCiudad(nombre)
                if (ciudades.isEmpty()) {
                    uiState = CiudadesEstado.Vacio
                } else {
                    uiState = CiudadesEstado.Resultado(ciudades)
                }
            } catch (exeption: Exception){
                uiState = CiudadesEstado.Error(exeption.message ?: "error desconocido")
            }
        }
    }

    private fun buscarPorGeolocalizacion(lat: Double, lon: Double) {
        uiState = CiudadesEstado.Cargando
        viewModelScope.launch {
            try {
                val resultados = repositorio.buscarCiudadPorCoords(lat, lon)
                ciudades = resultados
                uiState = if (ciudades.isEmpty()) CiudadesEstado.Vacio
                else CiudadesEstado.Resultado(ciudades)
            } catch (ex: Exception) {
                uiState = CiudadesEstado.Error(ex.message ?: "error desconocido")
            }
        }
    }

    private fun seleccionar(ciudad: Ciudad){
        userPrefs.saveCiudadSeleccionada(ciudad) // Para que siga funcionando lo de geolocalizacion
        val ruta = Ruta.Clima(lat = ciudad.lat, lon = ciudad.lon, nombre = ciudad.name
        )
        router.navegar(ruta)
    }
}

class CiudadesViewModelFactory(
    private val repositorio: Repositorio,
    private val router: Router,
    private val userPrefs: UserPreferences
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CiudadesViewModel::class.java)) {
            return CiudadesViewModel(repositorio, router, userPrefs) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
