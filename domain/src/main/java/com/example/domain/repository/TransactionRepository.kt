package com.example.domain.repository

import androidx.sqlite.db.SimpleSQLiteQuery
import com.example.domain.UiState
import com.example.domain.model.TransactionDomain
import kotlinx.coroutines.flow.Flow
import java.math.BigDecimal

interface TransactionRepository {
    fun getTransactions(query: SimpleSQLiteQuery): Flow<UiState<List<TransactionDomain?>>>
    fun getTransactionById(transactionId: Int): Flow<UiState<TransactionDomain?>>
    fun calculateAmounts(query: SimpleSQLiteQuery): Flow<UiState<BigDecimal>>
    fun insertTransaction(transaction: TransactionDomain): Flow<UiState<Unit>>
    fun deleteTransaction(transactionId: Int): Flow<UiState<Unit>>
}