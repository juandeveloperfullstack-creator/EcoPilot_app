package com.example.ecopilot.ui.screens

import android.R
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.ecopilot.network.LoginRequest
import com.example.ecopilot.network.RetrofitClient
import kotlinx.coroutines.launch

/**
 * **LoginScreen**: Pantalla de inicio de sesión de usuario.
 *
 * **Palabras clave / Conceptos clave:**
 * - `remember`: Preserva el estado (valores de las variables) durante las recomposiciones de la pantalla.
 * - `mutableStateOf`: Crea un estado reactivo; cuando su valor cambia, la interfaz se actualiza automáticamente.
 * - `OutlinedTextField`: Campo de entrada de texto con borde de estilo Material Design.
 * - `PasswordVisualTransformation`: Transforma los caracteres ingresados en puntos para ocultar la contraseña.
 * - `Callback / Event Handler`: Funciones como `onLoginExitoso` y `onIrARegistro` que notifican eventos al contenedor padre.
 *
 * @param onLoginExitoso Acción a realizar cuando el inicio de sesión es correcto.
 * @param onIrARegistro Acción para navegar a la pantalla de registro.
 */
@Composable
fun LoginScreen(
    onLoginExitoso: () -> Unit,
    onIrARegistro: () -> Unit
) {
    var correo by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var cargando by remember { mutableStateOf(false) }
    var mensajeError by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Bienvenido a EcoPilot",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Inicia sesión para continuar tu misión",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = correo,
            onValueChange = { correo = it },
            label = { Text("Correo electrónico") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        if (mensajeError.isNotEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = mensajeError,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                if (correo.isBlank() || password.isBlank()) {
                    mensajeError = "Ingresa tu correo y contraseña"
                    return@Button
                }
                cargando = true
                mensajeError = ""
                scope.launch {
                    try {
                        val response = RetrofitClient.apiService.login(
                            LoginRequest(email = correo.trim(), password = password)
                        )
                        if (response.isSuccessful) {
                            onLoginExitoso()
                        } else {
                            mensajeError = "Correo o contraseña incorrectos"
                        }
                    } catch (e: Exception) {
                        mensajeError = "Error: ${e.localizedMessage}"
                    } finally {
                        cargando = false
                    }
                }
            },
            enabled = !cargando,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            if (cargando) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp
                )
            } else {
                Text(text = "Iniciar Sesión", color = MaterialTheme.colorScheme.onPrimary)
            }
        }
        Spacer(modifier = Modifier.height(12.dp))

        TextButton(onClick = { onIrARegistro() }) {
            Text(text = "¿No tienes cuenta? Regístrate aquí", color = MaterialTheme.colorScheme.secondary)
        }
    }
}
