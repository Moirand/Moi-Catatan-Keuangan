package com.example.domain.model

import com.example.domain.constant.CategoryType

data class CategoryDomain(
    val id: Int = 0,
    val name: String,
    val type: CategoryType
)