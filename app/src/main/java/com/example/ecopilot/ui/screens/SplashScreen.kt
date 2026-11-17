package com.example.ecopilot.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ecopilot.R
import kotlinx.coroutines.delay

/**
 * **SplashScreen**: Pantalla de bienvenida / carga inicial de EcoPilot.
 *
 * **Palabras clave / Conceptos clave:**
 * - `@Composable`: Función de Jetpack Compose que define un elemento de la interfaz de usuario.
 * - `LaunchedEffect`: Efecto secundario (Side-Effect) que ejecuta corrutinas de manera segura dentro del ciclo de vida del Composable.
 * - `delay`: Suspende la corrutina durante un tiempo determinado (ejemplo: 2.5 segundos) sin bloquear el hilo principal.
 * - `CircularProgressIndicator`: Componente visual de Material 3 que muestra una animación de carga circular.
 *
 * @param onTiempoTerminado Callback ejecutado cuando finaliza el temporizador del Splash.
 */
@Composable
fun SplashScreen(onTiempoTerminado: () -> Unit) {
    // Se ejecuta una sola vez cuando el Composable entra en la composición
    LaunchedEffect(Unit) {
        delay(2500)
        onTiempoTerminado()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo_ecopilot),
            contentDescription = "Logo EcoPilot",
            modifier = Modifier
                .size(150.dp)
                .clip(CircleShape)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Ecopilot",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Tu planeta tu Misión",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(32.dp))

        CircularProgressIndicator(
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(32.dp)
        )
    }
}
