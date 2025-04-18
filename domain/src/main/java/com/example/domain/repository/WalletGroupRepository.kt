package com.example.domain.repository

import com.example.domain.UiState
import com.example.domain.model.WalletGroupDomain
import kotlinx.coroutines.flow.Flow

interface WalletGroupRepository {
    fun getWalletGroups(): Flow<UiState<List<WalletGroupDomain?>>>
    fun getWalletGroupById(walletGroupId: Int): Flow<UiState<WalletGroupDomain?>>
}