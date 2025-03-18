package com.example.domain

sealed class UiState<T>(
    val data: T? = null,
    val message: String? = null
) {
    class Loading<T> : UiState<T>()
    class Error<T>(message: String) : UiState<T>(null, message)
    class Success<T>(data: T) : UiState<T>(data)
}