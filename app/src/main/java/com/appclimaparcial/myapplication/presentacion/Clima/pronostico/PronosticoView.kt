package com.appclimaparcial.myapplication.presentacion.Clima.pronostico

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.appclimaparcial.myapplication.repository.modelos.ListForecast

@Composable
fun PronosticoView(
    modifier: Modifier = Modifier,
    state : PronosticoEstado,
    onAction: (PronosticoIntencion)->Unit
) {
    // Ejecutar actualización cuando vuelve a primer plano
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        onAction(PronosticoIntencion.actualizarClima)
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    )
    {
        when(state) {
            is PronosticoEstado.Error -> ErrorView(mensaje = state.mensaje)
            is PronosticoEstado.Exitoso ->
                PronosticoListaView(climas = state.climas)
            PronosticoEstado.Vacio -> EmptyView()
            PronosticoEstado.Cargando -> LoadingView()
        }
        Spacer(modifier = Modifier.height(100.dp))
    }
}

@Composable
fun EmptyView() {
    Text(text = "No hay pronóstico")
}

@Composable
fun LoadingView() {
    Text(text = "Cargando pronóstico")
}

@Composable
fun ErrorView(mensaje: String) {
    Text(text = mensaje)
}

@Composable
fun PronosticoListaView(climas: List<ListForecast>) {
    LazyColumn {
        items(items = climas) { item ->
            Card {
                Text(text = "Temp: ${item.main.temp}")
            }
        }
    }
}