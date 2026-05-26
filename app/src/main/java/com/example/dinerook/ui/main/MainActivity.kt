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

interface SortToggleListener {
    fun toggleSortMenu()
}

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var sessionManager: SessionManager
    private lateinit var navController: NavController
    private var currentMenu: Menu? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sessionManager = SessionManager(this)

        if (!sessionManager.isLoggedIn()) {
            redirectToLogin()
            return
        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        binding.toolbar.overflowIcon = ContextCompat.getDrawable(this, R.drawable.ic_more_vert)?.mutate()?.apply {
            setTint(ContextCompat.getColor(this@MainActivity, R.color.white))
        }

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.fragmentContainer) as NavHostFragment
        navController = navHostFragment.navController

        val appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            updateMenuVisibility(destination.id)
        }
    }

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

    private fun updateMenuVisibility(destinationId: Int) {
        currentMenu?.let { menu ->
            when (destinationId) {
                R.id.gastoListFragment -> {
                    menu.findItem(R.id.action_add_gasto)?.isVisible = true
                    menu.findItem(R.id.action_sort)?.isVisible = true
                    menu.findItem(R.id.action_stats)?.isVisible = true
                }
                R.id.statsFragment -> {
                    menu.findItem(R.id.action_add_gasto)?.isVisible = false
                    menu.findItem(R.id.action_sort)?.isVisible = false
                    menu.findItem(R.id.action_stats)?.isVisible = false
                }
                R.id.addEditGastoFragment -> {
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
                if (navController.currentDestination?.id == R.id.gastoListFragment) {
                    navController.navigate(R.id.action_gastoList_to_addEdit)
                }
                true
            }
            R.id.action_sort -> {
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

    private fun logout() {
        sessionManager.logout()
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
