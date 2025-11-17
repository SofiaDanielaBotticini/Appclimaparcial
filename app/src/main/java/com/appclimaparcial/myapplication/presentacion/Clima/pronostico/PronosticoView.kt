package com.appclimaparcial.myapplication.presentacion.Clima.pronostico

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import com.appclimaparcial.myapplication.repository.modelos.DailyTemperature
import com.appclimaparcial.myapplication.repository.modelos.ListForecast
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottom
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStart
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberColumnCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.common.component.rememberLineComponent
import com.patrykandpatrick.vico.compose.common.fill
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.columnSeries
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import com.patrykandpatrick.vico.core.cartesian.layer.ColumnCartesianLayer
import com.patrykandpatrick.vico.core.cartesian.layer.LineCartesianLayer
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun PronosticoView(
    modifier: Modifier = Modifier, state: PronosticoEstado, onAction: (PronosticoIntencion) -> Unit
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
    ) {
        when (state) {
            is PronosticoEstado.Error -> ErrorView(mensaje = state.mensaje)
            is PronosticoEstado.Exitoso -> PronosticoListaView(climas = state.climas)

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
    val modifier: Modifier = Modifier

    val tempMaxSeries = daysForecast.map { it.temp_max }
    val tempMinSeries = daysForecast.map { it.temp_min }


    val modelProducer = remember { CartesianChartModelProducer() }
    LaunchedEffect(daysForecast) {
        modelProducer.runTransaction {
            columnSeries { series(tempMaxSeries) }
            lineSeries {
                series(tempMaxSeries)
                series(tempMinSeries)
            }
        }
    }
    JetpackComposeBasicComboChart(modelProducer, modifier)
}


// ---------------------------------------------------------------------------------
// --- FUNCIONES PARA VICO ---
// ---------------------------------------------------------------------------------

@Composable
private fun JetpackComposeBasicComboChart(
    modelProducer: CartesianChartModelProducer,
    modifier: Modifier = Modifier,
) {
    CartesianChartHost(
        rememberCartesianChart(
            rememberColumnCartesianLayer(
                ColumnCartesianLayer.ColumnProvider.series(
                    rememberLineComponent(fill = fill(Color(0xffffc002)), thickness = 16.dp)
                )
            ),
            rememberLineCartesianLayer(
                LineCartesianLayer.LineProvider.series(
                    LineCartesianLayer.Line(LineCartesianLayer.LineFill.single(fill(Color(0xffee2b2b))))
                )
            ),
            startAxis = VerticalAxis.rememberStart(),
            bottomAxis = HorizontalAxis.rememberBottom(),
        ),
        modelProducer,
        modifier,
    )
}


// ---------------------------------------------------------------------------------
// --- FUNCIONES PARA PROCESAR EL dt Y AGRUPAR POR DIA ---
// ---------------------------------------------------------------------------------
@RequiresApi(Build.VERSION_CODES.O)
fun Long.toFormattedDate(pattern: String = "EEE, MMM d"): String {
    // 1. Convert seconds to milliseconds
    val epochMilli = this * 1000L

    // 2. Create an Instant (a moment in time, UTC)
    val instant = Instant.ofEpochMilli(epochMilli)

    // 3. Define the desired output format and timezone
    val formatter = DateTimeFormatter.ofPattern(pattern).withZone(ZoneId.systemDefault())
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
            temp_min = dailyForecasts.minOf { it.main.temp })
    }.take(5) // 5 días
}