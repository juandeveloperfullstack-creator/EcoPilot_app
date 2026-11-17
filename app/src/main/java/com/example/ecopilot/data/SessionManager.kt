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

    private val prefs = context.getSharedPreferences("ecopilot_prefs", Context.MODE_PRIVATE)

    /**
     * Guarda el estado de la sesión del usuario.
     * @param estaLogueado `true` si el usuario inició sesión, `false` de lo contrario.
     */
    fun guardarSesion(estaLogueado: Boolean) {
        prefs.edit().putBoolean("IS_LOGGED_IN", estaLogueado).apply()
    }

    /**
     * Verifica si hay una sesión activa persistida.
     * @return `true` si el usuario está logueado, `false` por defecto.
     */
    fun estaLogueado(): Boolean {
        return prefs.getBoolean("IS_LOGGED_IN", false)
    }

    /**
     * Elimina los datos de sesión almacenados (Cerrar sesión).
     */
    fun cerrarSesion() {
        prefs.edit().clear().apply()
    }
}
