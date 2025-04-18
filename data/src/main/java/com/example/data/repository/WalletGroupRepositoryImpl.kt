package com.example.data.repository

import com.example.data.dataSource.local.room.dao.WalletGroupDao
import com.example.data.toDomain
import com.example.domain.UiState
import com.example.domain.model.WalletGroupDomain
import com.example.domain.repository.WalletGroupRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class WalletGroupRepositoryImpl(
    private val walletGroupDao: WalletGroupDao
) : WalletGroupRepository {
    override fun getWalletGroups(): Flow<UiState<List<WalletGroupDomain?>>> =
        flow {
            emit(UiState.Loading())
            emit(UiState.Success(walletGroupDao.getWalletGroups().map { it?.toDomain() }))
        }.catch { e ->
            emit(UiState.Error(e.message.toString()))
        }.flowOn(Dispatchers.IO)

    override fun getWalletGroupById(walletGroupId: Int): Flow<UiState<WalletGroupDomain?>> =
        flow {
            emit(UiState.Loading())
            emit(UiState.Success(walletGroupDao.getWalletGroupById(walletGroupId)?.toDomain()))
        }.catch { e ->
            emit(UiState.Error(e.message.toString()))
        }.flowOn(Dispatchers.IO)
}