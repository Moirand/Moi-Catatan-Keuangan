package com.example.di

import com.example.data.dataSource.local.room.AppDatabase
import com.example.data.dataSource.local.room.dao.CategoryDao
import com.example.data.dataSource.local.room.dao.TransactionDao
import com.example.data.dataSource.local.room.dao.WalletDao
import com.example.data.dataSource.local.room.dao.WalletGroupDao
import com.example.data.repository.CategoryRepositoryImpl
import com.example.data.repository.TransactionRepositoryImpl
import com.example.data.repository.WalletGroupRepositoryImpl
import com.example.data.repository.WalletRepositoryImpl
import com.example.domain.repository.CategoryRepository
import com.example.domain.repository.TransactionRepository
import com.example.domain.repository.WalletGroupRepository
import com.example.domain.repository.WalletRepository
import org.koin.dsl.module

val moduleKoin = module {
    // Database
    single<AppDatabase> { AppDatabase.getInstance(get()) }

    // DAO
    single<TransactionDao> { (get() as AppDatabase).transactionDao() }
    single<CategoryDao> { (get() as AppDatabase).categoryDao() }
    single<WalletDao> { (get() as AppDatabase).walletDao() }
    single<WalletGroupDao> { (get() as AppDatabase).walletGroupDao() }

    // Repository
    single<TransactionRepository> { TransactionRepositoryImpl(get()) }
    single<CategoryRepository> { CategoryRepositoryImpl(get()) }
    single<WalletRepository> { WalletRepositoryImpl(get()) }
    single<WalletGroupRepository> { WalletGroupRepositoryImpl(get()) }
}