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
import java.text.SimpleDateFormat
import java.util.*
import android.widget.Toast

class ProfileActivity : AppCompatActivity() {

    private lateinit var sessionManager: SessionManager
    private lateinit var textViewName: TextView
    private lateinit var textViewJoinDate: TextView
    private lateinit var textViewUserEmail: TextView
    private lateinit var textViewUserSex: TextView
    private lateinit var textViewLogout: TextView
    private lateinit var textViewUserPassword: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        sessionManager = SessionManager(this)

        if (!sessionManager.isLoggedIn()) {
            startActivity(Intent(this, SignInActivity::class.java))
            finish()
            return
        }

        initViews()
        loadUserProfile()
        setupNavigation()
        setupLogout()
        setupChangePassword()
    }

    private fun initViews() {
        textViewName = findViewById(R.id.textViewName)
        textViewJoinDate = findViewById(R.id.textViewJoinDate)
        textViewUserEmail = findViewById(R.id.textViewUserEmail)
        textViewUserSex = findViewById(R.id.textViewUserSex)
        textViewLogout = findViewById(R.id.textViewLogout)
        textViewUserPassword = findViewById(R.id.textViewUserPassword)
    }

    private fun loadUserProfile() {
        // Сначала показываем данные из SessionManager (мгновенно)
        textViewName.text = "${sessionManager.getUserSurname()} ${sessionManager.getUserName()}"

        // Показываем дату регистрации из SessionManager
        val registrationDate = sessionManager.getRegistrationDate()
        if (registrationDate.isNotEmpty()) {
            formatAndDisplayDate(registrationDate)
        }

        // Затем загружаем полные данные из базы
        lifecycleScope.launch {
            val user = SupabaseHelper.getUserById(sessionManager.getUserId(), this@ProfileActivity)
            user?.let {
                textViewName.text = "${it.surname} ${it.name} ${it.patronymic ?: ""}"
                textViewUserEmail.text = it.email
                textViewUserSex.text = it.sex
                // Обновляем дату регистрации
                formatAndDisplayDate(it.registrationDate)
            }
        }
    }

    private fun formatAndDisplayDate(dateStr: String) {
        try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val date = inputFormat.parse(dateStr)

            val calendar = Calendar.getInstance()
            calendar.time = date

            val month = calendar.get(Calendar.MONTH)
            val year = calendar.get(Calendar.YEAR)

            val monthName = getMonthInPrepositionalCase(month)
            textViewJoinDate.text = "Присоединился в $monthName $year"
        } catch (e: Exception) {
            textViewJoinDate.text = "Присоединился в $dateStr"
        }
    }

    private fun getMonthInPrepositionalCase(month: Int): String {
        return when (month) {
            Calendar.JANUARY -> "январе"
            Calendar.FEBRUARY -> "феврале"
            Calendar.MARCH -> "марте"
            Calendar.APRIL -> "апреле"
            Calendar.MAY -> "мае"
            Calendar.JUNE -> "июне"
            Calendar.JULY -> "июле"
            Calendar.AUGUST -> "августе"
            Calendar.SEPTEMBER -> "сентябре"
            Calendar.OCTOBER -> "октябре"
            Calendar.NOVEMBER -> "ноябре"
            Calendar.DECEMBER -> "декабре"
            else -> ""
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
            startActivity(Intent(this, SettingsActivity::class.java))
        }
    }

    private fun setupLogout() {
        textViewLogout.setOnClickListener {
            sessionManager.logout()

            val intent = Intent(this, SignInActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

    private fun setupChangePassword() {
        textViewUserPassword.setOnClickListener {
            startActivity(Intent(this, ChangingPasswordActivity::class.java))
        }
    }
}