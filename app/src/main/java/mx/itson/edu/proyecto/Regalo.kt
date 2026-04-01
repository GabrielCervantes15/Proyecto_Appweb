package mx.itson.edu.proyecto

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Regalo(
    @SerializedName("nombre") val nombre: String = "",
    @SerializedName("precio") val precio: Double = 0.0,
    @SerializedName("descripcion") val descripcion: String = "",
    @SerializedName("categoria") val categoria: String = "",
    @SerializedName("imagen_url") val imagenUrl: String = "",
    var cantidad: Int = 1
) : Serializable