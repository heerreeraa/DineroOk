package com.example.dinerook.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.dinerook.R
import com.example.dinerook.databinding.ActivityMainBinding
import com.example.dinerook.ui.login.LoginActivity
import com.example.dinerook.utils.SessionManager

/**
 * Interface para comunicar con el fragment de lista de gastos
 */
interface SortToggleListener {
    fun toggleSortMenu()
}

/**
 * Activity principal que contiene los fragments
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var sessionManager: SessionManager
    private lateinit var navController: NavController
    private var currentMenu: Menu? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializar SessionManager
        sessionManager = SessionManager(this)

        // Verificar si hay sesión activa, si no, ir a login
        if (!sessionManager.isLoggedIn()) {
            redirectToLogin()
            return
        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configurar Toolbar
        setSupportActionBar(binding.toolbar)

        // Forzar icono de overflow (3 puntitos) en blanco
        binding.toolbar.overflowIcon = ContextCompat.getDrawable(this, R.drawable.ic_more_vert)?.mutate()?.apply {
            setTint(ContextCompat.getColor(this@MainActivity, R.color.white))
        }

        // Configurar Navigation - usando supportFragmentManager
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.fragmentContainer) as NavHostFragment
        navController = navHostFragment.navController

        val appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

        // Escuchar cambios de destino para mostrar/ocultar botones
        navController.addOnDestinationChangedListener { _, destination, _ ->
            updateMenuVisibility(destination.id)
        }
    }

    /**
     * Redirige a LoginActivity si no hay sesión
     */
    private fun redirectToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        currentMenu = menu
        updateMenuVisibility(navController.currentDestination?.id ?: R.id.gastoListFragment)
        return true
    }

    /**
     * Actualiza la visibilidad de los elementos del menú según el fragmento actual
     */
    private fun updateMenuVisibility(destinationId: Int) {
        currentMenu?.let { menu ->
            when (destinationId) {
                R.id.gastoListFragment -> {
                    // En la lista: mostrar añadir, ordenar y stats
                    menu.findItem(R.id.action_add_gasto)?.isVisible = true
                    menu.findItem(R.id.action_sort)?.isVisible = true
                    menu.findItem(R.id.action_stats)?.isVisible = true
                }
                R.id.statsFragment -> {
                    // En estadísticas: ocultar todos menos logout
                    menu.findItem(R.id.action_add_gasto)?.isVisible = false
                    menu.findItem(R.id.action_sort)?.isVisible = false
                    menu.findItem(R.id.action_stats)?.isVisible = false
                }
                R.id.addEditGastoFragment -> {
                    // En añadir/editar: ocultar todos menos logout
                    menu.findItem(R.id.action_add_gasto)?.isVisible = false
                    menu.findItem(R.id.action_sort)?.isVisible = false
                    menu.findItem(R.id.action_stats)?.isVisible = false
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_add_gasto -> {
                // Navegar al fragment de agregar gasto solo si estamos en la lista
                if (navController.currentDestination?.id == R.id.gastoListFragment) {
                    navController.navigate(R.id.action_gastoList_to_addEdit)
                }
                true
            }
            R.id.action_sort -> {
                // Mostrar/ocultar menú de ordenación
                if (navController.currentDestination?.id == R.id.gastoListFragment) {
                    val navHostFragment = supportFragmentManager
                        .findFragmentById(R.id.fragmentContainer) as NavHostFragment
                    val currentFragment = navHostFragment.childFragmentManager.fragments.firstOrNull()
                    if (currentFragment is SortToggleListener) {
                        currentFragment.toggleSortMenu()
                    }
                }
                true
            }
            R.id.action_stats -> {
                // Navegar a estadísticas solo si estamos en la lista
                if (navController.currentDestination?.id == R.id.gastoListFragment) {
                    navController.navigate(R.id.action_gastoList_to_stats)
                }
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
