package mx.itson.edu.proyecto

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class RegaloAdapter(
    private var listaRegalos: MutableList<Regalo>,
    private val onItemDeleted: () -> Unit
) : RecyclerView.Adapter<RegaloAdapter.RegaloViewHolder>() {

    class RegaloViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val img: ImageView = view.findViewById(R.id.imgProduct)
        val nombre: TextView = view.findViewById(R.id.txtName)
        val precio: TextView = view.findViewById(R.id.txtPrice)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RegaloViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.activity_item_producto, parent, false)
        return RegaloViewHolder(view)
    }

    fun actualizarLista(nuevaLista: List<Regalo>) {
        this.listaRegalos = nuevaLista.toMutableList()
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: RegaloViewHolder, position: Int) {
        val regalo = listaRegalos[position]
        val context = holder.itemView.context

        holder.nombre.text = regalo.nombre
        holder.precio.text = "$${regalo.precio}"
        holder.img.setImageResource(regalo.imagenRes)

        val btnEliminar = holder.itemView.findViewById<ImageButton>(R.id.btnEliminar)

        if (context is activity_carrito) {
            btnEliminar?.visibility = View.VISIBLE
        } else {
            btnEliminar?.visibility = View.GONE

            // El clic al detalle solo funciona en la MainActivity (Catálogo)
            holder.itemView.setOnClickListener {
                val intent = Intent(context, activity_detail::class.java).apply {
                    putExtra("objetoRegalo", regalo)
                }
                context.startActivity(intent)
            }
        }

        btnEliminar?.setOnClickListener {
            // Importante: Eliminar también del objeto Global Carrito
            if (context is activity_carrito) {
                Carrito.productosSeleccionados.removeAt(position)
            }
            listaRegalos.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, listaRegalos.size)
            onItemDeleted()
        }
    }

    override fun getItemCount() = listaRegalos.size
}