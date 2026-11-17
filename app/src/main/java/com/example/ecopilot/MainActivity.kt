package com.example.ecopilot

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.ecopilot.data.SessionManager
import com.example.ecopilot.ui.screens.EcoPilotHomeScreen
import com.example.ecopilot.ui.screens.LoginScreen
import com.example.ecopilot.ui.screens.RegisterScreen
import com.example.ecopilot.ui.screens.SplashScreen
import com.example.ecopilot.ui.theme.EcoPilotTheme

/**
 * **MainActivity**: Actividad principal del flujo de la aplicación EcoPilot.
 *
 * **Palabras clave / Conceptos clave:**
 * - `ComponentActivity`: Clase base para actividades en Android que soportan Compose.
 * - `enableEdgeToEdge`: Permite que la app dibuje de borde a borde en la pantalla.
 * - `setContent`: Bloque principal de Compose que renderiza el tema y la interfaz.
 * - `SessionManager`: Clase encargada de verificar la persistencia de sesión local (`SharedPreferences`).
 * - `Navegación basada en Estado (State-based Navigation)`: Flujo de pantallas controlado por la variable `pantallaActual` (`splash` -> `login` / `register` -> `home`).
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val sessionManager = SessionManager(this)

        setContent {
            EcoPilotTheme(darkTheme = true) {
                var pantallaActual by remember { mutableStateOf("splash") }

                when (pantallaActual) {
                    "splash" -> SplashScreen(
                        onTiempoTerminado = {
                            pantallaActual = if (sessionManager.estaLogueado()) {
                                "home"
                            } else {
                                "login"
                            }
                        }
                    )
                    "login" -> LoginScreen(
                        onLoginExitoso = {
                            pantallaActual = "home"
                        },
                        onIrARegistro = { pantallaActual = "register" }
                    )
                    "register" -> RegisterScreen(
                        onRegistroExitoso = {
                            pantallaActual = "home"
                        },
                        onVolverALogin = { pantallaActual = "login" }
                    )
                    "home" -> Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                        EcoPilotHomeScreen(
                            modifier = Modifier.padding(innerPadding),
                            onCerrarSesion = {
                                sessionManager.cerrarSesion()
                                pantallaActual = "login"
                            }
                        )
                    }
                }
            }
        }
    }
}