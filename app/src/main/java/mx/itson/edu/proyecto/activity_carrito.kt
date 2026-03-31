package mx.itson.edu.proyecto

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog

class activity_carrito : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_carrito)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val rvCart = findViewById<RecyclerView>(R.id.rvCartItems)
        val tvTotal = findViewById<TextView>(R.id.tvTotalCart)
        val btnPagar = findViewById<Button>(R.id.btnFinalizarCompra)

        val adapter = RegaloAdapter(Carrito.productosSeleccionados) {
            tvTotal.text = "$${Carrito.calcularTotal()}"
            if (Carrito.productosSeleccionados.isEmpty()) {
                Toast.makeText(this, "El carrito está vacío", Toast.LENGTH_SHORT).show()
            }
        }

        rvCart.adapter = adapter
        rvCart.layoutManager = LinearLayoutManager(this)

        tvTotal.text = "$${Carrito.calcularTotal()}"

        btnPagar.setOnClickListener {
            val montoActual = Carrito.calcularTotal()
            if (montoActual > 0) {
                mostrarVentanaPago(montoActual)
            } else {
                Toast.makeText(this, "El carrito está vacío. ¡Agrega un regalo!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun mostrarVentanaPago(monto: Double) {
        val dialog = BottomSheetDialog(this)
        val vista = layoutInflater.inflate(R.layout.activity_diseno_pago, null)
        val btnConfirmar = vista.findViewById<Button>(R.id.btnConfirmarPagoReal)
        val etTarjeta = vista.findViewById<EditText>(R.id.etNumeroTarjeta)

        btnConfirmar.text = "Confirmar y Pagar $$monto"

        btnConfirmar.setOnClickListener {
            val numero = etTarjeta.text.toString()
            if (numero.length < 16) {
                etTarjeta.error = "Número incompleto (16 dígitos)"
            } else {
                dialog.dismiss()
                procesarPagoFinal(monto)
            }
        }

        dialog.setContentView(vista)
        dialog.show()
    }

    private fun procesarPagoFinal(monto: Double) {
        Toast.makeText(this, "Procesando pago de $$monto...", Toast.LENGTH_SHORT).show()

        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
            Carrito.productosSeleccionados.clear()
            Toast.makeText(this, "¡Compra Exitosa! El carrito se ha vaciado.", Toast.LENGTH_LONG).show()

            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }, 2000)
    }
}