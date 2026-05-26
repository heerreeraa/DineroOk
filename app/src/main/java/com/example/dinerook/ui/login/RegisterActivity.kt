package com.example.dinerook.ui.login

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.dinerook.R
import com.example.dinerook.databinding.ActivityRegisterBinding
import com.example.dinerook.utils.Validator
import com.example.dinerook.viewmodel.AuthResult
import com.example.dinerook.viewmodel.AuthViewModel
import com.google.android.material.snackbar.Snackbar

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
        binding.btnRegister.setOnClickListener { attemptRegister() }
        binding.tvInfo.setOnClickListener { finish() }
    }

    private fun setupObservers() {
        authViewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.btnRegister.isEnabled = !isLoading
            binding.btnRegister.text = if (isLoading) "" else getString(R.string.register_button)
        }

        authViewModel.registerResult.observe(this) { result ->
            when (result) {
                is AuthResult.Success -> {
                    Snackbar.make(binding.root, getString(R.string.register_success), Snackbar.LENGTH_SHORT)
                        .setBackgroundTint(getColor(R.color.success))
                        .setTextColor(getColor(R.color.white))
                        .addCallback(object : Snackbar.Callback() {
                            override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                                finish()
                            }
                        })
                        .show()
                }
                is AuthResult.Error -> {
                    Snackbar.make(binding.root, getString(R.string.register_error_exists), Snackbar.LENGTH_LONG)
                        .setBackgroundTint(getColor(R.color.error))
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

        binding.tilEmail.error = null
        binding.tilPassword.error = null
        binding.tilConfirmPassword.error = null

        var isValid = true

        if (Validator.isFieldEmpty(email)) {
            binding.tilEmail.error = getString(R.string.error_email_required)
            isValid = false
        } else if (!Validator.isValidEmail(email)) {
            binding.tilEmail.error = getString(R.string.error_email_invalid)
            isValid = false
        }

        if (Validator.isFieldEmpty(password)) {
            binding.tilPassword.error = getString(R.string.error_password_required)
            isValid = false
        } else if (!Validator.isValidPassword(password)) {
            binding.tilPassword.error = getString(R.string.error_password_invalid)
            isValid = false
        }

        if (Validator.isFieldEmpty(confirmPassword)) {
            binding.tilConfirmPassword.error = getString(R.string.error_password_required)
            isValid = false
        } else if (password != confirmPassword) {
            binding.tilConfirmPassword.error = getString(R.string.error_passwords_not_match)
            isValid = false
        }

        if (!isValid) return

        authViewModel.register(email, password)
    }
}
