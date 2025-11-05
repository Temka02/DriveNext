package com.example.drivenext.data.repository

import com.example.drivenext.domain.model.User
import kotlinx.coroutines.delay

/**
 * Репозиторий для работы с пользователями
 */
class UserRepository {

    /**
     * Получает список пользователей
     * @return Результат с списком пользователей или ошибкой
     */
    suspend fun getUsers(): Result<List<User>> {
        return try {
            // Имитация загрузки данных
            delay(1000)

            // Заглушка с тестовыми данными
            val users = listOf(
                User("1", "Иван Иванов", "ivan@example.com"),
                User("2", "Мария Петрова", "maria@example.com"),
                User("3", "Алексей Сидоров", "alex@example.com")
            )

            Result.success(users)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}