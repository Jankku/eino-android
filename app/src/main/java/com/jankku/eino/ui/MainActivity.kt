package com.jankku.eino.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.jankku.eino.R
import com.jankku.eino.databinding.ActivityMainBinding
import com.jankku.eino.ui.auth.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "MainActivity"

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navHostFragment: NavHostFragment
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_container) as NavHostFragment
        navController = navHostFragment.navController

        setNavigationGraph()

        binding.bottomNavigation.setupWithNavController(navController)

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.landingFragment,
                R.id.bookListFragment,
                R.id.movieListFragment,
                R.id.settingsFragment
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
    }

    fun setBottomNavigationVisibility(visibility: Int) {
        binding.bottomNavigation.visibility = visibility
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    private fun setNavigationGraph() {
        viewModel.isLoggedIn.observe(this) { isLoggedIn ->
            val navGraph = if (isLoggedIn) {
                navController.navInflater.inflate(R.navigation.nav_graph)
            } else {
                navController.navInflater.inflate(R.navigation.auth_graph)
            }

            navController.graph = navGraph
        }
    }
}