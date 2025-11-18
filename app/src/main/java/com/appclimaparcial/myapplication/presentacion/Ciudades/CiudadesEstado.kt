package com.appclimaparcial.myapplication.presentacion.Ciudades

import com.appclimaparcial.myapplication.repository.modelos.Ciudad

sealed class CiudadesEstado {
    object Vacio: CiudadesEstado()
    object Cargando: CiudadesEstado()
    data class Resultado(val ciudades: List<Ciudad>) : CiudadesEstado()
    data class Error(val mensaje: String): CiudadesEstado()
    data class Recomendadas(val ciudades: List<Ciudad>) : CiudadesEstado()
}