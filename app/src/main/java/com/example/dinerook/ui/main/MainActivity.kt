package com.example.dinerook.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.dinerook.R
import com.example.dinerook.databinding.ActivityMainBinding
import com.example.dinerook.ui.login.LoginActivity
import com.example.dinerook.utils.SessionManager

/**
 * Activity principal que contiene los fragments
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var sessionManager: SessionManager
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicializar SessionManager
        sessionManager = SessionManager(this)

        // Configurar Toolbar
        setSupportActionBar(binding.toolbar)

        // Configurar Navigation - usando supportFragmentManager
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.fragmentContainer) as NavHostFragment
        navController = navHostFragment.navController

        val appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_add_gasto -> {
                // Navegar al fragment de agregar gasto
                navController.navigate(R.id.action_gastoList_to_addEdit)
                true
            }
            R.id.action_stats -> {
                // Navegar a estadísticas
                navController.navigate(R.id.action_gastoList_to_stats)
                true
            }
            R.id.action_logout -> {
                showLogoutDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    /**
     * Muestra diálogo de confirmación de logout
     */
    private fun showLogoutDialog() {
        AlertDialog.Builder(this)
            .setTitle("Cerrar Sesión")
            .setMessage("¿Está seguro que desea cerrar sesión?")
            .setPositiveButton(R.string.btn_yes) { _, _ ->
                logout()
            }
            .setNegativeButton(R.string.btn_no, null)
            .show()
    }

    /**
     * Cierra sesión y vuelve al login
     */
    private fun logout() {
        sessionManager.logout()
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}

