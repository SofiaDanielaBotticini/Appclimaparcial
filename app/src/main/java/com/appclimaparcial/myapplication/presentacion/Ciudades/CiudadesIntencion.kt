package com.appclimaparcial.myapplication.presentacion.Ciudades

import com.appclimaparcial.myapplication.repository.modelos.Ciudad

sealed class CiudadesIntencion {

    data class Buscar(val nombre:String) : CiudadesIntencion()

    object CargarRecomendadas : CiudadesIntencion()
    data class Seleccionar(val ciudad: Ciudad) : CiudadesIntencion()
    data class BuscarGeo(val lat: Float, val lon: Float) : CiudadesIntencion() // Esto es lo nuevo que se agrega para la geolocalizacion
}
