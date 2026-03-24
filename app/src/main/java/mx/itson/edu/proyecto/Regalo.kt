package mx.itson.edu.proyecto

import java.io.Serializable

data class Regalo(
    val nombre: String,
    val precio: Double,
    val descripcion: String,
    val categoria: String,
    val imagenRes: Int
) : Serializable