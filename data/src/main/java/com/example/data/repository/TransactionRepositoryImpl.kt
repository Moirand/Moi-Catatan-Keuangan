package com.example.data.repository

import androidx.sqlite.db.SimpleSQLiteQuery
import com.example.data.dataSource.local.room.dao.TransactionDao
import com.example.data.toDomain
import com.example.domain.UiState
import com.example.domain.model.TransactionDomain
import com.example.domain.repository.TransactionRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.math.BigDecimal

class TransactionRepositoryImpl(
    private val transactionDao: TransactionDao
): TransactionRepository {
    override fun getTransactions(query: SimpleSQLiteQuery): Flow<UiState<List<TransactionDomain?>>> =
        flow {
            emit(UiState.Loading())
            emit(UiState.Success(transactionDao.getTransactions(query).map { it?.toDomain() }))
        }.catch { e ->
            emit(UiState.Error(e.message.toString()))
        }.flowOn(Dispatchers.IO)

    override fun getTransactionById(transactionId: Int): Flow<UiState<TransactionDomain?>> =
        flow {
            emit(UiState.Loading())
            emit(UiState.Success(transactionDao.getTransactionById(transactionId)?.toDomain()))
        }.catch { e ->
            emit(UiState.Error(e.message.toString()))
        }.flowOn(Dispatchers.IO)

    override fun calculateAmounts(query: SimpleSQLiteQuery): Flow<UiState<BigDecimal>> =
        flow {
            emit(UiState.Loading())
            emit(UiState.Success(transactionDao.calculateAmounts(query)))
        }.catch { e ->
            emit(UiState.Error(e.message.toString()))
        }.flowOn(Dispatchers.IO)
}