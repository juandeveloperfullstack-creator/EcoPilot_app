package com.example.ecopilot.network

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

/**
 * **ApiService**: Interfaz que define las peticiones HTTP enviadas al servidor (Laravel).
 *
 * **Librería usada: Retrofit**
 * - Conecta la app con el servidor backend mediante internet.
 * - `@POST`: Envía datos al servidor (iniciar sesión, registrarse, cerrar sesión).
 * - `@GET`: Pide información al servidor (lista de puntos de recolección).
 * - `@Body`: Envía datos estructurados (como correo y contraseña) en formato JSON.
 * - `@Header`: Envía datos de cabecera como el Token de autenticación del usuario.
 * - `suspend`: Ejecuta la petición en segundo plano para no congelar la pantalla.
 */
interface ApiService {

    /** Inicia sesión enviando correo y contraseña. Devuelve un token de acceso y datos del usuario. */
    @Headers("Accept: application/json")
    @POST("v1/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    /** Crea una cuenta de usuario nueva con sus datos personales. */
    @Headers("Accept: application/json")
    @POST("v1/register")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>

    /** Notifica al servidor el cierre de sesión inhabilitando el token actual. */
    @Headers("Accept: application/json")
    @POST("v1/logout")
    suspend fun logout(
        @Header("Authorization") token: String
    ): Response<Unit>

    /** Obtiene la lista de puntos de recolección registrados en la ciudad. */
    @GET("v1/puntos")
    suspend fun obtenerPuntos(): Response<List<PuntoRecoleccion>>
}
