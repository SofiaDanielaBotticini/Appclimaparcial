package com.appclimaparcial.myapplication.router

interface Router {
    fun navegar(ruta: Ruta )
}

sealed class Ruta(val id: String) {
    object Ciudades: Ruta("ciudades")
    data class Clima(val lat: Float,val lon:Float, val nombre:String): Ruta("clima")
}

