package com.example.drivenext.utils

import android.content.Context
import android.widget.Toast
import io.ktor.client.HttpClient
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.example.drivenext.domain.model.User
import org.json.JSONObject
import org.json.JSONArray
import io.ktor.client.request.get
import io.ktor.client.request.patch
import io.ktor.client.call.body

object SupabaseHelper {
    // Настройки проекта Supabase
    private const val SUPABASE_URL = "https://wkdtdvmpsrntxyjdybwo.supabase.co"
    private const val SUPABASE_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6IndrZHRkdm1wc3JudHh5amR5YndvIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NjI1MzI2MzUsImV4cCI6MjA3ODEwODYzNX0.bQlZKXTyyfpkigWiO21xnEdkVIotdSmh2fisQisQBc8"

    private val client = HttpClient()

    // Получение списка автомобилей
    suspend fun getCars(): List<Car> {
        return try {
            val response = client.get("$SUPABASE_URL/rest/v1/cars") {
                header("apikey", SUPABASE_KEY)
                header("Authorization", "Bearer $SUPABASE_KEY")
                header("Content-Type", "application/json")
                url {
                    parameters.append("select", "*")
                }
            }

            val responseText = response.body<String>()

            if (responseText != null && responseText != "[]" && responseText != "null") {
                val jsonArray = JSONArray(responseText)
                val carsList = mutableListOf<Car>()

                for (i in 0 until jsonArray.length()) {
                    val carJson = jsonArray.getJSONObject(i)
                    carsList.add(
                        Car(
                            model = carJson.getString("model"),
                            brand = carJson.getString("brand"),
                            price = carJson.getInt("price"),
                            transmission = carJson.getString("transmission"),
                            fuelType = carJson.getString("fuel_type")
                        )
                    )
                }
                carsList
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    // Поиск автомобилей
    suspend fun searchCars(searchQuery: String): List<Car> {
        return try {
            val response = client.get("$SUPABASE_URL/rest/v1/cars") {
                header("apikey", SUPABASE_KEY)
                header("Authorization", "Bearer $SUPABASE_KEY")
                header("Content-Type", "application/json")
                url {
                    parameters.append("select", "*")
                    parameters.append("or", "(brand.ilike.%${searchQuery}%,model.ilike.%${searchQuery}%)")
                }
            }

            val responseText = response.body<String>()

            if (responseText != null && responseText != "[]" && responseText != "null") {
                val jsonArray = JSONArray(responseText)
                val carsList = mutableListOf<Car>()

                for (i in 0 until jsonArray.length()) {
                    val carJson = jsonArray.getJSONObject(i)
                    carsList.add(
                        Car(
                            model = carJson.getString("model"),
                            brand = carJson.getString("brand"),
                            price = carJson.getInt("price"),
                            transmission = carJson.getString("transmission"),
                            fuelType = carJson.getString("fuel_type")
                        )
                    )
                }
                carsList
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    // Авторизация пользователя
    suspend fun signInUser(email: String, password: String, context: Context): User? {
        return try {
            val response = client.get("$SUPABASE_URL/rest/v1/user") {
                header("apikey", SUPABASE_KEY)
                header("Authorization", "Bearer $SUPABASE_KEY")
                // Добавляем параметры через URL параметры
                url {
                    parameters.append("email", "eq.$email")
                    parameters.append("password", "eq.$password")
                    parameters.append("select", "*")
                }
            }

            val responseText = response.body<String>()

            if (responseText != null && responseText != "[]" && responseText != "null") {
                // Парсим JSON ответ
                val jsonArray = JSONArray(responseText)
                if (jsonArray.length() > 0) {
                    val userJson = jsonArray.getJSONObject(0)
                    User(
                        id = userJson.getInt("id"),
                        email = userJson.getString("email"),
                        password = "", // Не храним пароль в сессии
                        surname = userJson.getString("surname"),
                        name = userJson.getString("name"),
                        patronymic = if (userJson.has("patronymic")) userJson.getString("patronymic") else null,
                        dob = userJson.getString("dob"),
                        sex = userJson.getString("sex"),
                        licenseNumber = userJson.getString("license_number"),
                        licenseDate = userJson.getString("license_date"),
                        registrationDate = userJson.getString("registration_date")
                    )
                } else {
                    null
                }
            } else {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Неверный email или пароль", Toast.LENGTH_SHORT).show()
                }
                null
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Ошибка входа: ${e.message}", Toast.LENGTH_LONG).show()
            }
            e.printStackTrace()
            null
        }
    }

    // Метод для получения полной информации о пользователе по ID
    suspend fun getUserById(userId: Int, context: Context): User? {
        return try {
            val response = client.get("$SUPABASE_URL/rest/v1/user") {
                header("apikey", SUPABASE_KEY)
                header("Authorization", "Bearer $SUPABASE_KEY")
                url {
                    parameters.append("id", "eq.$userId")
                    parameters.append("select", "*")
                }
            }

            val responseText = response.body<String>()

            if (responseText != null && responseText != "[]" && responseText != "null") {
                val jsonArray = JSONArray(responseText)
                if (jsonArray.length() > 0) {
                    val userJson = jsonArray.getJSONObject(0)
                    User(
                        id = userJson.getInt("id"),
                        email = userJson.getString("email"),
                        password = "", // Не возвращаем пароль
                        surname = userJson.getString("surname"),
                        name = userJson.getString("name"),
                        patronymic = if (userJson.has("patronymic")) userJson.getString("patronymic") else null,
                        dob = userJson.getString("dob"),
                        sex = userJson.getString("sex"),
                        licenseNumber = userJson.getString("license_number"),
                        licenseDate = userJson.getString("license_date"),
                        registrationDate = userJson.getString("registration_date")
                    )
                } else {
                    null
                }
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // Регистрация пользователя
    suspend fun registerUser(user: User, context: Context): Boolean {
        return try {
            // Создаем JSON объект с данными пользователя
            val userJson = JSONObject().apply {
                put("email", user.email)
                put("password", user.password)
                put("surname", user.surname)
                put("name", user.name)
                put("patronymic", user.patronymic ?: "")
                put("dob", user.dob)
                put("sex", user.sex)
                put("license_number", user.licenseNumber)
                put("license_date", user.licenseDate)
                put("registration_date", user.registrationDate)
            }

            val response = client.post("$SUPABASE_URL/rest/v1/user") {
                header("apikey", SUPABASE_KEY)
                header("Authorization", "Bearer $SUPABASE_KEY")
                header("Prefer", "return=minimal")
                header("Content-Type", "application/json")
                setBody(userJson.toString())
            }

            println("HTTP Status: ${response.status}")
            val responseBody = response.body<String>()
            println("Response: $responseBody")

            val isSuccess = response.status.value in 200..299

            if (isSuccess) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Регистрация успешна!", Toast.LENGTH_SHORT).show()
                }
            } else {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Ошибка регистрации: ${response.status}", Toast.LENGTH_LONG).show()
                }
            }

            isSuccess
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Ошибка регистрации: ${e.message}", Toast.LENGTH_LONG).show()
            }
            e.printStackTrace()
            false
        }
    }

    // Обновление пароля
    suspend fun updatePassword(userId: Int, currentPassword: String, newPassword: String, context: Context): Boolean {
        return try {
            // Сначала проверяем, что текущий пароль верный
            val checkResponse = client.get("$SUPABASE_URL/rest/v1/user") {
                header("apikey", SUPABASE_KEY)
                header("Authorization", "Bearer $SUPABASE_KEY")
                url {
                    parameters.append("id", "eq.$userId")
                    parameters.append("password", "eq.$currentPassword")
                    parameters.append("select", "id")
                }
            }

            val checkResponseText = checkResponse.body<String>()

            // Если пользователь с таким ID и текущим паролем не найден
            if (checkResponseText == null || checkResponseText == "[]" || checkResponseText == "null") {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Неверный текущий пароль", Toast.LENGTH_SHORT).show()
                }
                return false
            }

            // Если текущий пароль верный - обновляем на новый
            val updateJson = JSONObject().apply {
                put("password", newPassword)
            }

            val updateResponse = client.patch("$SUPABASE_URL/rest/v1/user") {
                header("apikey", SUPABASE_KEY)
                header("Authorization", "Bearer $SUPABASE_KEY")
                header("Prefer", "return=minimal")
                header("Content-Type", "application/json")
                url {
                    parameters.append("id", "eq.$userId")
                }
                setBody(updateJson.toString())
            }

            val isSuccess = updateResponse.status.value in 200..299

            if (isSuccess) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Пароль успешно изменен", Toast.LENGTH_SHORT).show()
                }
            } else {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Ошибка при изменении пароля", Toast.LENGTH_SHORT).show()
                }
            }

            isSuccess
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Ошибка: ${e.message}", Toast.LENGTH_LONG).show()
            }
            e.printStackTrace()
            false
        }
    }
}

data class Car(
    val model: String,
    val brand: String,
    val price: Int,
    val transmission: String,
    val fuelType: String
)

