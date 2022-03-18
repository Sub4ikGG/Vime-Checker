package com.example.vimechecker.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.ActivityNavigator
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.vimechecker.R
import com.example.vimechecker.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    private lateinit var navController: NavController
    /*private var currentFragment = 0*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navHostFragment = supportFragmentManager.findFragmentById(
            R.id.fragmentContainerView
        ) as NavHostFragment
        navController = navHostFragment.navController

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNavigationView.setupWithNavController(navController)
        /*currentFragment = R.id.main

        bottomNavigationView.setOnItemSelectedListener {
            var success = false
            when(it.itemId) {
                R.id.main -> {
                    success = switchFragment(R.id.main)
                }
                R.id.finder -> {
                    success = switchFragment(R.id.finder)
                }
                R.id.profile -> {
                    success = switchFragment(R.id.profile)
                }
            }
            success
        }*/
    }

    /*private fun switchFragment(fragment: Int): Boolean {
        return if(currentFragment != fragment) {
            navController.navigate(fragment)
            currentFragment = fragment
            true
        } else false
    }*/

    /*override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.fragmentContainerView)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }*/

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp()
    }
}