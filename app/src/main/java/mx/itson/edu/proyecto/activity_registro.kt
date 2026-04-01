package mx.itson.edu.proyecto

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

interface RegistroService {
    @POST("registro")
    fun registrar(@Body user: RegistroRequest): Call<RegistroResponse>
}

data class RegistroRequest(val nombre: String, val correo: String, val password: String)
data class RegistroResponse(val status: String, val message: String?)

class activity_registro : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)

        val etNombre = findViewById<EditText>(R.id.etNombreRegistro)
        val etCorreo = findViewById<EditText>(R.id.etEmailRegistro)
        val etPass = findViewById<EditText>(R.id.etPassRegistro)
        val btnRegistrar = findViewById<Button>(R.id.btnFinalizarRegistro)
        val tvVolver = findViewById<TextView>(R.id.tvVolverLogin)

        val retrofit = Retrofit.Builder()
            .baseUrl(Constants.URL_TIENDA)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(RegistroService::class.java)

        btnRegistrar.setOnClickListener {
            val nombre = etNombre.text.toString().trim()
            val correo = etCorreo.text.toString().trim()
            val pass = etPass.text.toString().trim()

            if (nombre.isNotEmpty() && correo.isNotEmpty() && pass.isNotEmpty()) {
                val request = RegistroRequest(nombre, correo, pass)

                service.registrar(request).enqueue(object : Callback<RegistroResponse> {
                    override fun onResponse(call: Call<RegistroResponse>, response: Response<RegistroResponse>) {
                        if (response.isSuccessful && response.body()?.status == "success") {
                            val sharedPref = getSharedPreferences("SesionUsuario", Context.MODE_PRIVATE)
                            with(sharedPref.edit()) {
                                putBoolean("logueado", true)
                                putString("correo_usuario", correo)
                                putString("nombre_usuario", nombre)
                                apply()
                            }

                            Toast.makeText(this@activity_registro, "¡Cuenta creada con éxito!", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this@activity_registro, MainActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                            finish()
                        } else {
                            val errorMsg = response.body()?.message ?: "Error al registrar"
                            Toast.makeText(this@activity_registro, errorMsg, Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<RegistroResponse>, t: Throwable) {
                        Toast.makeText(this@activity_registro, "Error de conexión: ${t.message}", Toast.LENGTH_SHORT).show()
                    }
                })
            } else {
                Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
            }
        }

        tvVolver.setOnClickListener {
            finish()
        }
    }
}