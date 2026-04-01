package mx.itson.edu.proyecto

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

interface TiendaService {
    @POST("actualizar_stock")
    fun finalizarCompra(@Body request: CompraRequest): Call<CompraResponse>
}

data class CompraRequest(val correo: String, val tarjeta: String, val productos: List<Regalo>)
data class CompraResponse(val status: String, val message: String)

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
        tvTotal.text = "Total: $${String.format("%.2f", Carrito.obtenerTotal())}"
    }

    private fun mostrarDialogoPago() {
        val dialog = BottomSheetDialog(this)
        val dialogView = layoutInflater.inflate(R.layout.activity_diseno_pago, null)
        dialog.setContentView(dialogView)

        val etTarjeta = dialogView.findViewById<EditText>(R.id.etNumeroTarjeta)
        val etCVV = dialogView.findViewById<EditText>(R.id.etCVV)
        val btnConfirmar = dialogView.findViewById<Button>(R.id.btnConfirmarPagoReal)

        val sharedPref = getSharedPreferences("SesionUsuario", Context.MODE_PRIVATE)
        val correo = sharedPref.getString("correo_usuario", "")

        if (!correo.isNullOrEmpty()) {
            val dbHelper = UserDBHelper(this)
            val tarjetaGuardada = dbHelper.obtenerTarjeta(correo)
            if (tarjetaGuardada.isNotEmpty()) etTarjeta.setText(tarjetaGuardada)
        }

        btnConfirmar.setOnClickListener {
            val numTarjeta = etTarjeta.text.toString().trim()
            val cvv = etCVV.text.toString().trim()

            if (numTarjeta.length == 16 && cvv.length == 3) {
                if (!correo.isNullOrEmpty()) {
                    procesarPagoYStock(correo, numTarjeta, cvv, dialog)
                }
            } else {
                Toast.makeText(this, "Verifica los datos de la tarjeta", Toast.LENGTH_SHORT).show()
            }
        }
        dialog.show()
    }

    private fun procesarPagoYStock(correo: String, numTarjeta: String, cvv: String, dialog: BottomSheetDialog) {
        val montoTotal = Carrito.obtenerTotal()

        val retrofitBank = Retrofit.Builder()
            .baseUrl(Constants.URL_BANCO)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val bankService = retrofitBank.create(BankService::class.java)
        val bankRequest = BankRequest(numTarjeta, cvv, montoTotal, correo)

        bankService.autorizarPago(bankRequest).enqueue(object : Callback<BankResponse> {
            override fun onResponse(call: Call<BankResponse>, response: Response<BankResponse>) {
                if (response.isSuccessful && response.body()?.status == "success") {
                    actualizarStockEnTienda(correo, numTarjeta, dialog)
                } else {
                    val msg = response.body()?.message ?: "Pago rechazado"
                    Toast.makeText(this@activity_carrito, msg, Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<BankResponse>, t: Throwable) {
                Toast.makeText(this@activity_carrito, "Error con el banco", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun actualizarStockEnTienda(correo: String, tarjeta: String, dialog: BottomSheetDialog) {
        val retrofitStore = Retrofit.Builder()
            .baseUrl(Constants.URL_TIENDA)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofitStore.create(TiendaService::class.java)
        val request = CompraRequest(correo, tarjeta, Carrito.productosSeleccionados)

        service.finalizarCompra(request).enqueue(object : Callback<CompraResponse> {
            override fun onResponse(call: Call<CompraResponse>, response: Response<CompraResponse>) {
                if (response.isSuccessful && response.body()?.status == "success") {
                    val dbHelper = UserDBHelper(this@activity_carrito)
                    Carrito.limpiar(this@activity_carrito)
                    adapter.notifyDataSetChanged()
                    actualizarTotal()
                    dialog.dismiss()

                    Toast.makeText(this@activity_carrito, "¡Compra exitosa! Inventario actualizado.", Toast.LENGTH_LONG).show()
                    val intent = Intent(this@activity_carrito, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()

                } else {
                    val errorMsg = response.body()?.message ?: "Error al procesar el inventario"
                    Toast.makeText(this@activity_carrito, errorMsg, Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<CompraResponse>, t: Throwable) {
                Toast.makeText(this@activity_carrito, "Error de red con la tienda: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}