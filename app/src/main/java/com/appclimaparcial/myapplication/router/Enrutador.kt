package com.appclimaparcial.myapplication.router

import androidx.navigation.NavHostController

class Enrutador(val navHostController: NavHostController): Router {
    override fun navegar(ruta: Ruta) {
        when(ruta){
            Ruta.Ciudades -> navHostController.navigate(ruta.id)
            is Ruta.Clima -> {
                val route = "${ruta.id}?lat=${ruta.lat}&lon=${ruta.lon}&nombre=${ruta.nombre}"
                navHostController.navigate(route)
            }
        }
    }
}
