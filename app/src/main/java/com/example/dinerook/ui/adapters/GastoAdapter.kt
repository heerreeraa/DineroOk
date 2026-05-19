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
import java.util.Locale

/**
 * Adapter para mostrar la lista de gastos en RecyclerView
 */
class GastoAdapter(
    private val onItemClick: (Gasto) -> Unit,
    private val onDeleteClick: (Gasto) -> Unit
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

                // Cantidad formateada en euros
                val amount = String.format(Locale.getDefault(), "%.2f €", gasto.cantidad)
                tvCantidad.text = amount

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

                // Click en la card -> editar
                root.setOnClickListener {
                    onItemClick(gasto)
                }

                // Click en botón eliminar
                btnDelete.setOnClickListener {
                    onDeleteClick(gasto)
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

