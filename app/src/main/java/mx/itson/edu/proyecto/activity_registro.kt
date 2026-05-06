package mx.itson.edu.proyecto

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class activity_registro : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)

        auth = FirebaseAuth.getInstance()

        val etNombre = findViewById<EditText>(R.id.etNombreRegistro)
        val etCorreo = findViewById<EditText>(R.id.etEmailRegistro)
        val etPass = findViewById<EditText>(R.id.etPassRegistro)
        val btnRegistrar = findViewById<Button>(R.id.btnFinalizarRegistro)
        val tvVolver = findViewById<TextView>(R.id.tvVolverLogin)

        btnRegistrar.setOnClickListener {
            val nombre = etNombre.text.toString().trim()
            val correo = etCorreo.text.toString().trim()
            val pass = etPass.text.toString().trim()

            if (nombre.isNotEmpty() && correo.isNotEmpty() && pass.isNotEmpty()) {
                auth.createUserWithEmailAndPassword(correo, pass)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            val userId = auth.currentUser?.uid
                            val dbRef = FirebaseDatabase.getInstance().getReference("usuarios")

                            val datosUsuario = mapOf(
                                "nombre" to nombre,
                                "correo" to correo
                            )

                            userId?.let {
                                dbRef.child(it).setValue(datosUsuario)
                            }

                            val sharedPref = getSharedPreferences("SesionUsuario", Context.MODE_PRIVATE)
                            with(sharedPref.edit()) {
                                putBoolean("logueado", true)
                                putString("correo_usuario", correo)
                                putString("nombre_usuario", nombre)
                                apply()
                            }

                            Toast.makeText(this, "¡Cuenta creada con éxito!", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this, MainActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(this, "Error: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
            } else {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
            }
        }

        tvVolver.setOnClickListener {
            finish()
        }
    }
}