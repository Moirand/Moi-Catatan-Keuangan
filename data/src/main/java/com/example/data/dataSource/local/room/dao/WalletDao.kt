package com.example.data.dataSource.local.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.dataSource.local.room.entity.Wallet
import com.example.domain.constant.tableColumn.WalletColumn

@Dao
interface WalletDao {
    @Query("SELECT * FROM ${WalletColumn.TABLE_NAME}")
    fun getWallets(): List<Wallet?>

    @Query("SELECT * FROM ${WalletColumn.TABLE_NAME} WHERE ${WalletColumn.COLUMN_ID} = :walletId")
    fun getWalletById(walletId: Int): Wallet?

    @Query("SELECT * FROM ${WalletColumn.TABLE_NAME} WHERE ${WalletColumn.COLUMN_WALLET_GROUP_ID} = :walletGroupId")
    fun getWalletsByWalletGroupId(walletGroupId: Int): List<Wallet?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertWallets(walletList: List<Wallet>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertWallet(wallet: Wallet)

    @Delete
    fun deleteWallet(wallet: Wallet)
}