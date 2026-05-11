package com.example.dinerook.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.dinerook.data.entity.Gasto
import com.example.dinerook.databinding.ItemGastoBinding
import com.example.dinerook.utils.CategoryHelper
import java.text.NumberFormat
import java.util.Locale

/**
 * Adapter para mostrar la lista de gastos en RecyclerView
 */
class GastoAdapter(
    private val onItemClick: (Gasto) -> Unit,
    private val onItemLongClick: (Gasto) -> Unit
) : ListAdapter<Gasto, GastoAdapter.GastoViewHolder>(GastoDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GastoViewHolder {
        val binding = ItemGastoBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return GastoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GastoViewHolder, position: Int) {
        val gasto = getItem(position)
        holder.bind(gasto)
    }

    inner class GastoViewHolder(
        private val binding: ItemGastoBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(gasto: Gasto) {
            binding.apply {
                // Nombre
                tvNombre.text = gasto.nombre

                // Cantidad formateada
                val format = NumberFormat.getCurrencyInstance(Locale("es", "MX"))
                tvCantidad.text = format.format(gasto.cantidad)

                // Categoría
                tvCategoria.text = gasto.categoria

                // Fecha
                tvFecha.text = gasto.fecha

                // Icono de categoría
                val iconRes = CategoryHelper.getCategoryIcon(gasto.categoria)
                ivCategoryIcon.setImageResource(iconRes)

                // Color del icono según categoría
                val colorRes = CategoryHelper.getCategoryColor(gasto.categoria)
                val color = ContextCompat.getColor(root.context, colorRes)
                ivCategoryIcon.setColorFilter(color)

                // Click listeners
                root.setOnClickListener {
                    onItemClick(gasto)
                }

                root.setOnLongClickListener {
                    onItemLongClick(gasto)
                    true
                }
            }
        }
    }

    /**
     * DiffUtil para optimizar actualizaciones del RecyclerView
     */
    class GastoDiffCallback : DiffUtil.ItemCallback<Gasto>() {
        override fun areItemsTheSame(oldItem: Gasto, newItem: Gasto): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Gasto, newItem: Gasto): Boolean {
            return oldItem == newItem
        }
    }
}

