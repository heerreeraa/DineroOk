package com.example.dinerook.ui.login

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.dinerook.R
import com.example.dinerook.databinding.ActivityLoginBinding
import com.example.dinerook.ui.main.MainActivity
import com.example.dinerook.utils.SessionManager
import com.example.dinerook.utils.Validator
import com.google.android.material.snackbar.Snackbar

/**
 * Activity de Login
 * Gestiona la autenticación del usuario con validaciones
 */
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicializar SessionManager
        sessionManager = SessionManager(this)

        // Verificar si ya hay sesión activa
        if (sessionManager.isLoggedIn()) {
            navigateToMain()
            return
        }

        setupUI()
    }

    private fun setupUI() {
        binding.btnLogin.setOnClickListener {
            attemptLogin()
        }
    }

    /**
     * Intenta realizar el login con validaciones
     */
    private fun attemptLogin() {
        // Obtener valores
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()

        // Limpiar errores previos
        binding.tilEmail.error = null
        binding.tilPassword.error = null

        // Validar campos
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

        // Si las validaciones fallan, detener
        if (!isValid) {
            return
        }

        // Login exitoso (en una app real, aquí iría la validación contra servidor)
        performLogin(email)
    }

    /**
     * Realiza el login y guarda la sesión
     */
    private fun performLogin(email: String) {
        // Guardar sesión
        sessionManager.saveSession(email)

        // Mostrar mensaje de éxito
        Toast.makeText(this, getString(R.string.login_success), Toast.LENGTH_SHORT).show()

        // Navegar a MainActivity
        navigateToMain()
    }

    /**
     * Navega a MainActivity y cierra LoginActivity
     */
    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}

