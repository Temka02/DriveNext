package com.example.drivenext.presentation.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.drivenext.R
import com.example.drivenext.utils.SessionManager

class SplashActivity : AppCompatActivity() {

    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        sessionManager = SessionManager(this)

        Handler(Looper.getMainLooper()).postDelayed({
            checkSessionAndNavigate()
        }, 2500)
    }
    private fun checkSessionAndNavigate() {
        if (sessionManager.isLoggedIn()) {
            // Пользователь уже авторизован - идем на главный экран
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        } else {
            // Пользователь не авторизован - идем на онбординг
            val intent = Intent(this, OnboardingActivity::class.java)
            startActivity(intent)
        }
        finish()
    }
}