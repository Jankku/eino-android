package com.jankku.eino.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.preference.PreferenceManager
import com.google.android.material.navigationrail.NavigationRailView
import com.jankku.eino.R
import com.jankku.eino.databinding.ActivityMainBinding
import com.jankku.eino.ui.auth.AuthViewModel
import com.jankku.eino.util.applyTheme
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "MainActivity"

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navHostFragment: NavHostFragment
    private lateinit var navController: NavController
    private val viewModel: AuthViewModel by viewModels()
    var navRail: NavigationRailView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme() // Prevents screen flashing when it's called before super.onCreate()
        super.onCreate(savedInstanceState)
        installSplashScreen()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupNavigation()
        setupStartDestination()
        setupActionBar()
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    private fun setupNavigation() {
        navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_container) as NavHostFragment
        navController = navHostFragment.navController
        navRail = binding.navigationRail

        binding.navigationRail?.setupWithNavController(navController)
        binding.bottomNavigation?.setupWithNavController(navController)
    }

    private fun setupStartDestination() {
        val graph = navController.navInflater.inflate(R.navigation.nav_graph)

        viewModel.isLoggedIn.observe(this) { isLoggedIn ->
            if (isLoggedIn) {
                graph.setStartDestination(R.id.book_graph)
            } else {
                graph.setStartDestination(R.id.auth_graph)
            }

            navController.graph = graph
        }
    }

    private fun setupActionBar() {
        setSupportActionBar(binding.toolbar)
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.landingFragment,
                R.id.bookListFragment,
                R.id.movieListFragment,
                R.id.settingsFragment
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
    }

    private fun setTheme() {
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        val theme = prefs.getString(
            getString(R.string.settings_theme_key),
            getString(R.string.settings_theme_value_system)
        )

        if (theme != null) applyTheme(this, theme)
    }

    fun setCustomTitle(title: String) {
        supportActionBar?.title = title
    }

    fun setBottomNavigationVisibility(visibility: Int) {
        binding.bottomNavigation?.visibility = visibility
    }

    fun setNavigationRailVisibility(visibility: Int) {
        binding.navigationRail?.visibility = visibility
    }
}