package com.example.drivenext.presentation.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.drivenext.utils.isInternetAvailable

import com.example.drivenext.R

class EntryActivity : AppCompatActivity() {
    private lateinit var signInButton: Button
    private lateinit var signUpButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!isInternetAvailable()) {
            startActivity(Intent(this, NoConnectionActivity::class.java))
            finish()
            return
        }

        setContentView(R.layout.activity_entry)

        signInButton = findViewById(R.id.buttonLogin)
        signUpButton = findViewById(R.id.buttonRegister)

        signInButton.setOnClickListener {
            if (isInternetAvailable()) {
                startActivity(Intent(this, SignInActivity::class.java))
            } else {
                startActivity(Intent(this, NoConnectionActivity::class.java))
            }
        }

        signUpButton.setOnClickListener {
            if (isInternetAvailable()) {
                startActivity(Intent(this, CreateAccount1Activity::class.java))
            } else {
                startActivity(Intent(this, NoConnectionActivity::class.java))
            }
        }
    }
}