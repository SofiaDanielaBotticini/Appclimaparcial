/*package com.appclimaparcial.myapplication.repository

import com.appclimaparcial.myapplication.repository.modelos.Ciudad
import com.appclimaparcial.myapplication.repository.modelos.Clima
import com.appclimaparcial.myapplication.repository.modelos.ListForecast

class RepositorioMock : Repositorio {

    val cordoba = Ciudad(
        name = "Cordoba",
        lat = -31.42f,
        lon = -64.18f,
        country = "Argentina"
    )

    val bsAs = Ciudad(
        name = "Buenos Aires",
        lat = -34.61f,
        lon = -58.38f,
        country = "Argentina"
    )

    val laPlata = Ciudad(
        name = "La Plata",
        lat = -34.92f,
        lon = -57.95f,
        country = "Argentina"
    )

    val ciudades = listOf(cordoba, bsAs, laPlata)

    override suspend fun buscarCiudad(ciudad: String): List<Ciudad> {
        return when {
            ciudad.lowercase().contains("cor") -> listOf(cordoba)
            ciudad.lowercase().contains("plata") -> listOf(laPlata)
            else -> emptyList()
        }
    }

    // Uso float para hacer lo mismo que  con la interfaz Repositorio
    override suspend fun buscarCiudadPorCoords(lat: Float, lon: Float): List<Ciudad> {
        return when {
            lat == cordoba.lat && lon == cordoba.lon -> listOf(cordoba)
            lat == laPlata.lat && lon == laPlata.lon -> listOf(laPlata)
            else -> emptyList()
        }
    }

    override suspend fun traerClima(lat: Float, lon: Float): Clima {
        // verificar implementacion
        throw NotImplementedError("Implementar traerClima en RepositorioMock si se necesita")
    }

    override suspend fun traerPronostico(nombre: String): List<ListForecast> {
        // Se implementa cuando quieras testear pronostico
        throw NotImplementedError("Implementar traerPronostico en RepositorioMock si se necesita")
    }
}

class RepositorioMockError : Repositorio {

    override suspend fun buscarCiudad(ciudad: String): List<Ciudad> {
        throw Exception()
    }

    override suspend fun buscarCiudadPorCoords(lat: Float, lon: Float): List<Ciudad> {
        throw Exception()
    }

    override suspend fun traerClima(lat: Float, lon: Float): Clima {
        throw Exception()
    }

    override suspend fun traerPronostico(nombre: String): List<ListForecast> {
        throw Exception()
    }
}*/
