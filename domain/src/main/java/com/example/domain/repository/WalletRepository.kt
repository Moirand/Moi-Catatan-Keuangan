package com.example.domain.repository

import com.example.domain.UiState
import com.example.domain.model.WalletDomain
import kotlinx.coroutines.flow.Flow

interface WalletRepository {
    fun getWalletById(walletId: Int): Flow<UiState<WalletDomain?>>
}