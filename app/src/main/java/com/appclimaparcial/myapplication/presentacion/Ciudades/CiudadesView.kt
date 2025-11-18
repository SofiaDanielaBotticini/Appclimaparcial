package com.appclimaparcial.myapplication.presentacion.Ciudades

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.appclimaparcial.myapplication.repository.modelos.Ciudad
import kotlinx.serialization.InternalSerializationApi

@OptIn(InternalSerializationApi::class)
@Composable
fun CiudadesView(
    modifier: Modifier = Modifier,
    state: CiudadesEstado,
    onAction: (CiudadesIntencion) -> Unit,
    onRequestLocation: () -> Unit // Es para que la vista pida permiso/ubicación al usuario
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { TopBar() })
    { innerPadding ->
        var value by remember { mutableStateOf("") }

        Column(modifier = modifier.padding(innerPadding)) {
            OutlinedTextField(
                value = value,
                label = { Text(text = "Buscar por nombre") },
                placeholder = { Text(text = "Villa luro") },
                onValueChange = {
                    value = it
                    onAction(CiudadesIntencion.Buscar(value))
                },
                modifier = Modifier.fillMaxWidth()
            )

            Button(onClick = { onRequestLocation() }) {
                Text(text = "Buscar por geolocalización")
            }

            when (state) {
                is CiudadesEstado.Recomendadas -> {
                    Text(
                        "Ciudades recomendadas:",
                        style = MaterialTheme.typography.titleMedium
                    )
                    ListaDeCiudades(state.ciudades) { ciudad ->
                        onAction(CiudadesIntencion.Seleccionar(ciudad))
                    }
                }
                CiudadesEstado.Cargando -> Text("Cargando...")

                is CiudadesEstado.Error -> Text(state.mensaje)

                is CiudadesEstado.Resultado -> {
                    ListaDeCiudades(state.ciudades) {
                        onAction(CiudadesIntencion.Seleccionar(it))
                    }
                }
                CiudadesEstado.Vacio -> Text("No hay resultados")
            }
        }
    }
}

@Composable
fun ListaDeCiudades(ciudades: List<Ciudad>, onSelect: (Ciudad) -> Unit) {
    LazyColumn {
        items(items = ciudades) {
            Card(onClick = { onSelect(it) }) {
                Text(text = it.name)
            }
        }
    }
}

@InternalSerializationApi
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar() {
    CenterAlignedTopAppBar(
        title = { Text("Aplicacion del clima - Grupo 3") },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.primary
        )
    )
}