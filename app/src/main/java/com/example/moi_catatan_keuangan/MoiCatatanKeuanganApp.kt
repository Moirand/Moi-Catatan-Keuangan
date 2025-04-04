package com.example.moi_catatan_keuangan

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.moi_catatan_keuangan.addTransaction.AddTransactionScreen
import com.example.moi_catatan_keuangan.component.MyBottomBar
import com.example.moi_catatan_keuangan.component.MyTopBar
import com.example.moi_catatan_keuangan.home.HomeScreen

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MoiCatatanKeuanganApp() {
    val navController = rememberNavController()
    val currentScreen = navController.currentBackStackEntryAsState().value?.destination?.route

    var topBar: @Composable () -> Unit by remember { mutableStateOf({}) }

    Scaffold(
        topBar = topBar,
        bottomBar = { if (currentScreen == Screen.Home.route) MyBottomBar(navController) },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        NavHost(
            modifier = Modifier.padding(innerPadding),
            startDestination = Screen.Home.route,
            navController = navController
        ) {
            composable(Screen.Home.route) {
                topBar = {}
                HomeScreen()
            }

            composable(Screen.AddTransaction.route) {
                var onSaveButtonClicked: () -> Unit by remember { mutableStateOf({}) }
                topBar = {
                    MyTopBar(
                        title = { Text("Tambah Transaksi") },
                        navigationIcon = {
                            IconButton(
                                onClick = { navController.navigateUp() }
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                                    contentDescription = "Kembali"
                                )
                            }
                        },
                        actions = {
                            IconButton(
                                onClick = {
                                    onSaveButtonClicked()
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Simpan"
                                )
                            }
                        }
                    )
                }
                AddTransactionScreen(
                    onSave = { isSaved ->
                        if (isSaved) navController.navigateUp()
                    },
                    onSaveButtonClick = {
                        onSaveButtonClicked = it
                    }
                )
            }
        }
    }
}