package mx.itson.edu.proyecto

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class activity_detail : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_detail)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val imgDetalle = findViewById<ImageView>(R.id.ivProductLarge)
        val tvNombre = findViewById<TextView>(R.id.tvDetailTitle)
        val tvPrecio = findViewById<TextView>(R.id.tvDetailPrice)
        val tvDescripcion = findViewById<TextView>(R.id.tvDescription)
        val btnAgregar = findViewById<Button>(R.id.btnAddToCart)
        val regaloSeleccionado = intent.getSerializableExtra("objetoRegalo") as? Regalo

        if (regaloSeleccionado != null) {
            tvNombre.text = regaloSeleccionado.nombre
            tvPrecio.text = "$${regaloSeleccionado.precio}"
            tvDescripcion.text = regaloSeleccionado.descripcion
            imgDetalle.setImageResource(regaloSeleccionado.imagenRes)


            btnAgregar.setOnClickListener {
                Carrito.productosSeleccionados.add(regaloSeleccionado)
                Toast.makeText(this, "${regaloSeleccionado.nombre} agregado al carrito", Toast.LENGTH_SHORT).show()
                finish()
            }
        } else {
            Toast.makeText(this, "Error al cargar el producto", Toast.LENGTH_SHORT).show()
        }

    }
}