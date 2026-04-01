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
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

interface LoginService {
    @POST("login")
    fun login(@Body user: UserRequest): Call<LoginResponse>
}

data class UserRequest(val correo: String, val password: String)
data class LoginResponse(val status: String, val user: UserData?, val message: String?)
data class UserData(val nombre: String, val correo: String)

class activity_login : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sharedPref = getSharedPreferences("SesionUsuario", Context.MODE_PRIVATE)
        val estaLogueado = sharedPref.getBoolean("logueado", false)

        if (estaLogueado) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
            return
        }

        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        val mainView = findViewById<android.view.View>(R.id.main)
        if (mainView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(mainView) { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
                insets
            }
        }

        val tvRegistrate = findViewById<TextView>(R.id.tvRegister)
        val btnEntrar = findViewById<Button>(R.id.btnLogin)
        val etCorreo = findViewById<EditText>(R.id.etUser)
        val etPass = findViewById<EditText>(R.id.etPass)

        val retrofit = Retrofit.Builder()
            .baseUrl(Constants.URL_TIENDA)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(LoginService::class.java)

        tvRegistrate.setOnClickListener {
            val intent = Intent(this, activity_registro::class.java)
            startActivity(intent)
        }

        btnEntrar.setOnClickListener {
            val correoInput = etCorreo.text.toString().trim()
            val passInput = etPass.text.toString().trim()

            if (correoInput.isNotEmpty() && passInput.isNotEmpty()) {
                val request = UserRequest(correoInput, passInput)
                service.login(request).enqueue(object : Callback<LoginResponse> {
                    override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                        if (response.isSuccessful && response.body()?.status == "success") {
                            val userData = response.body()?.user
                            val correoServer = userData?.correo ?: ""
                            val nombreServer = userData?.nombre ?: ""

                            val editor = sharedPref.edit()
                            editor.putBoolean("logueado", true)
                            editor.putString("correo_usuario", correoServer)
                            editor.putString("nombre_usuario", nombreServer)
                            editor.apply()

                            val dbHelper = UserDBHelper(this@activity_login)
                            if (dbHelper.obtenerTarjeta(correoServer).isEmpty()) {
                                dbHelper.registrarUsuario(nombreServer, correoServer, "********")
                            }

                            val intent = Intent(this@activity_login, MainActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(this@activity_login, "Correo o contraseña incorrectos", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                        Toast.makeText(this@activity_login, "Error de conexión: ${t.message}", Toast.LENGTH_SHORT).show()
                    }
                })
            } else {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
            }
        }
    }
}