package mx.itson.edu.proyecto

object Carrito {
    val productosSeleccionados = mutableListOf<Regalo>()

    fun agregar(regalo: Regalo) {
        productosSeleccionados.add(regalo)
    }

    fun obtenerTotal(): Double {
        return productosSeleccionados.sumOf { it.precio }
    }
    fun calcularTotal(): Double {
        return productosSeleccionados.sumOf { it.precio }
    }
}