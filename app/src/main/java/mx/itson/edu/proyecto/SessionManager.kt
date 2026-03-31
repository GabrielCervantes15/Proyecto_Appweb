package mx.itson.edu.proyecto

import android.content.Context

class SessionManager(context: Context) {
    private val prefs = context.getSharedPreferences("SesionUsuario", Context.MODE_PRIVATE)

    fun isFirstTime(): Boolean {
        return prefs.getBoolean("logueado", false).not()
    }

    fun setLoggedIn(correo: String) {
        prefs.edit().apply {
            putBoolean("logueado", true)
            putString("correo_usuario", correo)
            apply()
        }
    }

    fun getUserEmail(): String? {
        return prefs.getString("correo_usuario", "")
    }
}