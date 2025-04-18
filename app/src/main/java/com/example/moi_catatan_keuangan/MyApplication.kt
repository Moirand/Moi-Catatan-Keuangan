package com.example.moi_catatan_keuangan

import android.app.Application
import com.example.di.moduleKoin
import com.example.moi_catatan_keuangan.addTransaction.AddTransactionViewModel
import com.example.moi_catatan_keuangan.home.HomeViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import org.koin.dsl.module

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@MyApplication)
            androidLogger(Level.DEBUG)
            modules(
                moduleKoin,
                viewModelKoin
            )
        }
    }
}

val viewModelKoin = module {
    viewModel { HomeViewModel(get(), get(), get(), get()) }
    viewModel { AddTransactionViewModel(get(), get(), get(), get()) }
}