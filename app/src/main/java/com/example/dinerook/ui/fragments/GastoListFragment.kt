package com.example.dinerook.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.dinerook.R
import com.example.dinerook.data.entity.Gasto
import com.example.dinerook.databinding.FragmentGastoListBinding
import com.example.dinerook.ui.adapters.GastoAdapter
import com.example.dinerook.viewmodel.GastoViewModel
import com.google.android.material.snackbar.Snackbar

/**
 * Fragment que muestra la lista de gastos
 */
class GastoListFragment : Fragment() {

    private var _binding: FragmentGastoListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: GastoViewModel by viewModels()
    private lateinit var adapter: GastoAdapter

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
        setupObservers()
        setupListeners()
    }

    /**
     * Configura el RecyclerView con el adapter
     */
    private fun setupRecyclerView() {
        adapter = GastoAdapter(
            onItemClick = { gasto ->
                // Click normal: editar gasto
                navigateToEdit(gasto)
            },
            onItemLongClick = { gasto ->
                // Long click: confirmar eliminación
                showDeleteDialog(gasto)
            }
        )

        binding.rvGastos.adapter = adapter
    }

    /**
     * Observa cambios en el ViewModel
     */
    private fun setupObservers() {
        viewModel.allGastos.observe(viewLifecycleOwner) { gastos ->
            adapter.submitList(gastos)

            // Mostrar/ocultar mensaje de lista vacía
            binding.layoutEmpty.isVisible = gastos.isEmpty()
            binding.rvGastos.isVisible = gastos.isNotEmpty()
        }
    }

    /**
     * Configura los listeners de la UI
     */
    private fun setupListeners() {
        binding.fabAdd.setOnClickListener {
            navigateToAdd()
        }
    }

    /**
     * Navega al fragment para agregar un nuevo gasto
     */
    private fun navigateToAdd() {
        findNavController().navigate(
            R.id.action_gastoList_to_addEdit
        )
    }

    /**
     * Navega al fragment para editar un gasto
     */
    private fun navigateToEdit(gasto: Gasto) {
        val bundle = Bundle().apply {
            putInt("gastoId", gasto.id)
        }
        findNavController().navigate(
            R.id.action_gastoList_to_addEdit,
            bundle
        )
    }

    /**
     * Muestra diálogo de confirmación para eliminar
     */
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

    /**
     * Elimina un gasto y muestra mensaje
     */
    private fun deleteGasto(gasto: Gasto) {
        viewModel.deleteGasto(gasto)
        Snackbar.make(
            binding.root,
            getString(R.string.gasto_delete_success),
            Snackbar.LENGTH_SHORT
        ).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

