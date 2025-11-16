package com.appclimaparcial.myapplication.presentacion.Clima.actual

sealed class ClimaEstado {
    data class Exitoso (
        val ciudad: String = "",
        val temperatura: Double = 0.0,
        val descripcion: String= "",
        val st :Double = 0.0 ) : ClimaEstado()
    data class Error(
        val mensaje :String = "" ) : ClimaEstado()
    object Vacio: ClimaEstado()
    object Cargando: ClimaEstado()
}