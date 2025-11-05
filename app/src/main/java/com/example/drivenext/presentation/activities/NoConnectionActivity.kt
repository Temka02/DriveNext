package com.example.drivenext.presentation.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.drivenext.utils.isInternetAvailable
import com.example.drivenext.R
class NoConnectionActivity : AppCompatActivity() {

    private lateinit var retryButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_no_connection)

        retryButton = findViewById(R.id.buttonTryAgain)

        retryButton.setOnClickListener {
            if (isInternetAvailable()) {
                startActivity(Intent(this, EntryActivity::class.java))
                finish()
            } else {
                retryButton.isEnabled = false
                retryButton.postDelayed({
                    retryButton.isEnabled = true
                }, 1000)
            }
        }
    }
}