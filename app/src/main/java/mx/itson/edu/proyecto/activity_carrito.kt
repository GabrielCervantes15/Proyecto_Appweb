package mx.itson.edu.proyecto

import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog

class activity_carrito : AppCompatActivity() {
    private lateinit var adapter: RegaloAdapter
    private lateinit var tvTotal: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_carrito)

        val rvCarrito = findViewById<RecyclerView>(R.id.rvCartItems)
        tvTotal = findViewById(R.id.tvTotalCart)
        val btnPagar = findViewById<Button>(R.id.btnFinalizarCompra)

        actualizarTotal()

        adapter = RegaloAdapter(Carrito.productosSeleccionados) {
            actualizarTotal()
        }

        rvCarrito.layoutManager = LinearLayoutManager(this)
        rvCarrito.adapter = adapter

        btnPagar.setOnClickListener {
            if (Carrito.productosSeleccionados.isEmpty()) {
                Toast.makeText(this, "El carrito está vacío", Toast.LENGTH_SHORT).show()
            } else {
                mostrarDialogoPago()
            }
        }
    }

    private fun actualizarTotal() {
        tvTotal.text = "Total: $${Carrito.obtenerTotal()}"
    }

    private fun mostrarDialogoPago() {
        val dialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.activity_diseno_pago, null)
        dialog.setContentView(view)

        val etTarjeta = view.findViewById<EditText>(R.id.etNumeroTarjeta)
        val btnConfirmar = view.findViewById<Button>(R.id.btnConfirmarPagoReal)

        val sharedPref = getSharedPreferences("SesionUsuario", Context.MODE_PRIVATE)
        val correo = sharedPref.getString("correo_usuario", "")

        if (!correo.isNullOrEmpty()) {
            val dbHelper = UserDBHelper(this)
            val tarjetaGuardada = dbHelper.obtenerTarjeta(correo)

            if (tarjetaGuardada.isNotEmpty()) {
                etTarjeta.setText(tarjetaGuardada)
            }
        }

        btnConfirmar.setOnClickListener {
            val numTarjeta = etTarjeta.text.toString().trim()

            if (numTarjeta.length == 16) {
                if (!correo.isNullOrEmpty()) {
                    val dbHelper = UserDBHelper(this)
                    val resultado = dbHelper.actualizarTarjeta(correo, numTarjeta)

                    if (resultado > 0) {
                        Toast.makeText(this, "Pago procesado con éxito", Toast.LENGTH_LONG).show()
                        Carrito.limpiar(this)
                        dialog.dismiss()
                        finish()
                    } else {
                        Toast.makeText(this, "Error al procesar el pago", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "La tarjeta debe tener 16 dígitos", Toast.LENGTH_SHORT).show()
            }
        }
        dialog.show()
    }
}