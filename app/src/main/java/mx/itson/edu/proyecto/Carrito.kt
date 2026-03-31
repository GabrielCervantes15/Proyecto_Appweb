package mx.itson.edu.proyecto

import android.content.Context

object Carrito {
    var productosSeleccionados = mutableListOf<Regalo>()

    fun cargarDeBaseDeDatos(context: Context) {
        val db = UserDBHelper(context)
        productosSeleccionados = db.obtenerCarrito()
    }

    fun agregar(regalo: Regalo, context: Context) {
        val existente = productosSeleccionados.find { it.nombre == regalo.nombre }
        if (existente != null) {
            existente.cantidad += regalo.cantidad
        } else {
            productosSeleccionados.add(regalo)
        }
        UserDBHelper(context).guardarCarrito(productosSeleccionados)
    }

    fun eliminar(posicion: Int, context: Context) {
        if (posicion in productosSeleccionados.indices) {
            productosSeleccionados.removeAt(posicion)
            UserDBHelper(context).guardarCarrito(productosSeleccionados)
        }
    }

    fun limpiar(context: Context) {
        productosSeleccionados.clear()
        UserDBHelper(context).vaciarCarritoDB()
    }

    fun calcularTotal(): Double = productosSeleccionados.sumOf { it.precio * it.cantidad }
    fun obtenerTotal(): Double = calcularTotal()
}