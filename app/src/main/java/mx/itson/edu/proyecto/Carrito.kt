package mx.itson.edu.proyecto

import android.content.Context
import android.widget.Toast

object Carrito {
    val productosSeleccionados = mutableListOf<Regalo>()

    fun cargarDesdeDB(context: Context) {
        val db = UserDBHelper(context)
        val listaLocal = db.obtenerCarrito()
        productosSeleccionados.clear()
        productosSeleccionados.addAll(listaLocal)
    }

    fun agregar(regalo: Regalo, context: Context) {
        val existente = productosSeleccionados.find { it.nombre == regalo.nombre }

        if (existente != null) {
            existente.cantidad += regalo.cantidad
        } else {
            productosSeleccionados.add(regalo)
        }

        try {
            val dbHelper = UserDBHelper(context)
            dbHelper.guardarCarrito(productosSeleccionados)
            Toast.makeText(context, "${regalo.nombre} añadido al carrito", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(context, "Error al guardar localmente", Toast.LENGTH_SHORT).show()
        }
    }

    fun eliminar(posicion: Int, context: Context) {
        if (posicion in productosSeleccionados.indices) {
            val nombreProducto = productosSeleccionados[posicion].nombre
            productosSeleccionados.removeAt(posicion)

            val dbHelper = UserDBHelper(context)
            dbHelper.eliminarProductoIndividual(nombreProducto)
        }
    }

    fun limpiar(context: Context) {
        productosSeleccionados.clear()
        val dbHelper = UserDBHelper(context)
        dbHelper.vaciarCarritoDB()
    }

    fun obtenerTotal(): Double {
        return productosSeleccionados.sumOf { it.precio * it.cantidad }
    }
}