package mx.itson.edu.proyecto

object Carrito {
    val productosSeleccionados = mutableListOf<Regalo>()

    fun agregar(regalo: Regalo) {
        productosSeleccionados.add(regalo)
    }

    fun calcularTotal(): Double {
        return productosSeleccionados.sumOf { it.precio * it.cantidad }
    }

    fun obtenerTotal(): Double = calcularTotal()
}