package mx.itson.edu.proyecto

import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide

class activity_detail : AppCompatActivity() {
    private var cantidadSeleccionada = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_detail)

        val mainView = findViewById<android.view.View>(R.id.main)
        if (mainView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(mainView) { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
                insets
            }
        }

        val imgDetalle = findViewById<ImageView>(R.id.ivProductLarge)
        val tvNombre = findViewById<TextView>(R.id.tvDetailTitle)
        val tvPrecio = findViewById<TextView>(R.id.tvDetailPrice)
        val tvDescripcion = findViewById<TextView>(R.id.tvDescription)
        val btnAgregar = findViewById<Button>(R.id.btnAddToCart)

        val tvQty = findViewById<TextView>(R.id.tvQuantity)
        val btnPlus = findViewById<ImageButton>(R.id.btnPlus)
        val btnMinus = findViewById<ImageButton>(R.id.btnMinus)

        val regaloSeleccionado = intent.getSerializableExtra("objetoRegalo") as? Regalo

        if (regaloSeleccionado != null) {
            tvNombre.text = regaloSeleccionado.nombre
            tvPrecio.text = "$${regaloSeleccionado.precio}"
            tvDescripcion.text = regaloSeleccionado.descripcion

            Glide.with(this)
                .load(regaloSeleccionado.imagenUrl)
                .into(imgDetalle)

            btnPlus.setOnClickListener {
                cantidadSeleccionada++
                tvQty.text = cantidadSeleccionada.toString()
            }

            btnMinus.setOnClickListener {
                if (cantidadSeleccionada > 1) {
                    cantidadSeleccionada--
                    tvQty.text = cantidadSeleccionada.toString()
                }
            }

            btnAgregar.setOnClickListener {
                regaloSeleccionado.cantidad = cantidadSeleccionada
                Carrito.agregar(regaloSeleccionado, this)

                Toast.makeText(this, "${regaloSeleccionado.nombre} ($cantidadSeleccionada) agregado", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
}