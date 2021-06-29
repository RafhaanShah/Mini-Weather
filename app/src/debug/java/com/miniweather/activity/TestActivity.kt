package com.miniweather.activity

import android.os.Bundle
import androidx.annotation.IdRes
import androidx.annotation.NavigationRes
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.miniweather.R

class TestActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)
    }

    fun navigateToFragment(
        @NavigationRes navGraphId: Int,
        @IdRes startDestId: Int,
        fragmentArgs: Bundle? = null
    ) {
        val navController = findNavController(R.id.test_nav_container)
        val navGraph = navController.navInflater.inflate(navGraphId)
        navGraph.startDestination = startDestId
        navController.setGraph(navGraph, fragmentArgs)
    }
}
