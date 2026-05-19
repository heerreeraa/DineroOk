package com.example.dinerook.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.dinerook.data.entity.CategoryStat
import com.example.dinerook.databinding.ItemCategoryStatBinding
import com.example.dinerook.utils.CategoryHelper
import java.util.Locale

/**
 * Adapter para mostrar estadísticas por categoría (usa ListAdapter para eficiencia)
 */
class CategoryStatAdapter(
    stats: List<CategoryStat> = emptyList()
) : ListAdapter<CategoryStat, CategoryStatAdapter.StatViewHolder>(CategoryStatDiffCallback()) {

    init {
        submitList(stats)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StatViewHolder {
        val binding = ItemCategoryStatBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return StatViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StatViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class StatViewHolder(
        private val binding: ItemCategoryStatBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(stat: CategoryStat) {
            binding.apply {
                tvCategoryName.text = stat.categoria
                tvCategoryCount.text = "${stat.count} gastos (${stat.percentage}%)"
                tvCategoryAmount.text = String.format(Locale.getDefault(), "%.2f €", stat.total)

                val iconRes = CategoryHelper.getCategoryIcon(stat.categoria)
                ivCategoryIcon.setImageResource(iconRes)

                val colorRes = CategoryHelper.getCategoryColor(stat.categoria)
                ivCategoryIcon.setColorFilter(ContextCompat.getColor(root.context, colorRes))
            }
        }
    }

    class CategoryStatDiffCallback : DiffUtil.ItemCallback<CategoryStat>() {
        override fun areItemsTheSame(oldItem: CategoryStat, newItem: CategoryStat) =
            oldItem.categoria == newItem.categoria

        override fun areContentsTheSame(oldItem: CategoryStat, newItem: CategoryStat) =
            oldItem == newItem
    }
}

