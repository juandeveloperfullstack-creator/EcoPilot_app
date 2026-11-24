package com.example.ecopilot.data

import android.content.Context

/**
 * **SessionManager**: Gestor de sesión de usuario utilizando `SharedPreferences`.
 *
 * **Palabras clave / Conceptos clave:**
 * - `SharedPreferences`: Almacenamiento local clave-valor persistente en Android.
 * - `Context.MODE_PRIVATE`: Garantiza que solo esta aplicación pueda leer/escribir las preferencias.
 * - `IS_LOGGED_IN`: Clave (key) que almacena el estado booleano de autenticación.
 *
 * @property context El contexto de la aplicación necesario para acceder a `SharedPreferences`.
 */
class SessionManager(context: Context) {
    private val prefs = context.getSharedPreferences("ecopilot_session", Context.MODE_PRIVATE)

    /**
     * Guarda el estado de la sesión del usuario.
     * @param estaLogueado `true` si el usuario inició sesión, `false` de lo contrario.
     */
    fun guardarSesion(token: String, nombre: String, puntos: Int) {
        prefs.edit()
            .putBoolean("is_logged_in", true)
            .putString("auth_token", token)
            .putString("user_name", nombre)
            .putInt("user_points", puntos)
            .apply()
    }

    fun estaLogueado(): Boolean = prefs.getBoolean("is_logged_in", false)

    fun obtenerToken(): String = prefs.getString("auth_token", "") ?: ""

    fun obtenerNombre(): String = prefs.getString("user_name", "Explorador") ?: "Explorador"

    fun obtenerPuntos(): Int = prefs.getInt("user_points", 0)

    fun cerrarSesion() {
        prefs.edit().clear().apply()
    }
}
