package com.jankku.eino.ui

import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.jankku.eino.R
import com.jankku.eino.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navHostFragment: NavHostFragment
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_container) as NavHostFragment
        navController = navHostFragment.navController

        binding.bottomNavigation.setupWithNavController(navController)

        // Setup the ActionBar with navController and 3 top level destinations
        appBarConfiguration = AppBarConfiguration(
            setOf(R.id.bookListFragment, R.id.movieListFragment, R.id.settingsFragment)
        )
        setupActionBarWithNavController(navController, appBarConfiguration)

        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)
    }

    /**
     * Used in [BaseFragment] to set the visibility of bottom nav
     */
    fun setBottomNavigationVisibility(visibility: Int) {
        // get the reference of the bottomNavigation and set the visibility.
        binding.bottomNavigation.visibility = visibility
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}