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
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import java.util.Locale

/**
 * Fragment que muestra estadísticas de gastos con gráfico circular
 */
class StatsFragment : Fragment() {

    private var _binding: FragmentStatsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: GastoViewModel by viewModels()
    private lateinit var categoryAdapter: CategoryStatAdapter

    // Lista completa de estadísticas para filtrar
    private var allCategoryStats: List<CategoryStat> = emptyList()

    // Categoría actualmente seleccionada (null = todas)
    private var selectedCategory: String? = null

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

        setupRecyclerView()
        setupPieChart()
        setupObservers()
    }

    /**
     * Configura el RecyclerView con el adapter
     */
    private fun setupRecyclerView() {
        categoryAdapter = CategoryStatAdapter(emptyList())
        binding.rvCategoryStats.adapter = categoryAdapter
    }

    /**
     * Configura el aspecto inicial del PieChart
     */
    private fun setupPieChart() {
        binding.pieChart.apply {
            setUsePercentValues(true)
            description.isEnabled = false
            // Menos padding interno para evitar hueco arriba/abajo
            setExtraOffsets(0f, 0f, 0f, 0f)

            // Configurar el agujero central (más pequeño para que el gráfico se vea más grande)
            isDrawHoleEnabled = true
            setHoleColor(Color.TRANSPARENT)
            holeRadius = 45f
            transparentCircleRadius = 50f

            setDrawCenterText(false)
            setDrawEntryLabels(false)

            // Leyenda a la izquierda ocupando menos alto (menos espacios)
            legend.isEnabled = true
            legend.textSize = 13f
            legend.textColor = ContextCompat.getColor(requireContext(), R.color.text_primary)
            legend.isWordWrapEnabled = true
            legend.horizontalAlignment = com.github.mikephil.charting.components.Legend.LegendHorizontalAlignment.LEFT
            legend.verticalAlignment = com.github.mikephil.charting.components.Legend.LegendVerticalAlignment.CENTER
            legend.orientation = com.github.mikephil.charting.components.Legend.LegendOrientation.VERTICAL
            legend.setDrawInside(false)
            legend.xEntrySpace = 8f
            legend.yEntrySpace = 4f
            legend.formSize = 12f
            legend.formToTextSpace = 8f
            legend.maxSizePercent = 0.45f

            rotationAngle = 0f
            isRotationEnabled = true
            isHighlightPerTapEnabled = true

            animateY(1000, Easing.EaseInOutQuad)

            // Listener para detectar clicks en el gráfico
            setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
                override fun onValueSelected(e: Entry?, h: Highlight?) {
                    if (e is PieEntry) {
                        // Extraer nombre de categoría (quitar el porcentaje)
                        val label = e.label
                        val categoryName = label.substringBefore(" (")

                        if (selectedCategory == categoryName) {
                            // Si ya está seleccionada, deseleccionar y mostrar todas
                            selectedCategory = null
                            binding.pieChart.highlightValue(null)
                            updateCategoryList(allCategoryStats)
                        } else {
                            // Seleccionar esta categoría y filtrar
                            selectedCategory = categoryName
                            val filtered = allCategoryStats.filter { it.categoria == categoryName }
                            updateCategoryList(filtered)
                        }
                    }
                }

                override fun onNothingSelected() {
                    // Mostrar todas las categorías
                    selectedCategory = null
                    updateCategoryList(allCategoryStats)
                }
            })
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
            binding.tvTotalCount.text = count.toString()
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
        binding.layoutSummary.isVisible = false
        binding.tvCategoriesTitle.isVisible = false
    }

    /**
     * Calcula y muestra las estadísticas
     */
    private fun showStats(gastos: List<Gasto>) {
        binding.layoutEmpty.isVisible = false
        binding.rvCategoryStats.isVisible = true
        binding.pieChart.isVisible = true
        binding.layoutSummary.isVisible = true
        binding.tvCategoriesTitle.isVisible = true

        allCategoryStats = calculateCategoryStats(gastos)
        selectedCategory = null

        // Actualizar RecyclerView
        updateCategoryList(allCategoryStats)

        // Actualizar PieChart
        updatePieChart(allCategoryStats)
    }

    /**
     * Actualiza la lista de categorías en el RecyclerView
     */
    private fun updateCategoryList(stats: List<CategoryStat>) {
        categoryAdapter.submitList(stats)
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
