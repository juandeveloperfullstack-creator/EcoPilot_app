package com.example.ecopilot.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draganddrop.DragAndDropSourceModifierNode
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ecopilot.data.SessionManager
import kotlinx.coroutines.launch
import com.example.ecopilot.network.RetrofitClient

/**
 * **EcoPilotHomeScreen**: Pantalla principal de la aplicación.
 *
 * **Palabras clave / Conceptos clave:**
 * - `LazyColumn`: Lista de desplazamiento vertical optimizada (similar a RecyclerView) que solo renderiza los elementos visibles.
 * - `Card`: Contenedor visual de elevación y bordes redondeados para agrupar información.
 * - `Stateful Composable`: Componente que mantiene y reacciona al estado (`seccionActual`).
 * - `OutlinedButton`: Botón secundario con borde, utilizado para el cierre de sesión.
 *
 * @param modifier Modificador de diseño pasado desde el contenedor padre (Scaffold).
 * @param onCerrarSesion Callback para limpiar la sesión y regresar al login.
 */
@Composable
fun EcoPilotHomeScreen(
    modifier: Modifier = Modifier,
    onCerrarSesion: () -> Unit
) {
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    val nombreUsuario = remember { sessionManager.obtenerNombre() }
    val puntosUsuario = remember { sessionManager.obtenerPuntos() }
    val scope = rememberCoroutineScope()
    var cerrandoSesion by remember { mutableStateOf(false) }

    var seccionActual by remember { mutableStateOf("¡Listo para reciclar hoy!") }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // 1. Cabecera de Bienvenida Personalizada
        item {
            Text(
                text = "Hola, $nombreUsuario 👋",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Tu planeta, tu misión",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Cada acción suma. Aprende, comparte y contribuye al cambio global",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(20.dp))
        }

        // 2. Tarjeta de Resumen y Estado con Puntos Reales
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Progreso Ecológico",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Puntos acumulados: $puntosUsuario ECOs",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = seccionActual,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }

        // 3. Título de Secciones
        item {
            Text(
                text = "Explorar EcoPilot",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 12.dp)
            )
        }

        // 4. Lista de Opciones de Navegación del Menú
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {


                Spacer(modifier = Modifier.height(16.dp))

                OutlinedButton(
                    onClick = {
                        cerrandoSesion = true
                        scope.launch {
                            try {
                                val token = sessionManager.obtenerToken()
                                if (token.isNotEmpty()) {
                                    RetrofitClient.apiService.logout("Bearer $token")
                                }
                            } catch (e: Exception) {
                                // si el servidor no responde o no hay red, se cierra la sesion localmente
                            } finally {
                                cerrandoSesion = false
                                onCerrarSesion()
                            }
                        }
                    },
                    enabled  = !cerrandoSesion,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (cerrandoSesion) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            color = MaterialTheme.colorScheme.error,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(text = "Cerrar Sesión", color = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }
    }
}

/**
 * Componente reutilizable para los botones de las diferentes secciones del menú.
 *
 * @param titulo Texto del botón.
 * @param onClick Acción ejecutada al hacer clic.
 */
@Composable
fun BotonMenuSeccion(titulo: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
    ) {
        Text(
            text = titulo,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onPrimary
        )
    }
}