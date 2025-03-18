package com.example.data.dataSource.local.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RawQuery
import androidx.sqlite.db.SimpleSQLiteQuery
import com.example.data.dataSource.local.room.entity.Transaction
import com.example.domain.constant.tableColumn.TransactionColumn
import java.math.BigDecimal

@Dao
interface TransactionDao {
    @RawQuery(observedEntities = [Transaction::class])
    fun getTransactions(query: SimpleSQLiteQuery): List<Transaction?>

    @RawQuery(observedEntities = [Transaction::class])
    fun calculateAmounts(query: SimpleSQLiteQuery): BigDecimal

    @Query("SELECT * FROM ${TransactionColumn.TABLE_NAME} WHERE ${TransactionColumn.COLUMN_ID} = :transactionId")
    fun getTransactionById(transactionId: Int): Transaction?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTransactions(transactionList: List<Transaction>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTransaction(transaction: Transaction)

    @Delete
    fun deleteTransaction(transaction: Transaction)
}