package com.example.data.repository

import com.example.data.dataSource.local.room.dao.WalletGroupDao
import com.example.data.toDomain
import com.example.domain.UiState
import com.example.domain.model.WalletGroupDomain
import com.example.domain.repository.WalletGroupRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class WalletGroupRepositoryImpl(
    private val walletGroupDao: WalletGroupDao
) : WalletGroupRepository {
    override fun getWalletGroupById(walletGroupId: Int): Flow<UiState<WalletGroupDomain?>> =
        flow {
            emit(UiState.Loading())
            emit(UiState.Success(walletGroupDao.getWalletGroupById(walletGroupId)?.toDomain()))
        }.catch { e ->
            emit(UiState.Error(e.message.toString()))
        }
}