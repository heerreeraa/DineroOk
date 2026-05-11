package com.example.dinerook.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.dinerook.R
import com.example.dinerook.data.entity.CategoryStat
import com.example.dinerook.data.entity.Gasto
import com.example.dinerook.databinding.FragmentStatsBinding
import com.example.dinerook.ui.adapters.CategoryStatAdapter
import com.example.dinerook.viewmodel.GastoViewModel
import java.text.NumberFormat
import java.util.Locale

/**
 * Fragment que muestra estadísticas de gastos
 */
class StatsFragment : Fragment() {

    private var _binding: FragmentStatsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: GastoViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStatsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupObservers()
    }

    /**
     * Observa cambios en los datos
     */
    private fun setupObservers() {
        // Observar total gastado
        viewModel.totalGastado.observe(viewLifecycleOwner) { total ->
            val format = NumberFormat.getCurrencyInstance(Locale("es", "MX"))
            binding.tvTotalAmount.text = format.format(total ?: 0.0)
        }

        // Observar cantidad de gastos
        viewModel.gastosCount.observe(viewLifecycleOwner) { count ->
            binding.tvTotalCount.text = getString(R.string.stats_count, count)
        }

        // Observar todos los gastos para calcular estadísticas por categoría
        viewModel.allGastos.observe(viewLifecycleOwner) { gastos ->
            if (gastos.isEmpty()) {
                showEmptyState()
            } else {
                showStats(gastos)
            }
        }
    }

    /**
     * Muestra el estado vacío
     */
    private fun showEmptyState() {
        binding.layoutEmpty.isVisible = true
        binding.rvCategoryStats.isVisible = false
    }

    /**
     * Calcula y muestra las estadísticas
     */
    private fun showStats(gastos: List<Gasto>) {
        binding.layoutEmpty.isVisible = false
        binding.rvCategoryStats.isVisible = true

        val categoryStats = calculateCategoryStats(gastos)
        val adapter = CategoryStatAdapter(categoryStats)
        binding.rvCategoryStats.adapter = adapter
    }

    /**
     * Calcula estadísticas por categoría
     */
    private fun calculateCategoryStats(gastos: List<Gasto>): List<CategoryStat> {
        // Agrupar por categoría
        val groupedByCategory = gastos.groupBy { it.categoria }

        // Calcular total general
        val totalGeneral = gastos.sumOf { it.cantidad }

        // Crear lista de estadísticas
        val stats = mutableListOf<CategoryStat>()

        groupedByCategory.forEach { (categoria, gastosCategoria) ->
            val total = gastosCategoria.sumOf { it.cantidad }
            val count = gastosCategoria.size
            val percentage = if (totalGeneral > 0) {
                ((total / totalGeneral) * 100).toInt()
            } else {
                0
            }

            stats.add(CategoryStat(categoria, total, count, percentage))
        }

        // Ordenar por total descendente
        return stats.sortedByDescending { it.total }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

