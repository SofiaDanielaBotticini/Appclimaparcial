/*package com.appclimaparcial.myapplication.repository

import com.appclimaparcial.myapplication.repository.modelos.Ciudad
import com.appclimaparcial.myapplication.repository.modelos.Clima
import com.appclimaparcial.myapplication.repository.modelos.ListForecast

class RepositorioMock  : Repositorio {

    val cordoba = Ciudad(name = "Cordoba", lat = -31.42f, lon = -64.18f, country = "Argentina")
    val bsAs = Ciudad(name = "Buenos Aires", lat = -34.61f, lon = -58.38f, country = "Argentina")
    val laPlata = Ciudad(name = "La Plata", lat = -34.92f, lon = -57.95f, country = "Argentina")

    val ciudades = listOf(cordoba,bsAs,laPlata)

    override suspend fun buscarCiudad(ciudad: String): List<Ciudad> {
        if (ciudad == "error"){
            throw Exception()
        }
        return ciudades.filter { it.name.contains(ciudad,ignoreCase = true) }
    }

    override suspend fun buscarCiudadPorCoords(lat: Double, lon: Double): List<Ciudad> {
        // mock: devuelve la lista completa
        return ciudades
    }

    override suspend fun traerClima(lat: Float, lon: Float): Clima {
    }

    override suspend fun traerPronostico(nombre: String): List<ListForecast> {
    }
}

class RepositorioMockError  : Repositorio {
    override suspend fun buscarCiudad(ciudad: String): List<Ciudad> { throw Exception() }
    override suspend fun buscarCiudadPorCoords(lat: Double, lon: Double): List<Ciudad> { throw Exception() }
    override suspend fun traerClima(lat: Float, lon: Float): Clima { throw Exception() }
    override suspend fun traerPronostico(nombre: String): List<ListForecast> { throw Exception() }
}
*/