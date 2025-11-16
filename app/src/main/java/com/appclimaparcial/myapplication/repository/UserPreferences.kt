package com.appclimaparcial.myapplication.repository

import android.content.Context
import android.content.SharedPreferences
import com.appclimaparcial.myapplication.repository.modelos.Ciudad

// Toda esta capa se agrega para cumplir con "guardar la configuración del usuario"

class UserPreferences(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    fun saveCiudadSeleccionada(ciudad: Ciudad) {
        prefs.edit()
            .putString("name", ciudad.name)
            .putFloat("lat", ciudad.lat)
            .putFloat("lon", ciudad.lon)
            .apply()
    }

    fun getCiudadSeleccionada(): Ciudad? {
        val name = prefs.getString("name", null) ?: return null
        val lat = prefs.getFloat("lat", 0f)
        val lon = prefs.getFloat("lon", 0f)
        return Ciudad(name = name, lat = lat, lon = lon, country = "")
    }

    fun clear() {
        prefs.edit().clear().apply()
    }
}
