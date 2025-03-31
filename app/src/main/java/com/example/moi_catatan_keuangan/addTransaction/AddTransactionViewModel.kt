package com.example.moi_catatan_keuangan.addTransaction

import androidx.lifecycle.ViewModel
import com.example.domain.UiState
import com.example.domain.model.TransactionDomain
import com.example.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow

class AddTransactionViewModel(
    private val transactionRepository: TransactionRepository
): ViewModel() {
    fun insertTransaction(transaction: TransactionDomain): Flow<UiState<Unit>> =
        transactionRepository.insertTransaction(transaction)
}