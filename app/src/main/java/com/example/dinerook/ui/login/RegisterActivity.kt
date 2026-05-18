package com.example.dinerook.ui.login

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.dinerook.R
import com.example.dinerook.databinding.ActivityRegisterBinding
import com.example.dinerook.utils.Validator
import com.example.dinerook.viewmodel.AuthResult
import com.example.dinerook.viewmodel.AuthViewModel

/**
 * Activity de Registro
 * Permite crear nuevas cuentas de usuario
 */
class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
        setupObservers()
    }

    private fun setupUI() {
        binding.btnRegister.setOnClickListener {
            attemptRegister()
        }

        // Link para ir a login
        binding.tvInfo.setOnClickListener {
            finish() // Volver a LoginActivity
        }
    }

    private fun setupObservers() {
        // Observar estado de loading
        authViewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) android.view.View.VISIBLE else android.view.View.GONE
            binding.btnRegister.isEnabled = !isLoading
            binding.btnRegister.text = if (isLoading) "" else getString(R.string.register_button)
        }

        authViewModel.registerResult.observe(this) { result ->
            when (result) {
                is AuthResult.Success -> {
                    // Mostrar Snackbar de éxito antes de volver
                    com.google.android.material.snackbar.Snackbar.make(
                        binding.root,
                        getString(R.string.register_success),
                        com.google.android.material.snackbar.Snackbar.LENGTH_SHORT
                    ).setBackgroundTint(getColor(R.color.success))
                    .setTextColor(getColor(R.color.white))
                    .addCallback(object : com.google.android.material.snackbar.Snackbar.Callback() {
                        override fun onDismissed(transientBottomBar: com.google.android.material.snackbar.Snackbar?, event: Int) {
                            finish() // Volver a login después del Snackbar
                        }
                    })
                    .show()
                }
                is AuthResult.Error -> {
                    // Mostrar Snackbar con error
                    com.google.android.material.snackbar.Snackbar.make(
                        binding.root,
                        getString(R.string.register_error_exists),
                        com.google.android.material.snackbar.Snackbar.LENGTH_LONG
                    ).setBackgroundTint(getColor(R.color.error))
                    .setTextColor(getColor(R.color.white))
                    .show()
                }
            }
        }
    }

    private fun attemptRegister() {
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()
        val confirmPassword = binding.etConfirmPassword.text.toString().trim()

        // Limpiar errores previos
        binding.tilEmail.error = null
        binding.tilPassword.error = null
        binding.tilConfirmPassword.error = null

        var isValid = true

        // Validar email
        if (Validator.isFieldEmpty(email)) {
            binding.tilEmail.error = getString(R.string.error_email_required)
            isValid = false
        } else if (!Validator.isValidEmail(email)) {
            binding.tilEmail.error = getString(R.string.error_email_invalid)
            isValid = false
        }

        // Validar contraseña
        if (Validator.isFieldEmpty(password)) {
            binding.tilPassword.error = getString(R.string.error_password_required)
            isValid = false
        } else if (!Validator.isValidPassword(password)) {
            binding.tilPassword.error = getString(R.string.error_password_invalid)
            isValid = false
        }

        // Validar confirmación de contraseña
        if (Validator.isFieldEmpty(confirmPassword)) {
            binding.tilConfirmPassword.error = getString(R.string.error_password_required)
            isValid = false
        } else if (password != confirmPassword) {
            binding.tilConfirmPassword.error = getString(R.string.error_passwords_not_match)
            isValid = false
        }

        if (!isValid) return

        // Intentar registro
        authViewModel.register(email, password)
    }
}

