package com.example.drivenext.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.drivenext.data.repository.UserRepository
import com.example.drivenext.domain.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel для управления состоянием главного экрана
 * Обрабатывает бизнес-логику и взаимодействует с репозиторием
 */
class UserViewModel : ViewModel() {

    private val repository = UserRepository()

    // Состояния UI
    private val _usersState = MutableStateFlow<UiState<List<User>>>(UiState.Loading)
    val usersState: StateFlow<UiState<List<User>>> = _usersState

    init {
        loadUsers()
    }

    /**
     * Загружает список пользователей
     */
    fun loadUsers() {
        viewModelScope.launch {
            _usersState.value = UiState.Loading

            val result = repository.getUsers()

            _usersState.value = when {
                result.isSuccess -> UiState.Success(result.getOrNull() ?: emptyList())
                else -> UiState.Error("Ошибка загрузки данных")
            }
        }
    }
}

/**
 * Состояния UI для отображения загрузки, данных или ошибок
 */
sealed class UiState<out T> {
    object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val message: String) : UiState<Nothing>()
}