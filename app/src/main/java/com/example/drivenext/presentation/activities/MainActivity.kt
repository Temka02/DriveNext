package com.example.drivenext.presentation.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.drivenext.R
import com.example.drivenext.utils.SupabaseHelper
import kotlinx.coroutines.launch
import com.google.android.material.textfield.TextInputEditText
import com.example.drivenext.utils.SessionManager

class MainActivity : AppCompatActivity() {

    private lateinit var sessionManager: SessionManager

    // Массивы с ID всех текстовых полей для каждой карточки
    private val modelViews = arrayOf(
        R.id.textViewCarModel1, R.id.textViewCarModel2,
        R.id.textViewCarModel3, R.id.textViewCarModel4
    )

    private val brandViews = arrayOf(
        R.id.textViewCarBrand1, R.id.textViewCarBrand2,
        R.id.textViewCarBrand3, R.id.textViewCarBrand4
    )

    private val priceViews = arrayOf(
        R.id.textViewPrice1, R.id.textViewPrice2,
        R.id.textViewPrice3, R.id.textViewPrice4
    )

    private val gearboxViews = arrayOf(
        R.id.textViewGearbox1, R.id.textViewGearbox2,
        R.id.textViewGearbox3, R.id.textViewGearbox4
    )

    private val fuelViews = arrayOf(
        R.id.textViewFuel1, R.id.textViewFuel2,
        R.id.textViewFuel3, R.id.textViewFuel4
    )

    // Массив с ID карточек
    private val cardViews = arrayOf(
        R.id.carCard1, R.id.carCard2, R.id.carCard3, R.id.carCard4
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sessionManager = SessionManager(this)

        if (!sessionManager.isLoggedIn()) {
            startActivity(Intent(this, SignInActivity::class.java))
            finish()
            return
        }

        setupNavigation()
        setupSearch()
        loadCars()
    }

    private fun setupSearch() {
        val searchEditText = findViewById<TextInputEditText>(R.id.brandText)
        val searchImageView = findViewById<ImageView>(R.id.search_cars)

        searchEditText.setOnEditorActionListener { v, actionId, event ->
            if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_SEARCH) {
                performSearch()
                true
            } else {
                false
            }
        }

        searchImageView.setOnClickListener {
            performSearch()
        }
    }

    private fun performSearch() {
        val searchQuery = findViewById<TextInputEditText>(R.id.brandText).text.toString().trim()

        if (searchQuery.isNotEmpty()) {
            val intent = Intent(this, SearchLoadingActivity::class.java)
            intent.putExtra("SEARCH_QUERY", searchQuery)
            startActivity(intent)
        } else {
            Toast.makeText(this, "Введите запрос для поиска", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupNavigation() {
        findViewById<ImageView>(R.id.imageViewHomePage).setOnClickListener {
        }

        findViewById<ImageView>(R.id.imageViewBookmark).setOnClickListener {
            Toast.makeText(this, "Переход в избранное", Toast.LENGTH_SHORT).show()
        }

        findViewById<ImageView>(R.id.imageViewSettingsPage).setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
    }

    private fun loadCars() {
        lifecycleScope.launch {
            try {
                val cars = SupabaseHelper.getCars()
                updateCarCards(cars)
            } catch (e: Exception) {
                Toast.makeText(this@MainActivity, "Ошибка загрузки данных", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateCarCards(cars: List<com.example.drivenext.utils.Car>) {
        // Обновляем только те карточки, для которых есть данные
        cars.forEachIndexed { index, car ->
            if (index < 4) {
                updateCarCard(index, car)
            }
        }

        // Скрываем оставшиеся карточки
        for (i in cars.size until 4) {
            hideCarCard(i)
        }
    }

    private fun updateCarCard(cardIndex: Int, car: com.example.drivenext.utils.Car) {
        findViewById<TextView>(modelViews[cardIndex]).text = car.model
        findViewById<TextView>(brandViews[cardIndex]).text = car.brand

        findViewById<TextView>(priceViews[cardIndex]).text =
            getString(R.string.price_format, car.price)

        findViewById<TextView>(gearboxViews[cardIndex]).text = car.transmission
        findViewById<TextView>(fuelViews[cardIndex]).text = car.fuelType

        findViewById<View>(cardViews[cardIndex]).visibility = View.VISIBLE
    }

    private fun hideCarCard(cardIndex: Int) {
        findViewById<View>(cardViews[cardIndex]).visibility = View.GONE
    }
}