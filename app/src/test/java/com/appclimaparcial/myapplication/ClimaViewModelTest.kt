/*package com.appclimaparcial.myapplication

import com.appclimaparcial.myapplication.presentacion.Clima.actual.ClimaEstado
import com.appclimaparcial.myapplication.presentacion.Clima.actual.ClimaIntencion
import com.appclimaparcial.myapplication.presentacion.Clima.actual.ClimaViewModel
import com.appclimaparcial.myapplication.presentacion.Clima.actual.ClimaViewModelFactory
import com.appclimaparcial.myapplication.repository.Repositorio
import com.appclimaparcial.myapplication.repository.modelos.Clima
import com.appclimaparcial.myapplication.repository.modelos.Main
import com.appclimaparcial.myapplication.repository.modelos.Weather
import com.appclimaparcial.myapplication.repository.modelos.Ciudad
import com.appclimaparcial.myapplication.repository.modelos.ListForecast
import com.appclimaparcial.myapplication.router.Router
import com.appclimaparcial.myapplication.router.Ruta
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

/**
 * Router fake para clima. Por ahora el ViewModel no navega,
 * pero lo dejo armado por si en algún momento lo necesito.
 */
class RouterFakeClima : Router {
    var ultimaRuta: Ruta? = null
    override fun navegar(ruta: Ruta) {
        ultimaRuta = ruta
    }
}

/**
 * Repositorio de prueba que devuelve siempre el mismo clima.
 * Así el test es predecible y no dependemos de nada externo.
 */
class RepositorioClimaExitoso : Repositorio {
    override suspend fun buscarCiudad(ciudad: String): List<Ciudad> = emptyList()

    override suspend fun buscarCiudadPorCoords(
        lat: Float,
        lon: Float
    ): List<Ciudad> = emptyList()

    override suspend fun traerClima(lat: Float, lon: Float): Clima {
        return Clima(
            name = "Ciudad Test",
            main = Main(
                temp = 25.0,
                feels_like = 27.0
            ),
            weather = listOf(
                Weather(
                    description = "despejado"
                )
            )
        )
    }

    override suspend fun traerPronostico(nombre: String): List<ListForecast> = emptyList()
}

/**
 * Repositorio que tira excepción al traer el clima.
 * Lo uso para testear el estado de Error en el ViewModel.
 */
class RepositorioClimaError : Repositorio {
    override suspend fun buscarCiudad(ciudad: String): List<Ciudad> = emptyList()

    override suspend fun buscarCiudadPorCoords(
        lat: Float,
        lon: Float
    ): List<Ciudad> = emptyList()

    override suspend fun traerClima(lat: Float, lon: Float): Clima {
        throw Exception("fallo al traer clima")
    }

    override suspend fun traerPronostico(nombre: String): List<ListForecast> = emptyList()
}

class ClimaViewModelTest {

    // Thread para simular Dispatchers.Main, porque el ViewModel usa viewModelScope en Main.
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

    // ---------- Test actualizarClima (caso OK) ----------
    // Acá pruebo el camino feliz: el repo devuelve datos y el estado es Exitoso.

    @Test
    fun actualizarClima_conRepositorioExitoso_generaEstadoExitoso() = runTest(timeout = 3.seconds) {
        val repo = RepositorioClimaExitoso()
        val router = RouterFakeClima()

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
            // Le doy un rato para que termine el trabajo del ViewModel.
            delay(1.milliseconds)

            val estado = viewModel.uiState
            assertTrue(estado is ClimaEstado.Exitoso)

            estado as ClimaEstado.Exitoso
            assertEquals("Ciudad Test", estado.ciudad)
            assertEquals(25.0, estado.temperatura, 0.0)
            assertEquals("despejado", estado.descripcion)
            assertEquals(27.0, estado.st, 0.0)
        }
    }

    // ---------- Test actualizarClima (caso error) ----------
    // Acá fuerzo un error en el repo y verifico que el ViewModel quede en estado Error.

    @Test
    fun actualizarClima_conErrorEnRepositorio_generaEstadoError() = runTest(timeout = 3.seconds) {
        val repo = RepositorioClimaError()
        val router = RouterFakeClima()

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

            // El mensaje puede variar según la excepción, pero al menos no debería venir vacío.
            val mensaje = (estado as ClimaEstado.Error).mensaje
            assertTrue(mensaje.isNotBlank())
        }
    }
}
*/
