/*package com.appclimaparcial.myapplication

import android.content.Context
import android.content.SharedPreferences
import com.appclimaparcial.myapplication.presentacion.Ciudades.CiudadesEstado
import com.appclimaparcial.myapplication.presentacion.Ciudades.CiudadesIntencion
import com.appclimaparcial.myapplication.presentacion.Ciudades.CiudadesViewModel
import com.appclimaparcial.myapplication.presentacion.Ciudades.CiudadesViewModelFactory
import com.appclimaparcial.myapplication.repository.RepositorioApi
import com.appclimaparcial.myapplication.repository.RepositorioMock
import com.appclimaparcial.myapplication.repository.RepositorioMockError
import com.appclimaparcial.myapplication.repository.UserPreferences
import com.appclimaparcial.myapplication.router.MockRouter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Test

import org.junit.Assert.*
import org.junit.Before
import org.mockito.Mockito.mock
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import kotlin.jvm.java
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {

    //Thread para simular la UI
    private val mainThreadSurrogate = newSingleThreadContext("UI thread")

    //Dependencias mock
    val repositorio = RepositorioMock()
    val router = MockRouter()
    private val mockContext: Context = mock()
    private val mockPrefs: SharedPreferences = mock()
    private val userPref: UserPreferences

    init {
        // Tell Mockito: "When getSharedPreferences is called on mockContext, return our mockPrefs"
        whenever(mockContext.getSharedPreferences(any(), any())).thenReturn(mockPrefs)
        userPref = UserPreferences(mockContext)
    }


    val repositorioError = RepositorioMock()

    //Armo ViewModel
    val factory = CiudadesViewModelFactory(repositorio, router, userPref)
    val viewModel = factory.create(CiudadesViewModel::class.java)

    @Before
    fun setUp() {
        Dispatchers.setMain(mainThreadSurrogate)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        mainThreadSurrogate.close()
    }

    @Test
    fun ciudadesViewModel_buscar_cor()  = runTest(timeout = 3.seconds) {
        //Creo Valor esperado
        val estadoEsperado = CiudadesEstado.Resultado(listOf(repositorio.cordoba))

        launch(Dispatchers.Main) {
            viewModel.ejecutar(intencion = CiudadesIntencion.Buscar("cor"))
            delay(1.milliseconds)
            assertEquals(estadoEsperado, viewModel.uiState)
        }
    }

    @Test
    fun ciudadesViewModel_buscar_plata()  = runTest(timeout = 3.seconds) {
        //Creo Valor esperado
        val estadoEsperado = CiudadesEstado.Resultado(listOf(repositorio.laPlata))

        launch(Dispatchers.Main) {
            viewModel.ejecutar(intencion = CiudadesIntencion.Buscar("plata"))
            delay(1.milliseconds)
            assertEquals(estadoEsperado, viewModel.uiState)
        }
    }

    @Test
    fun ciudadesViewModel_buscar_vacio()  = runTest(timeout = 3.seconds) {
        //Creo Valor esperado
        val estadoEsperado = CiudadesEstado.Vacio

        launch(Dispatchers.Main) {
            viewModel.ejecutar(intencion = CiudadesIntencion.Buscar("jojo"))
            delay(1.milliseconds)
            assertEquals(estadoEsperado, viewModel.uiState)
        }
    }

    @Test
    fun ciudadesViewModel_buscar_error()  = runTest(timeout = 3.seconds) {

        val repositorioError = RepositorioMockError()

        //Armo ViewModel
        val fa = CiudadesViewModelFactory(repositorioError, router, userPref)
        val vm = fa.create(CiudadesViewModel::class.java)

        //Creo Valor esperado
        val estadoEsperado = CiudadesEstado.Error("error desconocido")

        launch(Dispatchers.Main) {
            vm.ejecutar(intencion = CiudadesIntencion.Buscar("jojo"))
            delay(1.milliseconds)
            assertEquals(estadoEsperado, vm.uiState)
        }
    }

    @Test
    fun testJojo(){
        assertEquals(4, 2+2)
    }
}*/