package com.appclimaparcial.myapplication.presentacion.Clima.pronostico

import com.appclimaparcial.myapplication.repository.modelos.ListForecast

sealed class PronosticoEstado {
    data class Exitoso (
        val climas: List<ListForecast>
    ) : PronosticoEstado()
    data class Error(
        val mensaje :String = ""
    ) : PronosticoEstado()
    object Vacio: PronosticoEstado()
    object Cargando: PronosticoEstado()
}