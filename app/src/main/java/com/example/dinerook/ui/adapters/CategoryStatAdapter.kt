package com.example.dinerook.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.dinerook.data.entity.CategoryStat
import com.example.dinerook.databinding.ItemCategoryStatBinding
import com.example.dinerook.utils.CategoryHelper
import java.text.NumberFormat
import java.util.Locale

/**
 * Adapter para mostrar estadísticas por categoría
 */
class CategoryStatAdapter(
    private val stats: List<CategoryStat>
) : RecyclerView.Adapter<CategoryStatAdapter.StatViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StatViewHolder {
        val binding = ItemCategoryStatBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return StatViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StatViewHolder, position: Int) {
        holder.bind(stats[position])
    }

    override fun getItemCount() = stats.size

    class StatViewHolder(
        private val binding: ItemCategoryStatBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(stat: CategoryStat) {
            binding.apply {
                // Nombre de categoría
                tvCategoryName.text = stat.categoria

                // Contador
                tvCategoryCount.text = "${stat.count} gastos (${stat.percentage}%)"

                // Monto formateado en euros
                val amount = String.format(java.util.Locale.getDefault(), "%.2f €", stat.total)
                tvCategoryAmount.text = amount

                // Icono y color de categoría
                val iconRes = CategoryHelper.getCategoryIcon(stat.categoria)
                ivCategoryIcon.setImageResource(iconRes)

                val colorRes = CategoryHelper.getCategoryColor(stat.categoria)
                val color = ContextCompat.getColor(root.context, colorRes)
                ivCategoryIcon.setColorFilter(color)
            }
        }
    }
}

