package com.example.drivenext.presentation.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.drivenext.R
import com.example.drivenext.utils.SessionManager
import com.example.drivenext.utils.SupabaseHelper
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch
import androidx.lifecycle.lifecycleScope

class ChangingPasswordActivity : AppCompatActivity() {

    private lateinit var sessionManager: SessionManager
    private lateinit var editTextCurrentPassword: TextInputEditText
    private lateinit var editTextNewPassword: TextInputEditText
    private lateinit var editTextRepeatPassword: TextInputEditText
    private lateinit var buttonSavePassword: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_changing_password)

        sessionManager = SessionManager(this)

        initViews()
        setupClickListeners()
    }

    private fun initViews() {
        editTextCurrentPassword = findViewById(R.id.editTextCurrentPassword)
        editTextNewPassword = findViewById(R.id.editTextNewPassword)
        editTextRepeatPassword = findViewById(R.id.editTextRepeatPassword)
        buttonSavePassword = findViewById(R.id.buttonSavePassword)
    }

    private fun setupClickListeners() {
        buttonSavePassword.setOnClickListener {
            changePassword()
        }
    }

    private fun changePassword() {
        val currentPassword = editTextCurrentPassword.text.toString()
        val newPassword = editTextNewPassword.text.toString()
        val repeatPassword = editTextRepeatPassword.text.toString()

        // Валидация
        if (currentPassword.isEmpty()) {
            editTextCurrentPassword.error = "Введите текущий пароль"
            return
        }

        if (newPassword.isEmpty()) {
            editTextNewPassword.error = "Введите новый пароль"
            return
        }

        if (newPassword.length < 8) {
            editTextNewPassword.error = "Пароль должен содержать минимум 8 символов"
            return
        }

        if (repeatPassword.isEmpty()) {
            editTextRepeatPassword.error = "Повторите новый пароль"
            return
        }

        if (newPassword != repeatPassword) {
            editTextRepeatPassword.error = "Пароли не совпадают"
            return
        }


        lifecycleScope.launch {
            try {
                val success = SupabaseHelper.updatePassword(
                    userId = sessionManager.getUserId(),
                    currentPassword = currentPassword,
                    newPassword = newPassword,
                    context = this@ChangingPasswordActivity
                )

                if (success) {
                    finish()
                }
            } catch (e: Exception) {
            } finally {

                runOnUiThread {
                    buttonSavePassword.isEnabled = true
                    buttonSavePassword.text = "Сохранить"
                }
            }
        }
    }
}