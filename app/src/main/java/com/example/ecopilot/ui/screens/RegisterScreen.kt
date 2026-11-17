package com.example.ecopilot.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.ecopilot.data.SessionManager
import com.example.ecopilot.network.RegisterRequest
import com.example.ecopilot.network.RetrofitClient
import kotlinx.coroutines.launch

@Composable
fun RegisterScreen(
    onRegistroExitoso: () -> Unit,
    onVolverALogin: () -> Unit
) {
    var nombre by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmarPassword by remember { mutableStateOf("") }
    var cargando by remember { mutableStateOf(false) }
    var mensajeError by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Crear Cuenta",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Únete a la comunidad EcoPilot",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Nombre completo") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = correo,
            onValueChange = { correo = it },
            label = { Text("Correo electrónico") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña (mín. 6 caracteres)") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = confirmarPassword,
            onValueChange = { confirmarPassword = it },
            label = { Text("Confirmar contraseña") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        if (mensajeError.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = mensajeError,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                when {
                    nombre.isBlank() || correo.isBlank() || password.isBlank() -> {
                        mensajeError = "Por favor completa todos los campos"
                    }
                    password.length < 6 -> {
                        mensajeError = "La contraseña debe tener al menos 6 caracteres"
                    }
                    password != confirmarPassword -> {
                        mensajeError = "Las contraseñas no coinciden"
                    }
                    else -> {
                        cargando = true
                        mensajeError = ""
                        scope.launch {
                            try {
                                val response = RetrofitClient.apiService.register(
                                    RegisterRequest(
                                        name = nombre.trim(),
                                        email = correo.trim(),
                                        password = password,
                                        password_confirmation = confirmarPassword
                                    )
                                )
                                if (response.isSuccessful) {
                                    val authData = response.body()
                                    if (authData != null) {
                                        sessionManager.guardarSesion(
                                            token = authData.token,
                                            nombre = authData.user.name,
                                            puntos = authData.user.puntos_totales
                                        )
                                    }
                                    onRegistroExitoso()
                                } else {
                                    mensajeError = "No se pudo registrar (verifica si el correo ya existe)"
                                }
                            } catch (e: Exception) {
                                mensajeError = "Error de conexión con el servidor"
                            } finally {
                                cargando = false
                            }
                        }
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
                Text(text = "Registrarse", color = MaterialTheme.colorScheme.onPrimary)
            }
        }
        Spacer(modifier = Modifier.height(12.dp))

        TextButton(onClick = { onVolverALogin() }) {
            Text(text = "¿Ya tienes cuenta? Inicia sesión", color = MaterialTheme.colorScheme.secondary)
        }
    }
}