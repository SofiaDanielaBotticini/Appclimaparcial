package com.appclimaparcial.myapplication.repository

import com.appclimaparcial.myapplication.repository.modelos.Ciudad
import com.appclimaparcial.myapplication.repository.modelos.Clima
import com.appclimaparcial.myapplication.repository.modelos.ForecastDTO
import com.appclimaparcial.myapplication.repository.modelos.ListForecast
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class RepositorioApi : Repositorio {

    private val apiKey = "bb11b496a3744223b7d756d5e58c8cc8"

    private val cliente = HttpClient() {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
            })
        }
    }

    override suspend fun buscarCiudad(ciudad: String): List<Ciudad> {
        val respuesta = cliente.get("https://api.openweathermap.org/geo/1.0/direct") {
            parameter("q", ciudad)
            parameter("limit", 100)
            parameter("appid", apiKey)
        }
        if (respuesta.status == HttpStatusCode.OK) {
            val ciudades = respuesta.body<List<Ciudad>>()
            return ciudades
        } else {
            throw Exception("Error buscando ciudad")
        }
    }

    override suspend fun buscarCiudadPorCoords(lat: Double, lon: Double): List<Ciudad> {
        val respuesta = cliente.get("https://api.openweathermap.org/geo/1.0/reverse") {
            parameter("lat", lat)
            parameter("lon", lon)
            parameter("limit", 5)
            parameter("appid", apiKey)
        }
        if (respuesta.status == HttpStatusCode.OK) {
            return respuesta.body<List<Ciudad>>()
        } else {
            throw Exception("Error reverse geocoding")
        }
    }

    override suspend fun traerClima(lat: Float, lon: Float): Clima {
        val respuesta = cliente.get("https://api.openweathermap.org/data/2.5/weather") {
            parameter("lat", lat)
            parameter("lon", lon)
            parameter("units", "metric")
            parameter("appid", apiKey)
        }
        if (respuesta.status == HttpStatusCode.OK) {
            val clima = respuesta.body<Clima>()
            return clima
        } else {
            throw Exception("Error traerClima")
        }
    }

    override suspend fun traerPronostico(nombre: String): List<ListForecast> {
        val respuesta = cliente.get("https://api.openweathermap.org/data/2.5/forecast") {
            parameter("q", nombre)
            parameter("units", "metric")
            parameter("appid", apiKey)
        }
        if (respuesta.status == HttpStatusCode.OK) {
            val forecast = respuesta.body<ForecastDTO>()
            return forecast.list
        } else {
            throw Exception("Error traerPronostico")
        }
    }
}
