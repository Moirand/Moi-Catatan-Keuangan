package com.example.moi_catatan_keuangan

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.moi_catatan_keuangan.addTransaction.AddTransactionScreen
import com.example.moi_catatan_keuangan.component.MyBottomBar
import com.example.moi_catatan_keuangan.home.HomeScreen

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MoiCatatanKeuanganApp() {
    val navController = rememberNavController()
    val currentScreen = navController.currentBackStackEntryAsState().value?.destination?.route

    Scaffold(
        bottomBar = { if (currentScreen == Screen.Home.route) MyBottomBar(navController) },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        NavHost(
            modifier = Modifier.padding(innerPadding),
            startDestination = Screen.Home.route,
            navController = navController
        ) {
            composable(Screen.Home.route) {
                HomeScreen()
            }

            composable(Screen.AddTransaction.route) {
                AddTransactionScreen()
            }
        }
    }
}