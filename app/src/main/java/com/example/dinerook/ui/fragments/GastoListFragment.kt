package com.example.dinerook.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.dinerook.R
import com.example.dinerook.data.entity.Gasto
import com.example.dinerook.databinding.FragmentGastoListBinding
import com.example.dinerook.ui.adapters.GastoAdapter
import com.example.dinerook.ui.main.SortToggleListener
import com.example.dinerook.viewmodel.GastoViewModel
import com.google.android.material.snackbar.Snackbar

class GastoListFragment : Fragment(), SortToggleListener {

    private var _binding: FragmentGastoListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: GastoViewModel by viewModels()
    private lateinit var adapter: GastoAdapter

    private var currentGastos: List<Gasto> = emptyList()

    private enum class SortType { DATE, CATEGORY, AMOUNT }
    private var currentSortType = SortType.DATE
    private var isAscending = false

    private var isSortMenuVisible = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGastoListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupSortButtons()
        setupObservers()
        setupListeners()

        currentSortType = SortType.DATE
        isAscending = false
        updateSortUI()

        binding.layoutSort.isVisible = false
        isSortMenuVisible = false
    }

    override fun toggleSortMenu() {
        if (currentGastos.isEmpty()) return

        isSortMenuVisible = !isSortMenuVisible
        binding.layoutSort.isVisible = isSortMenuVisible

        if (!isSortMenuVisible) {
            currentSortType = SortType.DATE
            isAscending = false
            updateSortUI()
            applySorting(scrollToTop = true)
        }
    }

    private fun setupRecyclerView() {
        adapter = GastoAdapter(
            onItemClick = { gasto ->
                navigateToEdit(gasto)
            },
            onDeleteClick = { gasto ->
                showDeleteDialog(gasto)
            }
        )

        binding.rvGastos.adapter = adapter
    }

    private fun setupSortButtons() {
        binding.tvSortDate.setOnClickListener {
            onSortClicked(SortType.DATE)
        }

        binding.tvSortCategory.setOnClickListener {
            onSortClicked(SortType.CATEGORY)
        }

        binding.tvSortAmount.setOnClickListener {
            onSortClicked(SortType.AMOUNT)
        }
    }

    private fun onSortClicked(sortType: SortType) {
        if (currentSortType == sortType) {
            isAscending = !isAscending
        } else {
            currentSortType = sortType
            isAscending = false
        }
        updateSortUI()
        applySorting(scrollToTop = true)
    }

    private fun updateSortUI() {
        val context = requireContext()
        val activeColor = ContextCompat.getColor(context, R.color.primary)
        val inactiveColor = ContextCompat.getColor(context, R.color.text_secondary)

        val sortViews = listOf(
            binding.tvSortDate to SortType.DATE,
            binding.tvSortCategory to SortType.CATEGORY,
            binding.tvSortAmount to SortType.AMOUNT
        )

        sortViews.forEach { (tv, type) ->
            val isActive = currentSortType == type
            val color = if (isActive) activeColor else inactiveColor
            val arrowRes = if (isActive && isAscending) R.drawable.ic_arrow_up else R.drawable.ic_arrow_down

            tv.setTextColor(color)
            tv.setTypeface(null, if (isActive) android.graphics.Typeface.BOLD else android.graphics.Typeface.NORMAL)

            ContextCompat.getDrawable(context, arrowRes)?.mutate()?.let { arrow ->
                arrow.setTint(color)
                tv.setCompoundDrawablesWithIntrinsicBounds(null, null, arrow, null)
            }
        }
    }

    private fun applySorting(scrollToTop: Boolean = false) {
        val sortedList = when (currentSortType) {
            SortType.DATE -> {
                if (isAscending) currentGastos.sortedBy { it.fecha }
                else currentGastos.sortedByDescending { it.fecha }
            }
            SortType.CATEGORY -> {
                if (isAscending) currentGastos.sortedBy { it.categoria }
                else currentGastos.sortedByDescending { it.categoria }
            }
            SortType.AMOUNT -> {
                if (isAscending) currentGastos.sortedBy { it.cantidad }
                else currentGastos.sortedByDescending { it.cantidad }
            }
        }

        adapter.submitList(sortedList) {
            if (scrollToTop && sortedList.isNotEmpty()) {
                binding.rvGastos.scrollToPosition(0)
            }
        }
    }

    private fun setupObservers() {
        viewModel.allGastos.observe(viewLifecycleOwner) { gastos ->
            binding.progressBar.isVisible = false

            currentGastos = gastos

            binding.layoutEmpty.isVisible = gastos.isEmpty()
            binding.rvGastos.isVisible = gastos.isNotEmpty()

            if (gastos.isEmpty()) {
                binding.layoutSort.isVisible = false
                isSortMenuVisible = false
            }

            if (gastos.isNotEmpty()) {
                applySorting(scrollToTop = false)
            }
        }
    }

    private fun setupListeners() {
        binding.fabAdd.setOnClickListener { navigateToAdd() }
    }

    private fun navigateToAdd() {
        findNavController().navigate(
            R.id.action_gastoList_to_addEdit
        )
    }

    private fun navigateToEdit(gasto: Gasto) {
        val bundle = Bundle().apply {
            putInt("gastoId", gasto.id)
        }
        findNavController().navigate(
            R.id.action_gastoList_to_addEdit,
            bundle
        )
    }

    private fun showDeleteDialog(gasto: Gasto) {
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.gasto_delete))
            .setMessage(getString(R.string.gasto_delete_confirm))
            .setPositiveButton(R.string.btn_yes) { _, _ ->
                deleteGasto(gasto)
            }
            .setNegativeButton(R.string.btn_no, null)
            .show()
    }

    private fun deleteGasto(gasto: Gasto) {
        viewModel.deleteGasto(gasto)

        Snackbar.make(
            binding.root,
            getString(R.string.gasto_delete_success),
            Snackbar.LENGTH_LONG
        )
        .setBackgroundTint(requireContext().getColor(R.color.error))
        .setTextColor(requireContext().getColor(R.color.white))
        .setAction("Deshacer") {
            viewModel.insertGasto(gasto)
            Snackbar.make(
                binding.root,
                getString(R.string.gasto_restored),
                Snackbar.LENGTH_SHORT
            )
            .setBackgroundTint(requireContext().getColor(R.color.success))
            .setTextColor(requireContext().getColor(R.color.white))
            .show()
        }
        .setActionTextColor(requireContext().getColor(R.color.white))
        .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
