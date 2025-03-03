package com.example.data.dataSource.local.room.dao

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RawQuery
import androidx.sqlite.db.SupportSQLiteQuery
import com.example.data.dataSource.local.room.entity.Transaction
import com.example.domain.constant.tableColumn.TransactionColumn
import kotlinx.coroutines.flow.Flow

interface TransactionDao {
    @RawQuery(observedEntities = [Transaction::class])
    fun getTransactions(query: SupportSQLiteQuery): Flow<List<Transaction?>>

    @Query("SELECT * FROM ${TransactionColumn.TABLE_NAME} WHERE ${TransactionColumn.COLUMN_ID} = :transactionId")
    fun getTransactionById(transactionId: Int): Flow<Transaction?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTransactions(transactionList: List<Transaction>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTransaction(transaction: Transaction)

    @Delete
    fun deleteTransaction(transaction: Transaction)
}