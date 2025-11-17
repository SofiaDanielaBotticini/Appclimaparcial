package com.appclimaparcial.myapplication.presentacion.Clima.pronostico

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.appclimaparcial.myapplication.repository.modelos.DailyTemperature
import com.appclimaparcial.myapplication.repository.modelos.ListForecast
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun PronosticoView(
    modifier: Modifier = Modifier,
    state: PronosticoEstado,
    onAction: (PronosticoIntencion) -> Unit
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
        when (state) {
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

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun PronosticoListaView(climas: List<ListForecast>) {
    val daysForecast = forecastGraphInfo(climas)
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        items(items = daysForecast) { item ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp)
            ) {
                Text(
                    text = item.day.uppercase(),
                    modifier = Modifier.fillMaxWidth(),
                    style = MaterialTheme.typography.labelMedium,
                    textAlign = TextAlign.Center
                )
                Row(Modifier
                    .padding(24.dp)
                    .fillMaxWidth()
                ){
                    Text(text = "MAX: ${item.temp_max}",style = MaterialTheme.typography.labelMedium,)
                    Spacer(
                        modifier = Modifier.width(8.dp)
                    )
                    Text(text = "Min: ${item.temp_min}",style = MaterialTheme.typography.labelMedium,)
                }
            }
        }
    }
}

//funciones para procesar el value de dt a fecha y después agrupar la info
@RequiresApi(Build.VERSION_CODES.O)
fun Long.toFormattedDate(pattern: String = "EEE, MMM d"): String {
    // 1. Convert seconds to milliseconds
    val epochMilli = this * 1000L

    // 2. Create an Instant (a moment in time, UTC)
    val instant = Instant.ofEpochMilli(epochMilli)

    // 3. Define the desired output format and timezone
    val formatter = DateTimeFormatter.ofPattern(pattern)
        .withZone(ZoneId.systemDefault())
        .withLocale(Locale("es", "ES"))

    // 4. Format and return the date string
    return formatter.format(instant)
}

@RequiresApi(Build.VERSION_CODES.O)
fun Long.toLocalDate(): LocalDate {
    return Instant.ofEpochSecond(this).atZone(ZoneId.systemDefault()).toLocalDate()
}

@RequiresApi(Build.VERSION_CODES.O)
fun forecastGraphInfo(forecasts: List<ListForecast>): List<DailyTemperature> {
    val dailyGroupedForecasts = forecasts.groupBy { it.dt.toLocalDate() }

    return dailyGroupedForecasts.map { (_, dailyForecasts) ->

        val firstItemDt = dailyForecasts.first().dt
        DailyTemperature(
            day = firstItemDt.toFormattedDate("EEE"),
            temp_max = dailyForecasts.maxOf { it.main.temp },
            temp_min = dailyForecasts.minOf { it.main.temp }
        )
    }
        .take(5) // 5 días
}
