package com.example.dinerook.ui.login

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.dinerook.R
import com.example.dinerook.databinding.ActivityLoginBinding
import com.example.dinerook.ui.main.MainActivity
import com.example.dinerook.utils.SessionManager
import com.example.dinerook.utils.Validator
import com.example.dinerook.viewmodel.AuthResult
import com.example.dinerook.viewmodel.AuthViewModel

/**
 * Activity de Login
 * Gestiona la autenticación del usuario con validaciones
 */
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var sessionManager: SessionManager
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicializar SessionManager
        sessionManager = SessionManager(this)

        // Verificar si ya hay sesión activa
        if (sessionManager.isLoggedIn()) {
            // Verificar que el usuario realmente exista en la BD
            verifySessionAndNavigate()
        } else {
            setupUI()
            setupObservers()
        }
    }

    /**
     * Verifica que la sesión guardada sea válida (usuario existe en BD)
     */
    private fun verifySessionAndNavigate() {
        val email = sessionManager.getUserEmail()
        if (email.isNullOrEmpty()) {
            // No hay email guardado, limpiar sesión y mostrar login
            sessionManager.logout()
            setupUI()
            setupObservers()
            return
        }

        // Aquí se podría verificar contra la BD si el usuario existe
        // Por ahora, confiamos en la sesión guardada
        navigateToMain()
    }

    private fun setupUI() {
        binding.btnLogin.setOnClickListener {
            attemptLogin()
        }

        // Link para ir a registro
        binding.tvInfo.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupObservers() {
        authViewModel.loginResult.observe(this) { result ->
            when (result) {
                is AuthResult.Success -> {
                    val email = binding.etEmail.text.toString().trim()
                    sessionManager.saveSession(email)
                    Toast.makeText(this, getString(R.string.login_success), Toast.LENGTH_SHORT).show()
                    navigateToMain()
                }
                is AuthResult.Error -> {
                    Toast.makeText(this, getString(R.string.login_error), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    /**
     * Intenta realizar el login con validaciones
     */
    private fun attemptLogin() {
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()

        // Limpiar errores previos
        binding.tilEmail.error = null
        binding.tilPassword.error = null

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

        if (!isValid) return

        // Intentar login con base de datos
        authViewModel.login(email, password)
    }

    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}

