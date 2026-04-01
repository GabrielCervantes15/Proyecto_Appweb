package mx.itson.edu.proyecto

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class RegaloAdapter(
    private var listaRegalos: MutableList<Regalo>,
    private val onItemDeleted: () -> Unit
) : RecyclerView.Adapter<RegaloAdapter.RegaloViewHolder>() {

    class RegaloViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val img: ImageView? = view.findViewById(R.id.imgProduct)
        val nombre: TextView? = view.findViewById(R.id.txtName)
        val precio: TextView? = view.findViewById(R.id.txtPrice)
        val btnEliminar: ImageButton? = view.findViewById(R.id.btnEliminar)
    }

    fun actualizarLista(nuevaLista: List<Regalo>) {
        this.listaRegalos.clear()
        this.listaRegalos.addAll(nuevaLista)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RegaloViewHolder {
        val layoutRes = if (parent.context is activity_carrito) R.layout.activity_item_carrito else R.layout.activity_item_producto
        val view = LayoutInflater.from(parent.context).inflate(layoutRes, parent, false)
        return RegaloViewHolder(view)
    }

    override fun onBindViewHolder(holder: RegaloViewHolder, position: Int) {
        val regalo = listaRegalos[position]
        val context = holder.itemView.context

        holder.nombre?.text = if (context is activity_carrito && regalo.cantidad > 1) {
            "${regalo.nombre} (x${regalo.cantidad})"
        } else {
            regalo.nombre
        }

        holder.precio?.text = "$${String.format("%.2f", regalo.precio * regalo.cantidad)}"

        holder.img?.let {
            Glide.with(context)
                .load(regalo.imagenUrl)
                .placeholder(android.R.drawable.ic_menu_gallery)
                .error(android.R.drawable.ic_menu_report_image)
                .into(it)
        }

        if (context is activity_carrito) {
            holder.btnEliminar?.visibility = View.VISIBLE
            holder.itemView.setOnClickListener(null)
        } else {
            holder.btnEliminar?.visibility = View.GONE
            holder.itemView.setOnClickListener {
                val intent = Intent(context, activity_detail::class.java).apply {
                    putExtra("objetoRegalo", regalo)
                }
                context.startActivity(intent)
            }
        }

        holder.btnEliminar?.setOnClickListener {
            val currentPos = holder.bindingAdapterPosition
            if (currentPos != RecyclerView.NO_POSITION) {
                Carrito.eliminar(currentPos, context)
                notifyItemRemoved(currentPos)
                notifyItemRangeChanged(currentPos, listaRegalos.size)
                onItemDeleted()
            }
        }
    }

    override fun getItemCount() = listaRegalos.size
}