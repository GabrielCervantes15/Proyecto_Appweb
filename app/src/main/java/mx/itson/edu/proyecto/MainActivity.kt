package mx.itson.edu.proyecto

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {
    lateinit var adapter: RegaloAdapter
    lateinit var listaOriginal: List<Regalo>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val btnGlobos = findViewById<Button>(R.id.btnGlobos)
        val btnTazas = findViewById<Button>(R.id.btnTazas)
        val btnPeluches = findViewById<Button>(R.id.btnPeluches)
        val btnTodo = findViewById<Button>(R.id.btnTodos)
        val rvProducts = findViewById<RecyclerView>(R.id.rvProducts)
        val etSearch = findViewById<EditText>(R.id.searchFilter)
        val fabCart = findViewById<com.google.android.material.floatingactionbutton.FloatingActionButton>(R.id.fabCart)
        val btnJuegos = findViewById<Button>(R.id.btnJuegos)
        val btnFlores = findViewById<Button>(R.id.btnFlores)



        listaOriginal = listOf(
            // PELUCHES
            Regalo("Oso Gigante", 450.0, "Peluche suave de 1m con listón.", "Peluches", R.drawable.peluche_gigante),
            Regalo("Perrito Puppy", 280.0, "Peluche de perrito con orejas largas y textura extra suave.", "Peluches", R.drawable.puppy),
            Regalo("Unicornio Mágico", 320.0, "Unicornio de felpa con cuerno brillante de colores.", "Peluches", R.drawable.unicornio),
            Regalo("León de la Selva", 350.0, "Peluche de león con melena frondosa de 40cm.", "Peluches", R.drawable.leon),

            // GLOBOS
            Regalo("Globo Metálico", 120.0, "Globo de helio 'Happy Birthday'.", "Globos", R.drawable.globo_happy_birthday),
            Regalo("Globo 'Te Amo'", 130.0, "Globo metálico rojo en forma de corazón con helio.", "Globos", R.drawable.globo_teamo),
            Regalo("Arreglo Graduación", 380.0, "Set de globos negros y dorados con forma de birrete.", "Globos", R.drawable.globo_graduacion),
            Regalo("Globo Gigante #1", 160.0, "Globo de número gigante para aniversarios.", "Globos", R.drawable.globo_gigante),

            // TAZAS
            Regalo("Taza con Dulces", 180.0, "Taza rellena de chocolates variados.", "Tazas", R.drawable.taza_personalizada),
            Regalo("Taza 'Súper Mamá'", 190.0, "Taza de cerámica con diseño especial y dulces.", "Tazas", R.drawable.taza_super),
            Regalo("Set Tazas Pareja", 340.0, "Dúo de tazas que encajan, ideales para San Valentín.", "Tazas", R.drawable.taza_pareja),
            Regalo("Taza Gamer", 210.0, "Taza con asa en forma de control de videojuegos.", "Tazas", R.drawable.taza_gamer),

            // JUEGOS
            Regalo("Jenga Madera", 300.0, "Clásico juego de torre de madera.", "Juegos", R.drawable.jenga),
            Regalo("Rompecabezas 500", 290.0, "Rompecabezas con paisaje artístico de alta calidad.", "Juegos", R.drawable.rompecabezas),
            Regalo("Ajedrez de Madera", 450.0, "Tablero plegable con piezas talladas a mano.", "Juegos", R.drawable.ajedrez),

            // FLORES
            Regalo("Ramo 12 Rosas", 400.0, "Doce rosas rojas frescas recién cortadas.", "Flores", R.drawable.ramo),
            Regalo("Caja de Girasoles", 420.0, "Caja decorativa con 5 girasoles y follaje verde.", "Flores", R.drawable.girasoles),
            Regalo("Orquídea Blanca", 550.0, "Elegante orquídea en maceta de cerámica blanca.", "Flores", R.drawable.orquidea)

        )

        adapter = RegaloAdapter(listaOriginal.toMutableList()) {}
        rvProducts.layoutManager = GridLayoutManager(this, 2)
        rvProducts.adapter = adapter
        btnGlobos.setOnClickListener {
            adapter.actualizarLista(listaOriginal.filter { it.categoria == "Globos" })
        }

        btnTazas.setOnClickListener {
            adapter.actualizarLista(listaOriginal.filter { it.categoria == "Tazas" })
        }

        btnPeluches.setOnClickListener {
            adapter.actualizarLista(listaOriginal.filter { it.categoria == "Peluches" })
        }

        btnTodo.setOnClickListener {
            adapter.actualizarLista(listaOriginal)
            etSearch.setText("")
        }
        btnJuegos.setOnClickListener {
            adapter.actualizarLista(listaOriginal.filter { it.categoria == "Juegos" })
        }

        btnFlores.setOnClickListener {
            adapter.actualizarLista(listaOriginal.filter { it.categoria == "Flores" })
        }

        fabCart.setOnClickListener {
            val intent = Intent(this, activity_carrito::class.java)
            startActivity(intent)
        }

        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filtrar(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun filtrar(texto: String) {
        val listaFiltrada = listaOriginal.filter {
            it.nombre.lowercase().contains(texto.lowercase())
        }
        adapter.actualizarLista(listaFiltrada)
    }
}