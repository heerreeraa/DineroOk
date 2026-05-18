package com.example.dinerook.ui.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.dinerook.R
import com.example.dinerook.data.entity.CategoryStat
import com.example.dinerook.data.entity.Gasto
import com.example.dinerook.databinding.FragmentStatsBinding
import com.example.dinerook.ui.adapters.CategoryStatAdapter
import com.example.dinerook.utils.CategoryHelper
import com.example.dinerook.viewmodel.GastoViewModel
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import java.util.Locale

/**
 * Fragment que muestra estadísticas de gastos con gráfico circular
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

        setupPieChart()
        setupObservers()
    }

    /**
     * Configura el aspecto inicial del PieChart
     */
    private fun setupPieChart() {
        binding.pieChart.apply {
            setUsePercentValues(true)
            description.isEnabled = false
            setExtraOffsets(5f, 10f, 5f, 5f)

            // Configurar el agujero central
            isDrawHoleEnabled = true
            setHoleColor(Color.TRANSPARENT)
            holeRadius = 55f
            transparentCircleRadius = 60f

            // Deshabilitar etiquetas en el centro
            setDrawCenterText(false)
            setDrawEntryLabels(false)

            // Configurar leyenda multilinea
            legend.isEnabled = true
            legend.textSize = 11f
            legend.textColor = ContextCompat.getColor(requireContext(), R.color.text_primary)
            legend.isWordWrapEnabled = true
            legend.horizontalAlignment = com.github.mikephil.charting.components.Legend.LegendHorizontalAlignment.CENTER
            legend.verticalAlignment = com.github.mikephil.charting.components.Legend.LegendVerticalAlignment.BOTTOM
            legend.orientation = com.github.mikephil.charting.components.Legend.LegendOrientation.HORIZONTAL
            legend.setDrawInside(false)
            legend.xEntrySpace = 10f
            legend.yEntrySpace = 5f

            // Configurar rotación
            rotationAngle = 0f
            isRotationEnabled = true
            isHighlightPerTapEnabled = false  // Deshabilitar highlight al pulsar

            // Animación
            animateY(1000, Easing.EaseInOutQuad)
        }
    }

    /**
     * Observa cambios en los datos
     */
    private fun setupObservers() {
        // Observar total gastado
        viewModel.totalGastado.observe(viewLifecycleOwner) { total ->
            val amount = String.format(Locale.getDefault(), "%.2f €", total ?: 0.0)
            binding.tvTotalAmount.text = amount
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
        binding.pieChart.isVisible = false
        binding.tvTotalLabel.isVisible = false
        binding.tvTotalAmount.isVisible = false
        binding.tvTotalCount.isVisible = false
        binding.tvCategoriesTitle.isVisible = false
    }

    /**
     * Calcula y muestra las estadísticas
     */
    private fun showStats(gastos: List<Gasto>) {
        binding.layoutEmpty.isVisible = false
        binding.rvCategoryStats.isVisible = true
        binding.pieChart.isVisible = true
        binding.tvTotalLabel.isVisible = true
        binding.tvTotalAmount.isVisible = true
        binding.tvTotalCount.isVisible = true
        binding.tvCategoriesTitle.isVisible = true

        val categoryStats = calculateCategoryStats(gastos)

        // Actualizar RecyclerView
        val adapter = CategoryStatAdapter(categoryStats)
        binding.rvCategoryStats.adapter = adapter

        // Actualizar PieChart
        updatePieChart(categoryStats)
    }

    /**
     * Actualiza el gráfico circular con los datos
     */
    private fun updatePieChart(stats: List<CategoryStat>) {
        val entries = ArrayList<PieEntry>()
        val colors = ArrayList<Int>()

        stats.forEach { stat ->
            // Añadir nombre de categoría con porcentaje para la leyenda
            entries.add(PieEntry(stat.percentage.toFloat(), "${stat.categoria} (${stat.percentage}%)"))

            // Obtener color de la categoría
            val colorRes = CategoryHelper.getCategoryColor(stat.categoria)
            colors.add(ContextCompat.getColor(requireContext(), colorRes))
        }

        val dataSet = PieDataSet(entries, "").apply {
            this.colors = colors
            sliceSpace = 3f
            selectionShift = 5f

            // Ocultar valores dentro del gráfico
            setDrawValues(false)
        }

        val data = PieData(dataSet)

        binding.pieChart.data = data
        binding.pieChart.invalidate()
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

