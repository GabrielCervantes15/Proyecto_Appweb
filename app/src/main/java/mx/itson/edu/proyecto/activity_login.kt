package mx.itson.edu.proyecto

import android.content.Context
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
import com.google.firebase.auth.FirebaseAuth

class activity_login : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = FirebaseAuth.getInstance()

        if (auth.currentUser != null) {
            irAMain()
            return
        }

        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        val mainView = findViewById<android.view.View>(R.id.main)
        mainView?.let {
            ViewCompat.setOnApplyWindowInsetsListener(it) { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
                insets
            }
        }

        val tvRegistrate = findViewById<TextView>(R.id.tvRegister)
        val btnEntrar = findViewById<Button>(R.id.btnLogin)
        val etCorreo = findViewById<EditText>(R.id.etUser)
        val etPass = findViewById<EditText>(R.id.etPass)

        tvRegistrate.setOnClickListener {
            startActivity(Intent(this, activity_registro::class.java))
        }

        btnEntrar.setOnClickListener {
            val correo = etCorreo.text.toString().trim()
            val pass = etPass.text.toString().trim()

            if (correo.isNotEmpty() && pass.isNotEmpty()) {
                auth.signInWithEmailAndPassword(correo, pass)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            val user = auth.currentUser
                            val sharedPref = getSharedPreferences("SesionUsuario", Context.MODE_PRIVATE)
                            with(sharedPref.edit()) {
                                putBoolean("logueado", true)
                                putString("correo_usuario", user?.email)
                                apply()
                            }
                            irAMain()
                        } else {
                            Toast.makeText(this, "Error: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
            } else {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun irAMain() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}