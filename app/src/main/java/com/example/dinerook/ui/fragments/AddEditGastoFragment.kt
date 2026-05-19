package com.example.dinerook.ui.fragments

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.dinerook.R
import com.example.dinerook.data.entity.Gasto
import com.example.dinerook.databinding.FragmentAddEditGastoBinding
import com.example.dinerook.utils.CategoryHelper
import com.example.dinerook.utils.Validator
import com.example.dinerook.viewmodel.GastoViewModel
import com.google.android.material.snackbar.Snackbar
import java.text.SimpleDateFormat
import java.util.*

/**
 * Fragment para agregar o editar un gasto
 */
class AddEditGastoFragment : Fragment() {

    private var _binding: FragmentAddEditGastoBinding? = null
    private val binding get() = _binding!!

    private val viewModel: GastoViewModel by viewModels()

    private var gastoId: Int = -1
    private var isEditMode = false
    private val calendar = Calendar.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddEditGastoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Obtener argumentos
        gastoId = arguments?.getInt("gastoId", -1) ?: -1
        isEditMode = gastoId != -1

        // Título dinámico en la Toolbar
        val titleRes = if (isEditMode) R.string.gasto_edit_title else R.string.gasto_add_title
        requireActivity().title = getString(titleRes)
        (activity as? androidx.appcompat.app.AppCompatActivity)?.supportActionBar?.title = getString(titleRes)

        setupCategorySpinner()
        setupDatePicker()
        setupListeners()

        if (isEditMode) {
            loadGasto()
        } else {
            // Establecer fecha actual por defecto
            updateFechaField()
        }
    }

    /**
     * Configura el Spinner de categorías
     */
    private fun setupCategorySpinner() {
        val categories = CategoryHelper.getCategories()
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            categories
        )
        binding.spinnerCategoria.setAdapter(adapter)

        // Seleccionar primera categoría por defecto
        if (!isEditMode && categories.isNotEmpty()) {
            binding.spinnerCategoria.setText(categories[0], false)
        }
    }

    /**
     * Configura el DatePicker para seleccionar fecha
     */
    private fun setupDatePicker() {
        binding.etFecha.setOnClickListener {
            showDatePicker()
        }

        binding.tilFecha.setEndIconOnClickListener {
            showDatePicker()
        }
    }

    /**
     * Muestra el diálogo de selección de fecha
     */
    private fun showDatePicker() {
        DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth)
                updateFechaField()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    /**
     * Actualiza el campo de fecha con el valor del calendario
     */
    private fun updateFechaField() {
        val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        binding.etFecha.setText(format.format(calendar.time))
    }

    /**
     * Configura los listeners de los botones
     */
    private fun setupListeners() {
        binding.btnSave.setOnClickListener {
            if (validateForm()) {
                saveGasto()
            }
        }

        binding.btnCancel.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    /**
     * Carga los datos del gasto en modo edición
     */
    private var currentGasto: Gasto? = null

    private fun loadGasto() {
        // Observar allGastos para obtener el gasto a editar
        viewModel.allGastos.observe(viewLifecycleOwner) { gastos ->
            val gasto = gastos.find { it.id == gastoId }
            gasto?.let {
                currentGasto = it
                binding.etNombre.setText(it.nombre)
                binding.etCantidad.setText(it.cantidad.toString())
                binding.spinnerCategoria.setText(it.categoria, false)
                binding.etFecha.setText(it.fecha)

                // Actualizar el calendario
                try {
                    val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    calendar.time = format.parse(it.fecha) ?: Date()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    /**
     * Valida el formulario
     */
    private fun validateForm(): Boolean {
        var isValid = true

        // Limpiar errores previos
        binding.tilNombre.error = null
        binding.tilCantidad.error = null
        binding.tilCategoria.error = null
        binding.tilFecha.error = null

        // Validar nombre
        val nombre = binding.etNombre.text.toString().trim()
        if (Validator.isFieldEmpty(nombre)) {
            binding.tilNombre.error = getString(R.string.error_field_required)
            isValid = false
        }

        // Validar cantidad
        val cantidadStr = binding.etCantidad.text.toString().trim()
        if (!Validator.isValidAmount(cantidadStr)) {
            binding.tilCantidad.error = getString(R.string.error_amount_invalid)
            isValid = false
        }

        // Validar categoría
        val categoria = binding.spinnerCategoria.text.toString().trim()
        if (Validator.isFieldEmpty(categoria)) {
            binding.tilCategoria.error = getString(R.string.error_field_required)
            isValid = false
        }

        // Validar fecha
        val fecha = binding.etFecha.text.toString().trim()
        if (Validator.isFieldEmpty(fecha)) {
            binding.tilFecha.error = getString(R.string.error_field_required)
            isValid = false
        }

        return isValid
    }

    /**
     * Guarda el gasto (crear o actualizar)
     */
    private fun saveGasto() {
        // Mostrar loading
        binding.progressBar.visibility = android.view.View.VISIBLE
        binding.btnSave.isEnabled = false
        binding.btnSave.text = ""

        val nombre = binding.etNombre.text.toString().trim()
        val cantidad = binding.etCantidad.text.toString().trim().toDouble()
        val categoria = binding.spinnerCategoria.text.toString().trim()
        val fecha = binding.etFecha.text.toString().trim()

        if (isEditMode && currentGasto != null) {
            // Actualizar gasto existente manteniendo el userEmail original
            val gasto = currentGasto!!.copy(
                nombre = nombre,
                cantidad = cantidad,
                categoria = categoria,
                fecha = fecha
            )
            viewModel.updateGasto(gasto)
            showSuccessMessage(getString(R.string.gasto_update_success))
        } else {
            // Crear nuevo gasto (el ViewModel añadirá el userEmail automáticamente)
            val gasto = Gasto(0, nombre, cantidad, categoria, fecha, "")
            viewModel.insertGasto(gasto)
            showSuccessMessage(getString(R.string.gasto_save_success))
        }

        // Volver atrás después de un pequeño delay para que se vea el feedback
        binding.root.postDelayed({
            findNavController().navigateUp()
        }, 500)
    }

    private fun showSuccessMessage(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT)
            .setBackgroundTint(requireContext().getColor(R.color.success))
            .setTextColor(requireContext().getColor(R.color.white))
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
