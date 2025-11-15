package com.example.drivenext.presentation.activities

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.example.drivenext.R
import com.example.drivenext.utils.SessionManager
import com.example.drivenext.utils.SupabaseHelper
import kotlinx.coroutines.launch

class SignInActivity : AppCompatActivity() {
    private lateinit var emailInputLayout: TextInputLayout
    private lateinit var passwordInputLayout: TextInputLayout
    private lateinit var emailEditText: TextInputEditText
    private lateinit var passwordEditText: TextInputEditText
    private lateinit var signInButton: Button
    private lateinit var googleSignInButton: Button
    private lateinit var forgetPasswordText: TextView
    private lateinit var signUpText: TextView
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        sessionManager = SessionManager(this)

        // Если пользователь уже авторизован, переходим на главный экран
        if (sessionManager.isLoggedIn()) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            return
        }

        initViews()
        setupListeners()
    }

    private fun initViews() {
        emailInputLayout = findViewById(R.id.emailInputLayout)
        passwordInputLayout = findViewById(R.id.passwordInputLayout)
        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        signInButton = findViewById(R.id.buttonSignIn)
        googleSignInButton = findViewById(R.id.buttonSignInGoogle)
        forgetPasswordText = findViewById(R.id.textViewForgetPassword)
        signUpText = findViewById(R.id.textViewSignUp)
    }

    private fun setupListeners() {
        emailEditText.setOnFocusChangeListener { _, _ -> emailInputLayout.error = null }
        passwordEditText.setOnFocusChangeListener { _, _ -> passwordInputLayout.error = null }

        signInButton.setOnClickListener {
            if (validateForm()) {
                signInUser()
            }
        }

        googleSignInButton.setOnClickListener {
            Toast.makeText(this, "Google вход в разработке", Toast.LENGTH_SHORT).show()
        }

        forgetPasswordText.setOnClickListener {
            Toast.makeText(this, "Восстановление пароля в разработке", Toast.LENGTH_SHORT).show()
        }

        signUpText.setOnClickListener {
            startActivity(Intent(this, CreateAccount1Activity::class.java))
        }
    }

    private fun validateForm(): Boolean {
        var isValid = true

        val email = emailEditText.text.toString().trim()
        val password = passwordEditText.text.toString()

        emailInputLayout.error = null
        passwordInputLayout.error = null

        if (email.isEmpty()) {
            emailInputLayout.error = "Введите email"
            isValid = false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailInputLayout.error = "Введите корректный email"
            isValid = false
        }

        if (password.isEmpty()) {
            passwordInputLayout.error = "Введите пароль"
            isValid = false
        }

        return isValid
    }

    private fun signInUser() {
        val email = emailEditText.text.toString().trim()
        val password = passwordEditText.text.toString()

        lifecycleScope.launch {
            signInButton.isEnabled = false
            signInButton.text = "Вход..."

            val user = SupabaseHelper.signInUser(email, password, this@SignInActivity)

            if (user != null) {
                // Сохраняем сессию
                sessionManager.createLoginSession(
                    userId = user.id,
                    email = user.email,
                    name = user.name,
                    surname = user.surname,
                    registrationDate = user.registrationDate
                )

                Toast.makeText(this@SignInActivity, "Вход выполнен!", Toast.LENGTH_SHORT).show()
                val intent = Intent(this@SignInActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                signInButton.isEnabled = true
                signInButton.text = "Войти"
            }
        }
    }
}
