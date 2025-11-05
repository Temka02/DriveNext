package com.example.drivenext.presentation.activities

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.example.drivenext.R

class CreateAccount1Activity : AppCompatActivity() {
    private lateinit var backImage: ImageView
    private lateinit var emailInputLayout: TextInputLayout
    private lateinit var passwordInputLayout: TextInputLayout
    private lateinit var passwordRepeatLayout: TextInputLayout
    private lateinit var emailEditText: TextInputEditText
    private lateinit var passwordEditText: TextInputEditText
    private lateinit var passwordRepeatEditText: TextInputEditText
    private lateinit var checkBoxPolicy: CheckBox
    private lateinit var checkBoxErrorText: TextView
    private lateinit var continueButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_account1)

        backImage = findViewById(R.id.imageViewArrowLeft)
        emailInputLayout = findViewById(R.id.emailInputLayout)
        passwordInputLayout = findViewById(R.id.passwordInputLayout)
        passwordRepeatLayout = findViewById(R.id.passwordRepeatLayout)
        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        passwordRepeatEditText = findViewById(R.id.passwordRepeatText)
        checkBoxPolicy = findViewById(R.id.checkBoxPolicy)
        checkBoxErrorText = findViewById(R.id.checkBoxErrorText)
        continueButton = findViewById(R.id.buttonContinue)

        backImage.setOnClickListener { finish() }

        emailEditText.setOnFocusChangeListener { _, _ -> emailInputLayout.error = null }
        passwordEditText.setOnFocusChangeListener { _, _ -> passwordInputLayout.error = null }
        passwordRepeatEditText.setOnFocusChangeListener { _, _ -> passwordRepeatLayout.error = null }
        checkBoxPolicy.setOnCheckedChangeListener { _, _ -> checkBoxErrorText.visibility = android.view.View.GONE }

        continueButton.setOnClickListener {
            if (validateForm()) {
                startActivity(Intent(this, CreateAccount2Activity::class.java))
            }
        }
    }

    private fun validateForm(): Boolean {
        var isValid = true

        val email = emailEditText.text.toString().trim()
        val password = passwordEditText.text.toString()
        val repeatPassword = passwordRepeatEditText.text.toString()
        val isPolicyChecked = checkBoxPolicy.isChecked

        emailInputLayout.error = null
        passwordInputLayout.error = null
        passwordRepeatLayout.error = null
        checkBoxErrorText.visibility = android.view.View.GONE

        if (email.isEmpty()) {
            emailInputLayout.error = "Поле обязательно для заполнения."
            isValid = false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailInputLayout.error = "Введите корректный адрес электронной почты."
            isValid = false
        }

        if (password.isEmpty()) {
            passwordInputLayout.error = "Поле обязательно для заполнения."
            isValid = false
        } else if (password.length < 8) {
            passwordInputLayout.error = "Пароль должен содержать минимум 8 символов."
            isValid = false
        }

        if (repeatPassword.isEmpty()) {
            passwordRepeatLayout.error = "Поле обязательно для заполнения."
            isValid = false
        } else if (password != repeatPassword) {
            passwordRepeatLayout.error = "Пароли не совпадают."
            isValid = false
        }

        if (!isPolicyChecked) {
            checkBoxErrorText.text = "Необходимо согласиться с условиями обслуживания и политикой конфиденциальности."
            checkBoxErrorText.visibility = android.view.View.VISIBLE
            isValid = false
        }

        return isValid
    }
}