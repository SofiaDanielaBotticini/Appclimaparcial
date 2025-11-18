/*package com.appclimaparcial.myapplication

import com.appclimaparcial.myapplication.presentacion.Clima.actual.ClimaEstado
import com.appclimaparcial.myapplication.presentacion.Clima.actual.ClimaIntencion
import com.appclimaparcial.myapplication.presentacion.Clima.actual.ClimaViewModel
import com.appclimaparcial.myapplication.presentacion.Clima.actual.ClimaViewModelFactory
import com.appclimaparcial.myapplication.repository.RepositorioMock
import com.appclimaparcial.myapplication.repository.RepositorioMockError
import com.appclimaparcial.myapplication.router.MockRouter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

class ClimaViewModelTest {

    // Como en los tests no hay Main, invento uno para que el ViewModel no explote cuando usa viewModelScope.
    private val mainThreadSurrogate = newSingleThreadContext("UI thread")

    @Before
    fun setUp() {
        Dispatchers.setMain(mainThreadSurrogate)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        mainThreadSurrogate.close()
    }

    // Caso Ok
    // Uso RepositorioMock, que devuelve un clima mock fijo.

    @Test
    fun actualizarClima_conRepositorioMock_generaEstadoExitoso() = runTest(timeout = 3.seconds) {
        val repo = RepositorioMock()
        val router = MockRouter()

        val factory = ClimaViewModelFactory(
            repositorio = repo,
            router = router,
            lat = -31.42f,
            lon = -64.18f,
            nombre = "Ciudad Test"
        )

        val viewModel = factory.create(ClimaViewModel::class.java)

        launch(Dispatchers.Main) {
            viewModel.ejecutar(ClimaIntencion.actualizarClima)
            delay(1.milliseconds)

            val estado = viewModel.uiState
            assertTrue(estado is ClimaEstado.Exitoso)

            estado as ClimaEstado.Exitoso
            // Valores de la clase RepositorioMock.
            assertEquals("Ciudad Test", estado.ciudad)
            assertEquals(25.0, estado.temperatura, 0.0)
            assertEquals("despejado", estado.descripcion)
            assertEquals(27.0, estado.st, 0.0)
        }
    }


    // Uso RepositorioMockError para forzar un fallo y revisar que el estado sea Error.

    @Test
    fun actualizarClima_conRepositorioMockError_generaEstadoError() = runTest(timeout = 3.seconds) {
        val repo = RepositorioMockError()
        val router = MockRouter()

        val factory = ClimaViewModelFactory(
            repositorio = repo,
            router = router,
            lat = -31.42f,
            lon = -64.18f,
            nombre = "Ciudad Test"
        )

        val viewModel = factory.create(ClimaViewModel::class.java)

        launch(Dispatchers.Main) {
            viewModel.ejecutar(ClimaIntencion.actualizarClima)
            delay(1.milliseconds)

            val estado = viewModel.uiState
            assertTrue(estado is ClimaEstado.Error)

            val mensaje = (estado as ClimaEstado.Error).mensaje
            assertTrue(mensaje.isNotBlank())
        }
    }
}
*/
