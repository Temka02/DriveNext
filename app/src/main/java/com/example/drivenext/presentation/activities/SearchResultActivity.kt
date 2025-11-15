package com.example.drivenext.presentation.activities

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.drivenext.R
import com.example.drivenext.utils.SupabaseHelper
import kotlinx.coroutines.launch

class SearchResultActivity : AppCompatActivity() {

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

    private val cardViews = arrayOf(
        R.id.carCard1, R.id.carCard2, R.id.carCard3, R.id.carCard4
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_result)

        val searchQuery = intent.getStringExtra("SEARCH_QUERY") ?: ""

        setupNavigation()
        setupBackButton()
        loadSearchResults(searchQuery)
    }

    private fun setupBackButton() {
        findViewById<ImageView>(R.id.imageViewArrowLeft).setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun setupNavigation() {
        findViewById<ImageView>(R.id.imageViewHomePage).setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
        }

        findViewById<ImageView>(R.id.imageViewBookmark).setOnClickListener {
            Toast.makeText(this, "Переход в избранное", Toast.LENGTH_SHORT).show()
        }

        findViewById<ImageView>(R.id.imageViewSettingsPage).setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
    }

    private fun loadSearchResults(searchQuery: String) {
        lifecycleScope.launch {
            try {
                val cars = if (searchQuery.isNotEmpty()) {
                    SupabaseHelper.searchCars(searchQuery)
                } else {
                    SupabaseHelper.getCars()
                }

                updateCarCards(cars)
                updateResultsTitle(searchQuery, cars.size)

            } catch (e: Exception) {
                Toast.makeText(this@SearchResultActivity, "Ошибка загрузки данных: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateResultsTitle(searchQuery: String, resultsCount: Int) {
        val resultsTitle = findViewById<TextView>(R.id.textViewSearchResults)

        when {
            searchQuery.isNotEmpty() -> {
                resultsTitle.text = getString(R.string.search_results_with_query, searchQuery, resultsCount)
            }
            resultsCount > 0 -> {
                resultsTitle.text = getString(R.string.all_available_cars, resultsCount)
            }
            else -> {
                resultsTitle.text = getString(R.string.no_cars_found)
            }
        }
    }

    private fun updateCarCard(cardIndex: Int, car: com.example.drivenext.utils.Car) {
        findViewById<TextView>(modelViews[cardIndex]).text = car.model
        findViewById<TextView>(brandViews[cardIndex]).text = car.brand

        findViewById<TextView>(priceViews[cardIndex]).text =
            getString(R.string.price_format, car.price)

        findViewById<TextView>(gearboxViews[cardIndex]).text = car.transmission
        findViewById<TextView>(fuelViews[cardIndex]).text = car.fuelType

        findViewById<android.view.View>(cardViews[cardIndex]).visibility = android.view.View.VISIBLE
    }

    private fun updateCarCards(cars: List<com.example.drivenext.utils.Car>) {
        cars.forEachIndexed { index, car ->
            if (index < 4) {
                updateCarCard(index, car)
            }
        }

        for (i in cars.size until 4) {
            hideCarCard(i)
        }
    }

    private fun hideCarCard(cardIndex: Int) {
        findViewById<android.view.View>(cardViews[cardIndex]).visibility = android.view.View.GONE
    }
}