package com.example.data.repository

import com.example.data.dataSource.local.room.dao.WalletDao
import com.example.data.toDomain
import com.example.domain.UiState
import com.example.domain.model.WalletDomain
import com.example.domain.repository.WalletRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class WalletRepositoryImpl(
    private val walletDao: WalletDao
) : WalletRepository {
    override fun getWalletById(walletId: Int): Flow<UiState<WalletDomain?>> =
        flow {
            emit(UiState.Loading())
            emit(UiState.Success(walletDao.getWalletById(walletId)?.toDomain()))
        }.catch { e ->
            emit(UiState.Error(e.message.toString()))
        }.flowOn(Dispatchers.IO)
}