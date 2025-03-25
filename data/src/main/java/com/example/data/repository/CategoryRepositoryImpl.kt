package com.example.data.repository

import com.example.data.dataSource.local.room.dao.CategoryDao
import com.example.data.toDomain
import com.example.domain.UiState
import com.example.domain.model.CategoryDomain
import com.example.domain.repository.CategoryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class CategoryRepositoryImpl(
    private val categoryDao: CategoryDao
): CategoryRepository {
    override fun getCategoryById(categoryId: Int): Flow<UiState<CategoryDomain?>> =
        flow {
            emit(UiState.Loading())
            emit(UiState.Success(categoryDao.getCategoryById(categoryId)?.toDomain()))
        }.catch { e ->
            emit(UiState.Error(e.message.toString()))
        }.flowOn(Dispatchers.IO)
}