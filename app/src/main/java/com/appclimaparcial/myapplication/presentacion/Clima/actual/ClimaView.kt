package com.appclimaparcial.myapplication.presentacion.Clima.actual

import android.content.Intent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.appclimaparcial.myapplication.repository.modelos.Ciudad

@Composable
fun ClimaView(
    modifier: Modifier = Modifier,
    state : ClimaEstado,
    onAction: (ClimaIntencion) -> Unit,
    onChangeCity: () -> Unit
) {
    // Ejecutar actualización cuando vuelve a primer plano
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        onAction(ClimaIntencion.actualizarClima)
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when(state){
            is ClimaEstado.Error -> ErrorView(mensaje = state.mensaje)

            is ClimaEstado.Exitoso -> ClimaViewContenido(
                ciudad = state.ciudad,
                temperatura = state.temperatura,
                descripcion = state.descripcion,
                st = state.st,
                onChangeCity = onChangeCity
            )

            ClimaEstado.Vacio -> LoadingView()
            ClimaEstado.Cargando -> LoadingView()
        }

        Spacer(modifier = Modifier.height(100.dp))
    }
}

@Composable
fun ErrorView(mensaje: String){
    Text(text = mensaje)
}

@Composable
fun LoadingView(){
    Text(text = "Cargando...")
}

@Composable
fun ClimaViewContenido(
    ciudad: String,
    temperatura: Double,
    descripcion: String,
    st: Double,
    onChangeCity: () -> Unit
) {
    val context = LocalContext.current

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = ciudad, style = MaterialTheme.typography.titleMedium)
        Text(text = "${temperatura}°", style = MaterialTheme.typography.titleLarge)
        Text(text = descripcion, style = MaterialTheme.typography.bodyMedium)
        Text(text = "Sensación: ${st}°", style = MaterialTheme.typography.bodyMedium)

        Spacer(modifier = Modifier.height(12.dp))

        // Botón cambiar ciudad
        Button(onClick = { onChangeCity() }) {
            Text(text = "Cambiar ciudad")
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Botón compartir
        Button(onClick = {
            val shareIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(
                    Intent.EXTRA_TEXT,
                    "$ciudad: ${temperatura}°, $descripcion"
                )
                type = "text/plain"
            }
            context.startActivity(Intent.createChooser(shareIntent, "Compartir pronóstico"))
        }) {
            Text(text = "Compartir")
        }
    }
}