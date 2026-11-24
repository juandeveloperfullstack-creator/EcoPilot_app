package com.example.ecopilot.ui.layouts

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ecopilot.ui.screens.EcoPilotHomeScreen
import com.example.ecopilot.ui.screens.PuntosRecoleccionScreen

enum class SeccionApp(val titulo: String, val emoji: String) {
    INICIO("Inicio", "🏠"),
    PUNTOS("Puntos", "📍"),
    TIENDA("Tienda", "🎁"),
    MANUALIDADES("Manualidades", "♻️"),
    MAS("Más", "☰")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainLayout(
    onCerrarSesion: () -> Unit
) {
    var seccionSeleccionada by remember { mutableStateOf(SeccionApp.INICIO) }
    var mostrarMenuMas by remember { mutableStateOf(false) }
    var mostrarChatTiti by remember { mutableStateOf(false) }

    if (mostrarMenuMas) {
        ModalBottomSheet(
            onDismissRequest = { mostrarMenuMas = false },
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Más opciones",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(4.dp))

                NavigationDrawerItem(
                    icon = { Text("👤", fontSize = 18.sp) },
                    label = { Text("Mi perfil") },
                    selected = false,
                    onClick = { mostrarMenuMas = false }
                )
                NavigationDrawerItem(
                    icon = { Text("📰", fontSize = 18.sp) },
                    label = { Text("Noticias") },
                    selected = false,
                    onClick = { mostrarMenuMas = false }
                )
                NavigationDrawerItem(
                    icon = { Text("🌿", fontSize = 18.sp) },
                    label = { Text("¿Quiénes somos?") },
                    selected = false,
                    onClick = { mostrarMenuMas = false }
                )
                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                NavigationDrawerItem(
                    icon = { Text("🚪", fontSize = 18.sp) },
                    label = { Text("Cerrar Sesión", color = MaterialTheme.colorScheme.error) },
                    selected = false,
                    onClick = {
                        mostrarMenuMas = false
                        onCerrarSesion()
                    }
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    if (mostrarChatTiti) {
        AlertDialog(
            onDismissRequest = { mostrarChatTiti = false },
            title = { Text("Chat con Tití 🐒", fontWeight = FontWeight.Bold) },
            text = { Text("¡Hola! Soy Tití, tu asistente de EcoPilot. Pronto conectaremos mis respuestas con tu API.") },
            confirmButton = {
                TextButton(onClick = { mostrarChatTiti = false }) {
                    Text("Cerrar")
                }
            }
        )
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                SeccionApp.entries.forEach { seccion ->
                    val seleccionado = seccionSeleccionada == seccion && seccion != SeccionApp.MAS
                    NavigationBarItem(
                        selected = seleccionado,
                        onClick = {
                            if (seccion == SeccionApp.MAS) {
                                mostrarMenuMas = true
                            } else {
                                seccionSeleccionada = seccion
                            }
                        },
                        icon = {
                            Text(
                                text = seccion.emoji,
                                fontSize = 18.sp
                            )
                        },
                        label = {
                            Text(
                                text = seccion.titulo,
                                style = MaterialTheme.typography.labelSmall,
                                maxLines = 1
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primary
                        )
                    )
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { mostrarChatTiti = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Text(
                    text = "🐒",
                    fontSize = 22.sp
                )
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (seccionSeleccionada) {
                SeccionApp.INICIO -> EcoPilotHomeScreen(onCerrarSesion = onCerrarSesion)
                SeccionApp.PUNTOS -> PuntosRecoleccionScreen()
                SeccionApp.TIENDA -> PlaceholderModuloScreen("Tienda y Recompensas")
                SeccionApp.MANUALIDADES -> PlaceholderModuloScreen("Manualidades Ecológicas")
                SeccionApp.MAS -> Unit
            }
        }
    }
}

@Composable
fun PlaceholderModuloScreen(titulo: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = titulo,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Conectar API.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}