package com.example.data.dataSource.local.room.dao

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.dataSource.local.room.entity.Wallet
import com.example.domain.constant.tableColumn.WalletColumn
import kotlinx.coroutines.flow.Flow

interface WalletDao {
    @Query("SELECT * FROM ${WalletColumn.TABLE_NAME} WHERE ${WalletColumn.COLUMN_WALLET_GROUP_ID} = :walletGroupId")
    fun getWalletsByWalletGroupId(walletGroupId: Int): Flow<List<Wallet?>>

    @Query("SELECT * FROM ${WalletColumn.TABLE_NAME} WHERE ${WalletColumn.COLUMN_ID} = :walletId")
    fun getWalletById(walletId: Int): Flow<Wallet?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertWallets(walletList: List<Wallet>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertWallet(wallet: Wallet)

    @Delete
    fun deleteWallet(wallet: Wallet)
}