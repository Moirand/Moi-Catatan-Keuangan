package com.example.moi_catatan_keuangan

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object AddTransaction : Screen("add_transaction")
}