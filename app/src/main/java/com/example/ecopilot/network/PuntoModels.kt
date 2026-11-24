package com.example.ecopilot.network

data class PuntoRecoleccion(
    val id: Int,
    val nombre_lugar: String,
    val direccion: String,
    val latitud: Double,
    val longitud: Double,
    val telefono: String?,
    val horario_atencion: String,
    val estado_punto: String?,
    val zona_id: Int?
)