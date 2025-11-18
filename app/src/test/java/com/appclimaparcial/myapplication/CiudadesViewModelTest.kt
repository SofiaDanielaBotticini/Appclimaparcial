/*package com.appclimaparcial.myapplication

import com.appclimaparcial.myapplication.presentacion.Ciudades.CiudadesEstado
import com.appclimaparcial.myapplication.presentacion.Ciudades.CiudadesIntencion
import com.appclimaparcial.myapplication.presentacion.Ciudades.CiudadesViewModel
import com.appclimaparcial.myapplication.presentacion.Ciudades.CiudadesViewModelFactory
import com.appclimaparcial.myapplication.repository.Repositorio
import com.appclimaparcial.myapplication.repository.RepositorioMock
import com.appclimaparcial.myapplication.repository.modelos.Ciudad
import com.appclimaparcial.myapplication.router.Router
import com.appclimaparcial.myapplication.router.Ruta
import com.appclimaparcial.myapplication.repository.UserPreferences
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
 * UserPreferences trucho para poder probar la intención Seleccionar sin usar Android real.
 */
class FakeUserPreferences : UserPreferences {
    var ultimaCiudadGuardada: Ciudad? = null

    override fun saveCiudadSeleccionada(ciudad: Ciudad) {
        ultimaCiudadGuardada = ciudad
    }
}

/**
 * Router inventado para poder ver a dónde intenta navegar el ViewModel.
 */
class RouterFake : Router {
    var ultimaRuta: Ruta? = null

    override fun navegar(ruta: Ruta) {
        ultimaRuta = ruta
    }
}

/**
 * Repositorio que falla siempre. Lo uso para forzar estados de error.
 */
class RepositorioError : Repositorio {
    override suspend fun buscarCiudad(ciudad: String): List<Ciudad> {
        throw Exception()
    }

    override suspend fun buscarCiudadPorCoords(lat: Float, lon: Float): List<Ciudad> {
        throw Exception()
    }

    override suspend fun traerClima(
        lat: Float,
        lon: Float
    ): com.appclimaparcial.myapplication.repository.modelos.Clima {
        throw Exception()
    }

    override suspend fun traerPronostico(nombre: String):
            List<com.appclimaparcial.myapplication.repository.modelos.ListForecast> {
        return emptyList()
    }
}

class CiudadesViewModelTest {

    // Hago un thread para simular el Main (UI), porque los ViewModel usan Dispatchers.Main
    private val mainThreadSurrogate = newSingleThreadContext("UI thread")

    private lateinit var repositorio: RepositorioMock
    private lateinit var router: RouterFake
    private lateinit var userPrefs: FakeUserPreferences
    private lateinit var viewModel: CiudadesViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(mainThreadSurrogate)

        repositorio = RepositorioMock()
        router = RouterFake()
        userPrefs = FakeUserPreferences()

        val factory = CiudadesViewModelFactory(repositorio, router, userPrefs)
        viewModel = factory.create(CiudadesViewModel::class.java)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        mainThreadSurrogate.close()
    }

    // ---------- Tests de Buscar ----------
    // Acá pruebo las búsquedas por nombre para ver que devuelva lo esperado.

    @Test
    fun buscar_cor_devuelveCordoba() = runTest(timeout = 3.seconds) {
        val estadoEsperado = CiudadesEstado.Resultado(listOf(repositorio.cordoba))

        launch(Dispatchers.Main) {
            viewModel.ejecutar(CiudadesIntencion.Buscar("cor"))
            delay(1.milliseconds)

            assertEquals(estadoEsperado, viewModel.uiState)
        }
    }

    @Test
    fun buscar_plata_devuelveLaPlata() = runTest(timeout = 3.seconds) {
        val estadoEsperado = CiudadesEstado.Resultado(listOf(repositorio.laPlata))

        launch(Dispatchers.Main) {
            viewModel.ejecutar(CiudadesIntencion.Buscar("plata"))
            delay(1.milliseconds)

            assertEquals(estadoEsperado, viewModel.uiState)
        }
    }

    @Test
    fun buscar_sinResultados_devuelveVacio() = runTest(timeout = 3.seconds) {
        val estadoEsperado = CiudadesEstado.Vacio

        launch(Dispatchers.Main) {
            viewModel.ejecutar(CiudadesIntencion.Buscar("no-existe"))
            delay(1.milliseconds)

            assertEquals(estadoEsperado, viewModel.uiState)
        }
    }

    @Test
    fun buscar_conErrorEnRepositorio_devuelveError() = runTest(timeout = 3.seconds) {
        val repoError = RepositorioError()
        val factoryError = CiudadesViewModelFactory(repoError, router, userPrefs)
        val vmError = factoryError.create(CiudadesViewModel::class.java)

        launch(Dispatchers.Main) {
            vmError.ejecutar(CiudadesIntencion.Buscar("cualquiercosa"))
            delay(1.milliseconds)

            val estado = vmError.uiState
            assertTrue(estado is CiudadesEstado.Error)
            assertEquals("error desconocido", (estado as CiudadesEstado.Error).mensaje)
        }
    }

    // ---------- Tests de BuscarGeo ----------
    // Lo mismo que buscar, pero usando coordenadas.

    @Test
    fun buscarGeo_conCoordsDeCordoba_devuelveResultado() = runTest(timeout = 3.seconds) {
        val cordoba = repositorio.cordoba
        val estadoEsperado = CiudadesEstado.Resultado(listOf(cordoba))

        launch(Dispatchers.Main) {
            viewModel.ejecutar(
                CiudadesIntencion.BuscarGeo(
                    lat = cordoba.lat,
                    lon = cordoba.lon
                )
            )
            delay(1.milliseconds)

            assertEquals(estadoEsperado, viewModel.uiState)
        }
    }

    @Test
    fun buscarGeo_conCoordsQueNoExisten_devuelveVacio() = runTest(timeout = 3.seconds) {
        val estadoEsperado = CiudadesEstado.Vacio

        launch(Dispatchers.Main) {
            viewModel.ejecutar(
                CiudadesIntencion.BuscarGeo(
                    lat = 0f,
                    lon = 0f
                )
            )
            delay(1.milliseconds)

            assertEquals(estadoEsperado, viewModel.uiState)
        }
    }

    @Test
    fun buscarGeo_conErrorEnRepositorio_devuelveError() = runTest(timeout = 3.seconds) {
        val repoError = RepositorioError()
        val factoryError = CiudadesViewModelFactory(repoError, router, userPrefs)
        val vmError = factoryError.create(CiudadesViewModel::class.java)

        launch(Dispatchers.Main) {
            vmError.ejecutar(CiudadesIntencion.BuscarGeo(1f, 1f))
            delay(1.milliseconds)

            val estado = vmError.uiState
            assertTrue(estado is CiudadesEstado.Error)
        }
    }

    // ---------- Tests de CargarRecomendadas ----------
    // Probamos que cargue recomendaciones y también el camino de error.

    @Test
    fun cargarRecomendadas_devuelveEstadoRecomendadas() = runTest(timeout = 3.seconds) {
        launch(Dispatchers.Main) {
            viewModel.ejecutar(CiudadesIntencion.CargarRecomendadas)
            delay(1.milliseconds)

            val estado = viewModel.uiState
            assertTrue(estado is CiudadesEstado.Recomendadas)

            val ciudadesRecomendadas = (estado as CiudadesEstado.Recomendadas).ciudades
            // Con el repo mock actual puede que sea 0 y como máximo 5
            assertTrue(ciudadesRecomendadas.size <= 5)
        }
    }

    @Test
    fun cargarRecomendadas_conErrorEnRepositorio_devuelveError() = runTest(timeout = 3.seconds) {
        val repoError = object : Repositorio {
            override suspend fun buscarCiudad(ciudad: String): List<Ciudad> {
                throw Exception("Fallo buscador")
            }

            override suspend fun buscarCiudadPorCoords(lat: Float, lon: Float): List<Ciudad> {
                throw Exception("Fallo coords")
            }

            override suspend fun traerClima(
                lat: Float,
                lon: Float
            ): com.appclimaparcial.myapplication.repository.modelos.Clima {
                throw Exception("Fallo clima")
            }

            override suspend fun traerPronostico(nombre: String):
                    List<com.appclimaparcial.myapplication.repository.modelos.ListForecast> {
                return emptyList()
            }
        }

        val factoryError = CiudadesViewModelFactory(repoError, router, userPrefs)
        val vmError = factoryError.create(CiudadesViewModel::class.java)

        launch(Dispatchers.Main) {
            vmError.ejecutar(CiudadesIntencion.CargarRecomendadas)
            delay(1.milliseconds)

            val estado = vmError.uiState
            assertTrue(estado is CiudadesEstado.Error)
            assertEquals(
                "Error cargando ciudades recomendadas",
                (estado as CiudadesEstado.Error).mensaje
            )
        }
    }

    // ---------- Tests de Seleccionar ----------
    // Cuando selecciono una ciudad, debería guardarse y navegar a Clima.

    @Test
    fun seleccionar_ciudad_guardaEnUserPrefsYNavegaAClima() = runTest(timeout = 3.seconds) {
        val ciudad = repositorio.cordoba

        launch(Dispatchers.Main) {
            viewModel.ejecutar(CiudadesIntencion.Seleccionar(ciudad))
            delay(1.milliseconds)

            // Se guarda la ciudad en UserPreferences
            assertEquals(ciudad, userPrefs.ultimaCiudadGuardada)

            // Navega a la ruta Clima con los datos correctos
            val ruta = router.ultimaRuta
            assertNotNull(ruta)
            assertTrue(ruta is Ruta.Clima)

            ruta as Ruta.Clima
            assertEquals(ciudad.lat, ruta.lat)
            assertEquals(ciudad.lon, ruta.lon)
            assertEquals(ciudad.name, ruta.nombre)
        }
    }
}
*/
