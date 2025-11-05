package com.example.drivenext.presentation.activities

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.drivenext.R
class SignInActivity : AppCompatActivity() {
    private lateinit var signUpText: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        signUpText = findViewById(R.id.textViewSignUp)

        signUpText.setOnClickListener {
            startActivity(Intent(this, CreateAccount1Activity::class.java))
        }
    }
}
