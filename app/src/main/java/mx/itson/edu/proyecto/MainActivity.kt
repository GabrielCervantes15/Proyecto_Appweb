package mx.itson.edu.proyecto

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface ProductoService {
    @GET("productos")
    fun obtenerProductos(): Call<List<Regalo>>
}

class MainActivity : AppCompatActivity() {
    private lateinit var adapter: RegaloAdapter
    private var listaOriginal: List<Regalo> = listOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            enableEdgeToEdge()
            setContentView(R.layout.activity_main)

            val mainView = findViewById<android.view.View>(R.id.main)
            mainView?.let {
                ViewCompat.setOnApplyWindowInsetsListener(it) { v, insets ->
                    val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                    v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
                    insets
                }
            }

            Carrito.cargarDesdeDB(this)

            val rvProducts = findViewById<RecyclerView>(R.id.rvProducts)
            adapter = RegaloAdapter(mutableListOf()) {}
            rvProducts.layoutManager = GridLayoutManager(this, 2)
            rvProducts.adapter = adapter

            val etSearch = findViewById<EditText>(R.id.searchFilter)
            val fabCart = findViewById<FloatingActionButton>(R.id.fabCart)
            val btnLogout = findViewById<ImageButton>(R.id.btnLogout)

            setupCategoryButtons()

            val retrofit = Retrofit.Builder()
                .baseUrl(Constants.URL_TIENDA)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            val service = retrofit.create(ProductoService::class.java)

            service.obtenerProductos().enqueue(object : Callback<List<Regalo>> {
                override fun onResponse(call: Call<List<Regalo>>, response: Response<List<Regalo>>) {
                    if (response.isSuccessful) {
                        listaOriginal = response.body() ?: listOf()
                        adapter.actualizarLista(listaOriginal)
                    }
                }
                override fun onFailure(call: Call<List<Regalo>>, t: Throwable) {
                    Toast.makeText(this@MainActivity, "Sin conexión", Toast.LENGTH_SHORT).show()
                }
            })

            fabCart?.setOnClickListener {
                startActivity(Intent(this, activity_carrito::class.java))
            }

            btnLogout?.setOnClickListener {
                cerrarSesion()
            }

            etSearch?.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    filtrar(s.toString())
                }
                override fun afterTextChanged(s: Editable?) {}
            })

        } catch (e: Exception) {
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun cerrarSesion() {
        val sharedPref = getSharedPreferences("SesionUsuario", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            clear()
            apply()
        }

        val dbHelper = UserDBHelper(this)
        dbHelper.vaciarCarritoDB()

        try {
            dbHelper.writableDatabase.delete("perfil", null, null)
        } catch (e: Exception) { }

        Carrito.productosSeleccionados.clear()

        val intent = Intent(this, activity_login::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun setupCategoryButtons() {
        val categorias = mapOf(
            R.id.btnGlobos to "Globos",
            R.id.btnTazas to "Tazas",
            R.id.btnPeluches to "Peluches",
            R.id.btnJuegos to "Juegos",
            R.id.btnFlores to "Flores"
        )

        categorias.forEach { (id, nombre) ->
            findViewById<Button>(id)?.setOnClickListener {
                adapter.actualizarLista(listaOriginal.filter { it.categoria == nombre })
            }
        }

        findViewById<Button>(R.id.btnTodos)?.setOnClickListener {
            adapter.actualizarLista(listaOriginal)
            findViewById<EditText>(R.id.searchFilter)?.setText("")
        }
    }

    private fun filtrar(texto: String) {
        val listaFiltrada = listaOriginal.filter {
            it.nombre.lowercase().contains(texto.lowercase())
        }
        adapter.actualizarLista(listaFiltrada)
    }
}