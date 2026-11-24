package com.example.ecopilot.ui.screens

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.preference.PreferenceManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.ecopilot.network.PuntoRecoleccion
import com.example.ecopilot.network.RetrofitClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import kotlin.math.*

@Composable
fun PuntosRecoleccionScreen() {
    val context = LocalContext.current

    // Inicializar configuración de osmdroid con User-Agent propio
    LaunchedEffect(Unit) {
        Configuration.getInstance().apply {
            load(context, PreferenceManager.getDefaultSharedPreferences(context))
            userAgentValue = "EcoPilot-Popayan/1.0 (Android)"
        }
    }

    var puntos by remember { mutableStateOf<List<PuntoRecoleccion>>(emptyList()) }
    var cargando by remember { mutableStateOf(true) }
    var errorMensaje by remember { mutableStateOf<String?>(null) }

    // Coordenadas actuales del usuario (null hasta que presione "Usar mi ubicación")
    var ubicacionUsuario by remember { mutableStateOf<Pair<Double, Double>?>(null) }
    var mensajeUbicacion by remember { mutableStateOf("Activa tu ubicación para ver los puntos más cercanos a ti.") }

    // Cliente de ubicación GPS de Google Play Services
    val fusedLocationClient = remember {
        LocationServices.getFusedLocationProviderClient(context)
    }

    @SuppressLint("MissingPermission")
    fun obtenerGpsReal() {
        mensajeUbicacion = "Buscando tu ubicación GPS..."
        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
            .addOnSuccessListener { location ->
                if (location != null) {
                    ubicacionUsuario = Pair(location.latitude, location.longitude)
                    mensajeUbicacion = "Ubicación detectada. Puntos ordenados por cercanía."
                } else {
                    mensajeUbicacion = "No se pudo obtener el GPS. Verifica que la ubicación esté encendida."
                }
            }
            .addOnFailureListener {
                mensajeUbicacion = "Error al leer GPS: ${it.message}"
            }
    }

    // Lanzador para pedir permiso de ubicación en tiempo de ejecución
    val permisoGpsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permisos ->
        val concedido = permisos[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permisos[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (concedido) {
            obtenerGpsReal()
        } else {
            mensajeUbicacion = "Permiso de ubicación denegado."
        }
    }

    // Cargar puntos desde Laravel (GET api/puntos)
    LaunchedEffect(Unit) {
        try {
            val response = RetrofitClient.apiService.obtenerPuntos()
            if (response.isSuccessful) {
                val lista = response.body() ?: emptyList()
                puntos = lista.filter {
                    it.estado_punto == null || it.estado_punto.equals("activo", ignoreCase = true)
                }
            } else {
                errorMensaje = "Error al cargar puntos (${response.code()})"
            }
        } catch (e: Exception) {
            errorMensaje = "Sin conexión con el servidor: ${e.message}"
        } finally {
            cargando = false
        }
    }

    // Ordenar puntos por distancia si ya tenemos la ubicación del usuario
    val puntosOrdenados = remember(puntos, ubicacionUsuario) {
        val userLoc = ubicacionUsuario
        if (userLoc == null) {
            puntos.map { Pair(it, null as Double?) }
        } else {
            puntos.map { punto ->
                val distKm = calcularDistanciaKm(
                    userLoc.first, userLoc.second,
                    punto.latitud, punto.longitud
                )
                Pair(punto, distKm)
            }.sortedBy { it.second }
        }
    }

    // Punto donde se centrará el mapa (punto más cercano si hay GPS, o centro de Popayán por defecto)
    val centroMapa = remember(puntosOrdenados, ubicacionUsuario) {
        val masCercano = puntosOrdenados.firstOrNull()?.first
        when {
            ubicacionUsuario != null && masCercano != null -> GeoPoint(masCercano.latitud, masCercano.longitud)
            ubicacionUsuario != null -> GeoPoint(ubicacionUsuario!!.first, ubicacionUsuario!!.second)
            masCercano != null -> GeoPoint(masCercano.latitud, masCercano.longitud)
            else -> GeoPoint(2.4448, -76.6147) // Popayán, Cauca
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        // Encabezado
        item {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "Puntos de Recolección en Popayán",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Encuentra puntos oficiales para llevar materiales reciclables.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${puntos.size} puntos oficiales",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )

                    Button(
                        onClick = {
                            val tienePermiso = ContextCompat.checkSelfPermission(
                                context,
                                Manifest.permission.ACCESS_FINE_LOCATION
                            ) == PackageManager.PERMISSION_GRANTED

                            if (tienePermiso) {
                                obtenerGpsReal()
                            } else {
                                permisoGpsLauncher.launch(
                                    arrayOf(
                                        Manifest.permission.ACCESS_FINE_LOCATION,
                                        Manifest.permission.ACCESS_COARSE_LOCATION
                                    )
                                )
                            }
                        },
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("📍 Usar mi ubicación")
                    }
                }

                Text(
                    text = mensajeUbicacion,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Mapa interactivo con tiles WIKIMEDIA
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                AndroidView(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(16.dp)),
                    factory = { ctx ->
                        MapView(ctx).apply {
                            setTileSource(TileSourceFactory.WIKIMEDIA)
                            setMultiTouchControls(true)
                            controller.setZoom(14.0)
                            controller.setCenter(centroMapa)
                        }
                    },
                    update = { mapView ->
                        mapView.overlays.clear()
                        mapView.controller.setCenter(centroMapa)

                        // Marcador de la ubicación actual del usuario
                        ubicacionUsuario?.let { (lat, lon) ->
                            val markerUsuario = Marker(mapView).apply {
                                position = GeoPoint(lat, lon)
                                title = "Tu ubicación actual"
                                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                            }
                            mapView.overlays.add(markerUsuario)
                        }

                        // Marcadores de los puntos de recolección
                        puntosOrdenados.forEach { (punto, distKm) ->
                            val marker = Marker(mapView).apply {
                                position = GeoPoint(punto.latitud, punto.longitud)
                                title = punto.nombre_lugar
                                snippet = if (distKm != null) {
                                    "${punto.direccion} • A ${String.format("%.2f", distKm)} km"
                                } else {
                                    punto.direccion
                                }
                                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                            }
                            mapView.overlays.add(marker)
                        }

                        mapView.invalidate()
                    }
                )
            }
        }

        // Estados de carga o error
        if (cargando) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        } else if (errorMensaje != null) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = errorMensaje!!,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        } else {
            // Lista de tarjetas de puntos
            items(puntosOrdenados) { (punto, distanciaKm) ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = punto.nombre_lugar,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.weight(1f)
                            )

                            if (distanciaKm != null) {
                                Surface(
                                    color = MaterialTheme.colorScheme.primaryContainer,
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = "A ${String.format("%.2f", distanciaKm)} km",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }

                        Text(
                            text = "📍 ${punto.direccion}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Text(
                            text = "🕒 Horario: ${punto.horario_atencion}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        if (!punto.telefono.isNullOrBlank()) {
                            Text(
                                text = "📞 Teléfono: ${punto.telefono}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        OutlinedButton(
                            onClick = {
                                val uri = Uri.parse(
                                    "geo:${punto.latitud},${punto.longitud}?q=${punto.latitud},${punto.longitud}(${Uri.encode(punto.nombre_lugar)})"
                                )
                                val intent = Intent(Intent.ACTION_VIEW, uri)
                                context.startActivity(intent)
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("🗺️ Abrir ruta en Google Maps")
                        }
                    }
                }
            }
        }
    }
}

/**
 * Fórmula de Haversine para calcular la distancia en línea recta (en km)
 * entre la ubicación del usuario y cada punto de recolección.
 */
private fun calcularDistanciaKm(
    lat1: Double, lon1: Double,
    lat2: Double, lon2: Double
): Double {
    val radioTierraKm = 6371.0
    val dLat = Math.toRadians(lat2 - lat1)
    val dLon = Math.toRadians(lon2 - lon1)
    val a = sin(dLat / 2).pow(2) +
            cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
            sin(dLon / 2).pow(2)
    val c = 2 * atan2(sqrt(a), sqrt(1 - a))
    return radioTierraKm * c
}