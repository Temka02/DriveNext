package com.example.drivenext.domain.model

data class User(
    val id: Int = 0,
    val email: String = "",
    val password: String = "",
    val surname: String = "",
    val name: String = "",
    val patronymic: String? = null,
    val dob: String = "",
    val sex: String = "",
    val licenseNumber: String = "",
    val licenseDate: String = "",
    val registrationDate: String = "",
) {
    // Удобный конструктор для регистрации
    companion object {
        fun createForRegistration(
            email: String,
            password: String,
            surname: String,
            name: String,
            patronymic: String?,
            dob: String,
            sex: String,
            licenseNumber: String,
            licenseDate: String
        ): User {
            return User(
                email = email,
                password = password,
                surname = surname,
                name = name,
                patronymic = patronymic,
                dob = dob,
                sex = sex,
                licenseNumber = licenseNumber,
                licenseDate = licenseDate,
                registrationDate = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date())
            )
        }
    }
}