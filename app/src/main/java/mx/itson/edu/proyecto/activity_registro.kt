package mx.itson.edu.proyecto

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class activity_registro : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)

        val etNombre = findViewById<EditText>(R.id.etNombreRegistro)
        val etCorreo = findViewById<EditText>(R.id.etEmailRegistro)
        val etPass = findViewById<EditText>(R.id.etPassRegistro)
        val btnRegistrar = findViewById<Button>(R.id.btnFinalizarRegistro)
        val tvVolver = findViewById<TextView>(R.id.tvVolverLogin)

        val dbHelper = UserDBHelper(this)

        btnRegistrar.setOnClickListener {
            val nombre = etNombre.text.toString().trim()
            val correo = etCorreo.text.toString().trim()
            val pass = etPass.text.toString().trim()

            if (nombre.isNotEmpty() && correo.isNotEmpty() && pass.isNotEmpty()) {
                val id = dbHelper.registrarUsuario(nombre, correo, pass)

                if (id != -1L) {
                    val sharedPref = getSharedPreferences("SesionUsuario", Context.MODE_PRIVATE)
                    with(sharedPref.edit()) {
                        putBoolean("logueado", true)
                        putString("correo_usuario", correo)
                        apply()
                    }

                    Toast.makeText(this, "¡Cuenta creada con éxito!", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "Error al guardar en la base de datos", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
            }
        }

        tvVolver.setOnClickListener {
            finish()
        }
    }
}