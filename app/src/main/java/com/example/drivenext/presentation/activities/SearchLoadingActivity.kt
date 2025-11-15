package com.example.drivenext.presentation.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.drivenext.R

class SearchLoadingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_loading)

        val searchQuery = intent.getStringExtra("SEARCH_QUERY") ?: ""

        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, SearchResultActivity::class.java)
            intent.putExtra("SEARCH_QUERY", searchQuery)
            startActivity(intent)
            finish()
        }, 2500)
    }
}