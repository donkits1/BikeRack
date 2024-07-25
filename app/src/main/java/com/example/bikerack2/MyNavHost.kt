package com.example.bikerack2

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

@Composable
fun MyNavHost(navController: NavHostController = rememberNavController(), starDest: String) {
        NavHost(navController = navController, startDestination = starDest) {
            composable(route = "BikeScreen") {
                BikeScreen(navController = navController)
            }
        }
}