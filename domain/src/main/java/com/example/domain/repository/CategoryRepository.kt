package com.example.domain.repository

import com.example.domain.UiState
import com.example.domain.model.CategoryDomain
import kotlinx.coroutines.flow.Flow

interface CategoryRepository {
    fun getCategoryById(categoryId: Int): Flow<UiState<CategoryDomain?>>
}