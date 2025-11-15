package com.example.drivenext.presentation.activities

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.drivenext.R
import com.example.drivenext.utils.SessionManager
import com.example.drivenext.utils.SupabaseHelper
import kotlinx.coroutines.launch
import android.widget.Toast

class SettingsActivity : AppCompatActivity() {

    private lateinit var sessionManager: SessionManager
    private lateinit var textViewUserName: TextView
    private lateinit var textViewUserEmail: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        sessionManager = SessionManager(this)

        // Проверяем авторизацию
        if (!sessionManager.isLoggedIn()) {
            startActivity(Intent(this, SignInActivity::class.java))
            finish()
            return
        }

        initViews()
        loadUserData()
        setupNavigation()
    }

    private fun initViews() {
        textViewUserName = findViewById(R.id.textViewUserName)
        textViewUserEmail = findViewById(R.id.textViewUserEmail)

        // Показываем базовые данные из сессии
        textViewUserName.text = "${sessionManager.getUserSurname()} ${sessionManager.getUserName()}"
        textViewUserEmail.text = sessionManager.getUserEmail()
    }

    private fun loadUserData() {
        lifecycleScope.launch {
            // Загружаем полные данные пользователя из базы
            val user = SupabaseHelper.getUserById(sessionManager.getUserId(), this@SettingsActivity)
            user?.let {
                // Обновляем данные, если нужно
                textViewUserName.text = "${it.surname} ${it.name}"
                textViewUserEmail.text = it.email
            }
        }
    }

    private fun setupNavigation() {
        findViewById<ImageView>(R.id.imageViewHomePage).setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

        findViewById<ImageView>(R.id.imageViewBookmark).setOnClickListener {
            Toast.makeText(this, "Переход в избранное", Toast.LENGTH_SHORT).show()
        }

        findViewById<ImageView>(R.id.imageViewSettingsPage).setOnClickListener {
            // Уже на странице настроек
        }

        // Добавляем переход на профиль
        findViewById<ImageView>(R.id.imageViewArrowRight).setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }
    }
}