package mx.itson.edu.proyecto

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainActivity : AppCompatActivity() {
    private lateinit var adapter: RegaloAdapter
    private var listaOriginal: List<Regalo> = listOf()

    override fun onStart() {
        super.onStart()
        val user = FirebaseAuth.getInstance().currentUser
        if (user == null) {
            val intent = Intent(this, activity_login::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

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
            configurarFirebase()

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

    private fun configurarFirebase() {
        val database = FirebaseDatabase.getInstance().getReference("productos")
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val listaNueva = mutableListOf<Regalo>()
                for (data in snapshot.children) {
                    val regalo = data.getValue(Regalo::class.java)
                    regalo?.let { listaNueva.add(it) }
                }
                listaOriginal = listaNueva
                adapter.actualizarLista(listaOriginal)
            }
            override fun onCancelled(error: DatabaseError) {
                Log.e("FirebaseError", error.message)
            }
        })
    }

    private fun cerrarSesion() {
        FirebaseAuth.getInstance().signOut()
        val sharedPref = getSharedPreferences("SesionUsuario", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            clear()
            apply()
        }
        val dbHelper = UserDBHelper(this)
        dbHelper.vaciarCarritoDB()
        try {
            dbHelper.writableDatabase.delete("perfil", null, null)
        } catch (e: Exception) {}
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