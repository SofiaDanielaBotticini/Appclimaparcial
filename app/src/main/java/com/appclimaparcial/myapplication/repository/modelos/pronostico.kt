package com.appclimaparcial.myapplication.repository.modelos

import kotlinx.serialization.Serializable

@Serializable
data class ForecastDTO (
    val cod: String,
    val message: Long,
    val cnt: Long,
    val list: List<ListForecast>,
)

@Serializable
data class ListForecast(
    val dt: Long,
    val main: MainForecast,
    val weather: List<WeatherForecast>,
)

@Serializable
data class MainForecast(
    val temp: Double,
    val feels_like: Double,
    val temp_min: Double,
    val temp_max: Double,
    val pressure: Long,
    val sea_level: Long,
    val grnd_level: Long,
    val humidity: Long,
    val temp_kf: Double,
)

@Serializable
data class WeatherForecast(
    val main: String,
    val description: String,
    val icon: String
)

data class DailyTemperature(
    val temp_max: Double,
    val temp_min: Double,
    val day: String,
)

