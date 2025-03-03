package com.example.data.dataSource.local.room.dao

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.dataSource.local.room.entity.WalletGroup
import com.example.domain.constant.tableColumn.WalletGroupColumn
import kotlinx.coroutines.flow.Flow

interface WalletGroupDao {
    @Query("SELECT * FROM ${WalletGroupColumn.TABLE_NAME}")
    fun getWalletGroups(): Flow<List<WalletGroup?>>

    @Query("SELECT * FROM ${WalletGroupColumn.TABLE_NAME} WHERE ${WalletGroupColumn.COLUMN_ID} = :walletGroupId")
    fun getWalletGroupById(walletGroupId: Int): Flow<WalletGroup?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertWalletGroups(walletGroupList: List<WalletGroup>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertWalletGroup(walletGroup: WalletGroup)

    @Delete
    fun deleteWalletGroup(walletGroup: WalletGroup)
}